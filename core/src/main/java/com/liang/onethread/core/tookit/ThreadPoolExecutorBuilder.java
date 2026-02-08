package com.liang.onethread.core.tookit;

import cn.hutool.core.lang.Assert;
import com.liang.onethread.core.executor.OneThreadExecutor;
import com.liang.onethread.core.executor.support.BlockingQueueTypeEnum;
import lombok.Getter;

import java.util.Optional;
import java.util.concurrent.*;

/**
 * 动态线程池构建器
 * <p>
 * 采用建造者模式（Builder Pattern）构建线程池实例，提供流畅的链式 API，
 * 简化 {@link java.util.concurrent.ThreadPoolExecutor} 的创建过程。
 * 支持构建标准线程池和动态线程池两种类型。
 * <p>
 * 核心特性：
 * <ul>
 *     <li><b>链式调用</b> - 所有配置方法返回 this，支持流畅的链式配置</li>
 *     <li><b>默认值优化</b> - 提供合理的默认参数，降低使用门槛</li>
 *     <li><b>动态线程池支持</b> - 通过 {@link #dynamicPool()} 方法创建可动态调参的线程池</li>
 *     <li><b>多种队列类型</b> - 支持 LinkedBlockingQueue、ArrayBlockingQueue 等多种阻塞队列</li>
 * </ul>
 * <p>
 * 默认参数：
 * <ul>
 *     <li>核心线程数：CPU 核心数</li>
 *     <li>最大线程数：核心线程数 × 1.5</li>
 *     <li>队列类型：LinkedBlockingQueue</li>
 *     <li>队列容量：4096</li>
 *     <li>拒绝策略：AbortPolicy</li>
 *     <li>空闲线程存活时间：30 秒</li>
 * </ul>
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 构建标准线程池
 * ThreadPoolExecutor executor = ThreadPoolExecutorBuilder.builder()
 *     .corePoolSize(4)
 *     .maximumPoolSize(8)
 *     .workQueueCapacity(1024)
 *     .threadFactory("order-pool-")
 *     .build();
 *
 * // 构建动态线程池
 * ThreadPoolExecutor dynamicExecutor = ThreadPoolExecutorBuilder.builder()
 *     .dynamicPool()
 *     .threadPoolId("orderPool")
 *     .corePoolSize(4)
 *     .maximumPoolSize(16)
 *     .threadFactory("dynamic-order-", true)
 *     .rejectedHandler(new ThreadPoolExecutor.CallerRunsPolicy())
 *     .build();
 * }</pre>
 *
 * @see OneThreadExecutor
 * @see ThreadFactoryBuilder
 */
@Getter
public class ThreadPoolExecutorBuilder {

    /**
     * 线程池唯一标识
     */
    private String threadPoolId;

    /**
     * 核心线程数
     */
    private Integer corePoolSize = Runtime.getRuntime().availableProcessors();

    /**
     * 最大线程数
     */
    private Integer maximumPoolSize = corePoolSize + (corePoolSize >> 1);

    /**
     * 阻塞队列类型
     */
    private BlockingQueueTypeEnum workQueueType = BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE;

    /**
     * 队列容量
     */
    private Integer workQueueCapacity = 4096;

    /**
     * 拒绝策略
     */
    private RejectedExecutionHandler rejectedHandler = new ThreadPoolExecutor.AbortPolicy();

    /**
     * 线程工厂
     */
    private ThreadFactory threadFactory;

    /**
     * 空闲线程存活时间
     */
    private Long keepAliveTime = 30000L;

    /**
     * 是否允许核心线程超时
     */
    private boolean allowCoreThreadTimeOut = false;

    /**
     * 动态线程池标识
     */
    private boolean dynamicPool;

    /**
     * 最大等待时间
     */
    private long awaitTerminationMillis = 0L;

    /**
     * 设置构建线程池为动态线程池
     */
    public ThreadPoolExecutorBuilder dynamicPool() {
        this.dynamicPool = true;
        return this;
    }

    /**
     * 设置线程池唯一标识
     *
     * @param threadPoolId 线程池唯一标识
     */
    public ThreadPoolExecutorBuilder threadPoolId(String threadPoolId) {
        this.threadPoolId = threadPoolId;
        return this;
    }

    /**
     * 设置核心线程数
     *
     * @param corePoolSize 核心线程数
     */
    public ThreadPoolExecutorBuilder corePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    /**
     * 设置最大线程数
     *
     * @param maximumPoolSize 最大线程数
     */
    public ThreadPoolExecutorBuilder maximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
        return this;
    }

    /**
     * 设置阻塞队列容量
     *
     * @param workQueueCapacity 阻塞队列容量
     */
    public ThreadPoolExecutorBuilder workQueueCapacity(int workQueueCapacity) {
        this.workQueueCapacity = workQueueCapacity;
        return this;
    }

    /**
     * 设置阻塞队列类型
     *
     * @param workQueueType 阻塞队列类型（如 LinkedBlockingQueue、ArrayBlockingQueue）
     */
    public ThreadPoolExecutorBuilder workQueueType(BlockingQueueTypeEnum workQueueType) {
        this.workQueueType = workQueueType;
        return this;
    }

    /**
     * 设置线程工厂
     *
     * @param namePrefix 线程名前缀，如："onethread-"; 线程名形如：onethread-1
     */
    public ThreadPoolExecutorBuilder threadFactory(String namePrefix) {
        this.threadFactory = ThreadFactoryBuilder.builder()
                .namePrefix(namePrefix)
                .build();
        return this;
    }

    /**
     * 设置线程工厂
     *
     * @param threadFactory 线程工厂
     */
    public ThreadPoolExecutorBuilder threadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    /**
     * 快速设置线程工厂，封装常用参数以降低构建门槛
     * <p>
     * 出于实用主义，仅暴露常用的 namePrefix 和 daemon 参数
     * 若对线程优先级、异常处理器等有更细颗粒度需求，可选择扩展重载构造链
     *
     * @param namePrefix 线程名前缀（如："onethread-"），最终线程名为：onethread-1、onethread-2...
     * @param daemon     是否为守护线程（true 表示不会阻止 JVM 退出）
     */
    public ThreadPoolExecutorBuilder threadFactory(String namePrefix, Boolean daemon) {
        this.threadFactory = ThreadFactoryBuilder.builder()
                .namePrefix(namePrefix)
                .daemon(daemon)
                .build();
        return this;
    }

    /**
     * 设置拒绝策略
     *
     * @param rejectedHandler 拒绝策略（如 AbortPolicy、CallerRunsPolicy）
     */
    public ThreadPoolExecutorBuilder rejectedHandler(RejectedExecutionHandler rejectedHandler) {
        this.rejectedHandler = rejectedHandler;
        return this;
    }

    /**
     * 设置空闲线程存活时间
     *
     * @param keepAliveTime 存活时间，单位：秒
     */
    public ThreadPoolExecutorBuilder keepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    /**
     * 设置是否允许核心线程超时
     *
     * @param allowCoreThreadTimeOut 是否允许核心线程超时
     */
    public ThreadPoolExecutorBuilder allowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        return this;
    }

    /**
     * 设置最大等待时间
     *
     * @param awaitTerminationMillis 最大等待时间
     */
    public ThreadPoolExecutorBuilder awaitTerminationMillis(long awaitTerminationMillis) {
        this.awaitTerminationMillis = awaitTerminationMillis;
        return this;
    }

    /**
     * 创建线程池构建器
     */
    public static ThreadPoolExecutorBuilder builder() {
        return new ThreadPoolExecutorBuilder();
    }

    /**
     * 构建线程池实例
     */
    public ThreadPoolExecutor build() {
        BlockingQueue<Runnable> blockingQueue = BlockingQueueTypeEnum.createBlockingQueue(workQueueType.getName(), workQueueCapacity);
        RejectedExecutionHandler rejectedHandler = Optional.of(this.rejectedHandler)
                .orElseGet(ThreadPoolExecutor.AbortPolicy::new);

        Assert.notNull(threadFactory, "The thread factory cannot be null.");

        ThreadPoolExecutor threadPoolExecutor = getExecutor(blockingQueue, rejectedHandler);

        threadPoolExecutor.allowCoreThreadTimeOut(allowCoreThreadTimeOut);
        return threadPoolExecutor;
    }

    private ThreadPoolExecutor getExecutor(BlockingQueue<Runnable> blockingQueue, RejectedExecutionHandler rejectedHandler) {
        ThreadPoolExecutor threadPoolExecutor;
        if (dynamicPool)
            threadPoolExecutor = new OneThreadExecutor(
                    threadPoolId, corePoolSize,
                    maximumPoolSize, keepAliveTime,
                    TimeUnit.SECONDS, blockingQueue,
                    threadFactory, rejectedHandler
            );
        else
            threadPoolExecutor = new ThreadPoolExecutor(
                    corePoolSize,
                    maximumPoolSize,
                    keepAliveTime,
                    TimeUnit.SECONDS,
                    blockingQueue,
                    threadFactory,
                    rejectedHandler
            );
        return threadPoolExecutor;
    }
}
