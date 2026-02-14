package com.liang.onethread.springbase.support;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import com.liang.onethread.core.config.BootstrapConfigProperties;
import com.liang.onethread.core.executor.OneThreadExecutor;
import com.liang.onethread.core.executor.OneThreadRegistry;
import com.liang.onethread.core.executor.ThreadPoolExecutorProperties;
import com.liang.onethread.core.executor.support.BlockingQueueTypeEnum;
import com.liang.onethread.core.executor.support.RejectedPolicyTypeEnum;
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

    private final BootstrapConfigProperties properties;

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

            // 构建初始属性并注册
            ThreadPoolExecutorProperties executorProperties = properties.getExecutors()
                    .stream()
                    .filter(item -> Objects.equals(oneThreadExecutor.getThreadPoolId(), item.getThreadPoolId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("The thread pool id does not exist in the configuration."));

            overrideLocalThreadPoolConfig(executorProperties, oneThreadExecutor);
            
            // 注册到动态线程池注册器
            OneThreadRegistry.putHolder(
                    oneThreadExecutor.getThreadPoolId(),
                    oneThreadExecutor,
                    executorProperties
            );
        } catch (Exception ex) {
            log.error("Failed to register dynamic thread pool for bean: [{}]", beanName, ex);
        }

        return bean;
    }

    private void overrideLocalThreadPoolConfig(ThreadPoolExecutorProperties executorProperties, OneThreadExecutor oneThreadExecutor) {
        Integer remoteCorePoolSize = executorProperties.getCorePoolSize();
        Integer remoteMaximumPoolSize = executorProperties.getMaximumPoolSize();
        Assert.isTrue(remoteCorePoolSize <= remoteMaximumPoolSize, "remoteCorePoolSize must be smaller than remoteMaximumPoolSize.");

        int originMaximumPoolSize = oneThreadExecutor.getMaximumPoolSize();
        if (remoteMaximumPoolSize > originMaximumPoolSize) {
            executorProperties.setMaximumPoolSize(remoteMaximumPoolSize);
            executorProperties.setCorePoolSize(remoteCorePoolSize);
        } else {
            executorProperties.setCorePoolSize(remoteCorePoolSize);
            executorProperties.setMaximumPoolSize(remoteMaximumPoolSize);
        }

        // 阻塞队列没有常规 set 方法，通过反射赋值
        var workQueue = BlockingQueueTypeEnum.createBlockingQueue(executorProperties.getWorkQueue(), executorProperties.getQueueCapacity());
        // Java 9+ 的模块系统（JPMS）默认禁止通过反射访问 JDK 内部 API 的私有字段，所以需要配置开放反射权限
        // 在启动命令中增加以下参数，显式开放 java.util.concurrent 包
        // IDE 中通过在 VM options 中添加参数：--add-opens=java.base/java.util.concurrent=ALL-UNNAMED
        // 部署的时候，在启动脚本（如 java -jar 命令）中加入该参数：java -jar --add-opens=java.base/java.util.concurrent=ALL-UNNAMED your-app.jar
        ReflectUtil.setFieldValue(oneThreadExecutor, "workQueue", workQueue);

        // 赋值动态线程池其他核心参数
        oneThreadExecutor.setKeepAliveTime(executorProperties.getKeepAliveTime(), TimeUnit.SECONDS);
        oneThreadExecutor.allowCoreThreadTimeOut(executorProperties.getAllowCoreThreadTimeOut());
        oneThreadExecutor.setRejectedExecutionHandler(RejectedPolicyTypeEnum.createPolicy(executorProperties.getRejectedHandler()));
    }
}
