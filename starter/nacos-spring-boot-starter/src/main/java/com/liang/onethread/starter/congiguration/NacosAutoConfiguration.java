package com.liang.onethread.starter.congiguration;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.liang.onethread.core.config.BootstrapConfigProperties;
import com.liang.onethread.springbase.enable.MarkerConfiguration;
import com.liang.onethread.starter.refresher.NacosRefreshHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Nacos 动态线程池自动装配类
 * <p>
 * 负责在 Spring Context 中自动配置和注册与 Nacos 相关的 Bean 组件。
 * 核心作用是初始化 {@link NacosRefreshHandler}，以便启动 Nacos 配置监听功能。
 * </p>
 * <p>
 * 生效条件：
 * <ul>
 *     <li>必须存在 {@link MarkerConfiguration.Marker} Bean（通常由主启用注解 @EnableDynamicThreadPool 导入）</li>
 *     <li>配置项 {@code onethread.enable} 必须为 {@code true} 或缺省（默认开启）</li>
 * </ul>
 * </p>
 */
@ConditionalOnBean(MarkerConfiguration.Marker.class)
@ConditionalOnProperty(prefix = BootstrapConfigProperties.PREFIX, value = "enable", matchIfMissing = true, havingValue = "true")
public class NacosAutoConfiguration {

    /**
     * 注册 NacosRefreshHandler Bean。
     *
     * @param properties        动态线程池配置属性
     * @param nacosConfigManager Nacos 配置管理器
     * @return NacosRefreshHandler 实例
     */
    @Bean
    public NacosRefreshHandler nacosRefreshHandler(BootstrapConfigProperties properties, NacosConfigManager nacosConfigManager) {
        return new NacosRefreshHandler(properties, nacosConfigManager);
    }
}
