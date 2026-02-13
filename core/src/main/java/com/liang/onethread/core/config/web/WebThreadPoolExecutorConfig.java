package com.liang.onethread.core.config.web;

import com.liang.onethread.core.notification.dto.NotifyConfig;
import lombok.Data;

/**
 * Web 容器线程池配置参数对象
 * <p>
 * 该类专门用于映射和管理 Spring Boot 内嵌 Web 容器（如 Tomcat、Jetty、Undertow）
 * 的线程池参数。允许开发者通过配置文件动态调整 Web 容器的线程资源，
 * 而无需重启应用，从而应对突发的流量高峰。
 * </p>
 */
@Data
public class WebThreadPoolExecutorConfig {

    /**
     * 核心线程数
     */
    private Integer corePoolSize;

    /**
     * 最大线程数
     */
    private Integer maximumPoolSize;

    /**
     * 线程空闲存活时间
     */
    private Long keepAliveTime;

    /**
     * Web 容器报警通知配置
     * <p>
     * 用于配置针对 Web 容器线程池的特定报警规则和接收人信息。
     * </p>
     */
    private NotifyConfig notify;
}
