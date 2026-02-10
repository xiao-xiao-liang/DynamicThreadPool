package com.liang.onethread.springbase.enable;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 动态线程池标记配置类
 *
 * <p>该配置类由 {@link EnableOneThread} 注解导入，主要作用是向 Spring 容器中注册一个
 * {@link Marker} 类型的 Bean。该 Marker Bean 的存在与否，将作为启用动态线程池功能的
 * 判断依据（通常配合 {@code @ConditionalOnBean} 使用）。</p>
 *
 * <p>这种设计模式允许用户通过 {@code @EnableOneThread} 注解显式控制功能的开启，
 * 而不只是依赖于类路径下的依赖包是否存在。</p>
 *
 * @see EnableOneThread
 */
@Configuration
public class MarkerConfiguration {

    /**
     * 注册动态线程池标记 Bean。
     *
     * @return 标记对象实例
     */
    @Bean
    public Marker dynamicThreadPoolMarkerBean() {
        return new Marker();
    }

    /**
     * 动态线程池启用标记类。
     *
     * <p>这是一个空的标记类，仅用于在 Spring 容器中占位，标识 {@link EnableOneThread}
     * 注解已被使用。</p>
     */
    public class Marker {

    }
}