package com.liang.onethread.springbase.support;

import com.liang.onethread.core.executor.OneThreadExecutor;
import com.liang.onethread.core.executor.OneThreadRegistry;
import com.liang.onethread.core.executor.ThreadPoolExecutorProperties;
import com.liang.onethread.springbase.DynamicThreadPool;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 动态线程池 Bean 后置处理器
 *
 * <p>在 Spring Bean 初始化完成后，自动扫描所有实现了 {@link OneThreadExecutor} 并标注了
 * {@link DynamicThreadPool @DynamicThreadPool} 注解的 Bean，提取其运行时参数构建
 * {@link ThreadPoolExecutorProperties} 属性快照，最终注册到全局注册中心
 * {@link OneThreadRegistry}，从而纳入动态线程池管理。</p>
 *
 * <h4>处理流程</h4>
 * <ol>
 *     <li>判断 Bean 是否为 {@link OneThreadExecutor} 实例</li>
 *     <li>通过 {@link AopProxyUtils} 获取目标类，检查 {@link DynamicThreadPool} 注解</li>
 *     <li>构建线程池属性快照并注册到 {@link OneThreadRegistry}</li>
 * </ol>
 */
@Slf4j
@RequiredArgsConstructor
public class OneThreadBeanPostProcessor implements BeanPostProcessor {

    /**
     * 在 Bean 初始化完成后进行动态线程池的识别与注册。
     *
     * @param bean     当前正在初始化的 Bean 实例
     * @param beanName Bean 在 Spring 容器中的名称
     * @return 原始 Bean 实例（不做替换）
     * @throws BeansException 如果处理过程中发生 Spring 容器异常
     */
    @Nullable
    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (!(bean instanceof OneThreadExecutor oneThreadExecutor))
            return bean;

        // 获取目标类（处理 AOP 代理情况，确保能拿到注解），并检查类上是否存在注解
        Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);
        DynamicThreadPool annotation = AnnotationUtils.findAnnotation(clazz, DynamicThreadPool.class);
        if (Objects.isNull(annotation))
            return bean;

        try {
            log.info("Registering dynamic thread pool: [{}], ID: [{}]", beanName, oneThreadExecutor.getThreadPoolId());

            // TODO: 需要从配置中心读取动态线程池配置并对线程池进行赋值

            // 构建初始属性并注册
            ThreadPoolExecutorProperties defaultProperties = buildDefaultExecutorProperties(oneThreadExecutor);

            // 注册到动态线程池注册器
            OneThreadRegistry.putHolder(
                    oneThreadExecutor.getThreadPoolId(),
                    oneThreadExecutor,
                    defaultProperties
            );
        } catch (Exception ex) {
            log.error("Failed to register dynamic thread pool for bean: [{}]", beanName, ex);
        }

        return bean;
    }

    /**
     * 基于线程池当前运行时状态，构建其默认属性快照。
     *
     * @param executor 目标线程池实例
     * @return 包含核心线程数、最大线程数、队列类型及容量等参数的属性快照
     */
    private ThreadPoolExecutorProperties buildDefaultExecutorProperties(OneThreadExecutor executor) {
        BlockingQueue<Runnable> blockingQueue = executor.getQueue();

        // 队列相关参数计算
        int queueSize = blockingQueue.size();
        String queueType = blockingQueue.getClass().getSimpleName();
        int remainingCapacity = blockingQueue.remainingCapacity();
        int queueCapacity = queueSize + remainingCapacity;

        return new ThreadPoolExecutorProperties()
                .setCorePoolSize(executor.getCorePoolSize())
                .setMaximumPoolSize(executor.getMaximumPoolSize())
                .setAllowCoreThreadTimeOut(executor.allowsCoreThreadTimeOut())
                .setKeepAliveTime(executor.getKeepAliveTime(TimeUnit.SECONDS))
                .setWorkQueue(queueType)
                .setQueueCapacity(queueCapacity)
                .setRejectedHandler(executor.getRejectedExecutionHandler().getClass().getSimpleName())
                .setThreadPoolId(executor.getThreadPoolId());
    }
}
