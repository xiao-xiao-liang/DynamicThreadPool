package com.liang.onethread.springbase;

import java.lang.annotation.*;

/**
 * 动态线程池标记注解
 *
 * <p>标记在 {@link com.liang.onethread.core.executor.OneThreadExecutor} 的子类上，
 * 表示该线程池实例需要被纳入动态线程池管理体系。被此注解标记的 Bean 将在 Spring 容器初始化后
 * 由 {@link com.liang.onethread.springbase.support.OneThreadBeanPostProcessor} 自动发现、
 * 完成属性填充并注册到 {@link com.liang.onethread.core.executor.OneThreadRegistry}。</p>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DynamicThreadPool {
}
