package com.liang.onethread.core.executor;

import lombok.Getter;
import lombok.NonNull;

import java.util.concurrent.*;

/**
 * 增强的线程池 | 功能：动态变更参数、告警、受监控
 */
public class OneThreadExecutor extends ThreadPoolExecutor {

    /**
     * 线程池标识，用来动态变更参数等
     */
    @Getter
    private final String threadPoolId;

    /**
     * 使用给定的初始参数创建一个新的 {@code ExtensibleThreadPoolExecutor}。
     *
     * @param threadPoolId           线程池标识
     * @param corePoolSize           线程池中保持的核心线程数。
     *                               除非设置了 {@code allowCoreThreadTimeOut}，
     *                               否则即使这些线程处于空闲状态也会被保留
     * @param maximumPoolSize        线程池中允许创建的最大线程数
     * @param keepAliveTime          当线程数大于核心线程数时，
     *                               多余空闲线程在被终止之前等待新任务的最长时间
     * @param unit                   {@code keepAliveTime} 参数的时间单位
     * @param workQueue              用于在任务执行前保存任务的阻塞队列。
     *                               该队列仅保存通过 {@code execute} 方法提交的
     *                               {@code Runnable} 任务
     * @param threadFactory          用于创建新线程的线程工厂
     * @param handler                当线程数达到最大值且工作队列已满时，
     *                               用于处理被拒绝任务的处理器
     * @throws IllegalArgumentException 如果满足以下任一条件：<br>
     *                                  {@code corePoolSize < 0}<br>
     *                                  {@code keepAliveTime < 0}<br>
     *                                  {@code maximumPoolSize <= 0}<br>
     *                                  {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException     如果 {@code workQueue}、{@code unit}、
     *                                  {@code threadFactory} 或 {@code handler} 为 {@code null}
     */
    public OneThreadExecutor(
            @NonNull String threadPoolId,
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            @NonNull TimeUnit unit,
            @NonNull BlockingQueue<Runnable> workQueue,
            @NonNull ThreadFactory threadFactory,
            @NonNull RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);

        // 设置动态线程池扩展属性，线程池 ID 标识
        this.threadPoolId = threadPoolId;
    }
}
