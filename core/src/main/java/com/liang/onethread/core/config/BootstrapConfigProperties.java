package com.liang.onethread.core.config;

import com.liang.onethread.core.config.center.ApolloConfig;
import com.liang.onethread.core.config.center.NacosConfig;
import com.liang.onethread.core.config.monitor.MonitorConfig;
import com.liang.onethread.core.config.notify.NotifyPlatformsConfig;
import com.liang.onethread.core.config.web.WebThreadPoolExecutorConfig;
import com.liang.onethread.core.executor.ThreadPoolExecutorProperties;
import com.liang.onethread.core.parser.ConfigFileTypeEnum;
import lombok.Data;

import java.util.List;

/**
 * oneThread 动态线程池核心配置参数
 * <p>
 * 该类是整个动态线程池框架的配置入口，集成了所有子模块的配置项。
 * 包括：全局开关、配置中心（Nacos/Apollo）、监控、通知报警、Web 容器线程池以及自定义线程池列表等。
 * </p>
 */
@Data
public class BootstrapConfigProperties {

    public static final String PREFIX = "onethread";

    /**
     * 动态线程池全局开关
     * <p>
     * 控制是否启用动态线程池功能。
     * 默认为 {@code true}（开启）。如果设置为 {@code false}，则相关组件不会加载。
     * </p>
     */
    private Boolean enable = Boolean.TRUE;

    /**
     * Nacos 配置中心参数
     * <p>
     * 当选择 Nacos 作为配置中心时，需配置 DataId 和 Group。
     * </p>
     */
    private NacosConfig nacos;

    /**
     * Apollo 配置中心参数
     */
    private ApolloConfig apollo;

    /**
     * Web 容器线程池配置
     */
    private WebThreadPoolExecutorConfig web;

    /**
     * Nacos 远程配置文件格式
     */
    private ConfigFileTypeEnum configFileType;
    
    /**
     * 报警通知配置
     */
    private NotifyPlatformsConfig notifyPlatforms;

    /**
     * 运行状态监控配置
     */
    private MonitorConfig monitor = new MonitorConfig();

    /**
     * 自定义线程池配置列表
     */
    private List<ThreadPoolExecutorProperties> executors;

    private static BootstrapConfigProperties INSTANCE = new BootstrapConfigProperties();

    public static BootstrapConfigProperties getInstance() {
        return INSTANCE;
    }

    public static void setInstance(BootstrapConfigProperties properties) {
        INSTANCE = properties;
    }
}
