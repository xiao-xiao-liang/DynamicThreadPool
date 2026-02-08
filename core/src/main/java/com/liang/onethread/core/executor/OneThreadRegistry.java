package com.liang.onethread.core.executor;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 动态线程池注册中心
 * <p>
 * 该类是动态线程池框架的核心管理组件，提供线程池的注册、查询和统一管理功能。
 * 通过静态方法和线程安全的 ConcurrentHashMap 实现全局单例式的线程池管理。
 * <p>
 * 核心功能：
 * <ul>
 *     <li><b>注册线程池</b> - 将线程池及其配置注册到管理器中，便于统一管理</li>
 *     <li><b>检索线程池</b> - 根据线程池唯一标识快速获取对应的线程池实例</li>
 *     <li><b>遍历所有线程池</b> - 获取所有已注册的线程池集合，用于监控和批量操作</li>
 * </ul>
 * <p>
 * 设计特点：
 * <ul>
 *     <li>使用 {@link ConcurrentHashMap} 保证线程安全</li>
 *     <li>采用静态方法，无需创建实例即可使用</li>
 *     <li>线程池以 {@link ThreadPoolExecutorHolder} 形式存储，包含完整的配置信息</li>
 * </ul>
 * <p>
 * 典型使用场景：
 * <pre>{@code
 * // 注册线程池
 * OneThreadRegistry.putHolder("orderPool", executor, properties);
 *
 * // 获取线程池
 * ThreadPoolExecutorHolder holder = OneThreadRegistry.getHolder("orderPool");
 * ThreadPoolExecutor executor = holder.getExecutor();
 *
 * // 遍历所有线程池进行监控
 * OneThreadRegistry.getAllHolders().forEach(h -> {
 *     System.out.println(h.getThreadPoolId() + " - " + h.getExecutor().getActiveCount());
 * });
 * }</pre>
 *
 * @see ThreadPoolExecutorHolder
 * @see ThreadPoolExecutorProperties
 */
public class OneThreadRegistry {

    /**
     * KEY：线程池唯一标识 VALUE：线程池执行器持有者
     */
    private static final Map<String, ThreadPoolExecutorHolder> HOLDER_MAP = new ConcurrentHashMap<>();

    /**
     * 注册线程池到管理器
     *
     * @param threadPoolId 线程池唯一标识
     * @param executor     线程池执行器实例
     * @param properties   线程池参数配置
     */
    public static void putHolder(String threadPoolId, ThreadPoolExecutor executor, ThreadPoolExecutorProperties properties) {
        ThreadPoolExecutorHolder holder = new ThreadPoolExecutorHolder(threadPoolId, executor, properties);
        HOLDER_MAP.put(threadPoolId, holder);
    }

    /**
     * 根据线程池 ID 获取对应的线程池包装对象
     *
     * @param threadPoolId 线程池唯一标识
     * @return 线程池持有者对象
     * @throws RuntimeException 如果未找到对应线程池
     */
    public static ThreadPoolExecutorHolder getHolder(String threadPoolId) {
        return Optional.ofNullable(HOLDER_MAP.get(threadPoolId))
                .orElseThrow(() -> new RuntimeException("No thread pool executor found for id: " + threadPoolId));
    }

    /**
     * 获取所有线程池集合
     *
     * @return 线程池集合
     */
    public static Collection<ThreadPoolExecutorHolder> getAllHolders() {
        return HOLDER_MAP.values();
    }
}
