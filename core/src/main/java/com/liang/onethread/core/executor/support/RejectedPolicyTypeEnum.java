package com.liang.onethread.core.executor.support;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 拒绝策略类型枚举
 * <p>
 * 定义线程池在无法接受新任务时的处理策略。当线程池的工作队列已满且所有线程都在忙碌时，
 * 新提交的任务将根据配置的拒绝策略进行处理。
 * <p>
 * 支持的拒绝策略：
 * <ul>
 *     <li><b>CallerRunsPolicy</b> - 由调用线程执行任务，降低新任务提交速度，实现优雅降级</li>
 *     <li><b>AbortPolicy</b> - 直接抛出 {@link java.util.concurrent.RejectedExecutionException}，拒绝任务</li>
 *     <li><b>DiscardPolicy</b> - 静默丢弃被拒绝的任务，不抛出异常</li>
 *     <li><b>DiscardOldestPolicy</b> - 丢弃队列中最老的任务，然后尝试重新提交新任务</li>
 * </ul>
 * <p>
 * 选择建议：
 * <ul>
 *     <li>对任务必须执行的场景，使用 CallerRunsPolicy</li>
 *     <li>对需要明确感知任务被拒绝的场景，使用 AbortPolicy</li>
 *     <li>对可容忍任务丢失的场景，使用 DiscardPolicy 或 DiscardOldestPolicy</li>
 * </ul>
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 根据策略名称获取拒绝策略实例
 * RejectedExecutionHandler handler = RejectedPolicyTypeEnum.createPolicy("CallerRunsPolicy");
 *
 * // 在构建器中使用
 * ThreadPoolExecutorBuilder.builder()
 *     .rejectedHandler(RejectedPolicyTypeEnum.CALLER_RUNS_POLICY.getRejectedHandler())
 *     .build();
 * }</pre>
 *
 * @see java.util.concurrent.RejectedExecutionHandler
 */
@Getter
@AllArgsConstructor
public enum RejectedPolicyTypeEnum {

    /**
     * {@link ThreadPoolExecutor.CallerRunsPolicy}
     */
    CALLER_RUNS_POLICY("CallerRunsPolicy", new ThreadPoolExecutor.CallerRunsPolicy()),

    /**
     * {@link ThreadPoolExecutor.AbortPolicy}
     */
    ABORT_POLICY("AbortPolicy", new ThreadPoolExecutor.AbortPolicy()),

    /**
     * {@link ThreadPoolExecutor.DiscardPolicy}
     */
    DISCARD_POLICY("DiscardPolicy", new ThreadPoolExecutor.DiscardPolicy()),

    /**
     * {@link ThreadPoolExecutor.DiscardOldestPolicy}
     */
    DISCARD_OLDEST_POLICY("DiscardOldestPolicy", new ThreadPoolExecutor.DiscardOldestPolicy());

    private final String name;
    private final RejectedExecutionHandler rejectedHandler;

    private static final Map<String, RejectedPolicyTypeEnum> NAME_TO_ENUM_MAP;

    static {
        final RejectedPolicyTypeEnum[] values = RejectedPolicyTypeEnum.values();
        NAME_TO_ENUM_MAP = new HashMap<>(values.length);
        for (RejectedPolicyTypeEnum v : values)
            NAME_TO_ENUM_MAP.put(v.name, v);
    }

    /**
     * 根据给定的 {@link RejectedPolicyTypeEnum#name RejectedPolicyTypeEnum.name}
     * 创建一个 {@link RejectedExecutionHandler}。
     *
     * @param rejectedPolicyName {@link RejectedPolicyTypeEnum#name RejectedPolicyTypeEnum.name}
     * @return 对应的 {@link RejectedExecutionHandler} 实例
     * @throws IllegalArgumentException 如果未找到匹配的拒绝策略类型
     */
    public static RejectedExecutionHandler createPolicy(String rejectedPolicyName) {
        RejectedPolicyTypeEnum rejectedPolicyTypeEnum = NAME_TO_ENUM_MAP.get(rejectedPolicyName);
        if (rejectedPolicyTypeEnum != null)
            return rejectedPolicyTypeEnum.rejectedHandler;

        throw new IllegalArgumentException("No matching type of rejected execution was found: " + rejectedPolicyName);
    }
}
