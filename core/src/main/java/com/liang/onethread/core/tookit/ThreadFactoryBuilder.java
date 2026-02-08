package com.liang.onethread.core.tookit;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工厂建造者
 * <p>
 * 采用建造者模式构建自定义 {@link ThreadFactory}，支持设置线程名前缀、
 * 守护线程标志、线程优先级、未捕获异常处理器等属性。
 * <p>
 * 核心功能：
 * <ul>
 *     <li><b>线程命名</b> - 自定义线程名前缀，生成形如 "pool-1"、"pool-2" 的线程名</li>
 *     <li><b>守护线程</b> - 设置创建的线程是否为守护线程</li>
 *     <li><b>线程优先级</b> - 设置线程优先级（1~10）</li>
 *     <li><b>异常处理</b> - 设置未捕获异常处理器，统一处理线程异常</li>
 * </ul>
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 基础用法：只设置线程名前缀
 * ThreadFactory factory = ThreadFactoryBuilder.builder()
 *     .namePrefix("order-worker-")
 *     .build();
 *
 * // 完整配置
 * ThreadFactory factory = ThreadFactoryBuilder.builder()
 *     .namePrefix("async-task-")
 *     .daemon(true)
 *     .priority(Thread.NORM_PRIORITY)
 *     .uncaughtExceptionHandler((t, e) -> log.error("Thread {} error", t.getName(), e))
 *     .build();
 * }</pre>
 *
 * @see ThreadPoolExecutorBuilder
 */
public class ThreadFactoryBuilder {

    /**
     * 基础线程工厂，默认使用 Executors.defaultThreadFactory()
     */
    private ThreadFactory backingThreadFactory;

    /**
     * 线程名前缀，如："onethread-"; 线程名形如：onethread-1
     */
    private String namePrefix;

    /**
     * 是否守护线程，默认 false
     */
    private Boolean daemon;

    /**
     * 线程优先级，1~10
     */
    private Integer priority;

    /**
     * 未捕获异常处理器
     */
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public ThreadFactoryBuilder threadFactory(ThreadFactory backingThreadFactory) {
        this.backingThreadFactory = backingThreadFactory;
        return this;
    }

    public ThreadFactoryBuilder namePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
        return this;
    }

    public ThreadFactoryBuilder daemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }

    public ThreadFactoryBuilder priority(int priority) {
        if (priority < Thread.MIN_PRIORITY || priority > Thread.MAX_PRIORITY) {
            throw new IllegalArgumentException("The thread priority must be between 1 and 10.");
        }
        this.priority = priority;
        return this;
    }

    public ThreadFactoryBuilder uncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
        this.uncaughtExceptionHandler = handler;
        return this;
    }

    /**
     * 创建 ThreadFactoryBuilder 实例
     */
    public static ThreadFactoryBuilder builder() {
        return new ThreadFactoryBuilder();
    }

    /**
     * 构建线程工厂实例
     */
    public ThreadFactory build() {
        final ThreadFactory factory = backingThreadFactory != null ? backingThreadFactory : Executors.defaultThreadFactory();
        
        Assert.notEmpty(namePrefix, "The thread name prefix cannot be empty or an empty string.");
        
        final AtomicInteger count = StrUtil.isNotBlank(namePrefix) ? new AtomicInteger(0) : null;

        return runnable -> {
            Thread thread = factory.newThread(runnable);
            if (count != null)
                thread.setName(namePrefix + count.getAndIncrement());

            if (daemon != null)
                thread.setDaemon(daemon);
            
            if (priority != null)
                thread.setPriority(priority);

            if (uncaughtExceptionHandler != null)
                thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);

            return thread;
        };
    }
}
