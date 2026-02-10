package com.liang.onethread.springbase.config;

import com.liang.onethread.springbase.support.OneThreadBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OneThread 动态线程池基础自动配置类
 *
 * <p>提供动态线程池框架与 Spring 容器集成所需的核心基础设施 Bean 注册，
 * 包括 {@link OneThreadBeanPostProcessor} 等后置处理器的装配。</p>
 *
 * <p>该配置类应通过 Spring Boot 自动装配机制（{@code spring.factories} 或
 * {@code AutoConfiguration.imports}）加载，无需用户手动引入。</p>
 */
@Configuration
public class OneThreadBaseConfig {

    /**
     * 注册动态线程池 Bean 后置处理器。
     *
     * <p>该处理器负责在 Bean 初始化完成后扫描带有
     * {@link com.liang.onethread.springbase.DynamicThreadPool @DynamicThreadPool}
     * 注解的线程池实例，并将其注册到全局注册中心。</p>
     */
    @Bean
    public OneThreadBeanPostProcessor oneThreadBeanPostProcessor() {
        return new OneThreadBeanPostProcessor();
    }
}
