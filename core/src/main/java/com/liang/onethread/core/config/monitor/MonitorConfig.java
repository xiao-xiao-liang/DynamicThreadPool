package com.liang.onethread.core.config.monitor;

import lombok.Data;

/**
 * 监控配置参数对象
 * <p>
 * 该类负责定义动态线程池监控模块的相关配置。
 * 通过配置该类，可以控制是否开启监控、选择监控数据的采集类型（如 Micrometer、Prometheus 等）
 * 以及设置监控数据的采集时间间隔。
 * </p>
 */
@Data
public class MonitorConfig {

    /**
     * 监控功能开关
     * <p>
     * {@code true} 表示开启线程池监控，{@code false} 表示关闭。
     * 默认值为 {@code true}。
     * </p>
     */
    private Boolean enable = Boolean.TRUE;

    /**
     * 监控数据采集类型
     * <p>
     * 指定使用哪种技术栈或格式进行指标采集。
     * 默认值为 "micrometer"，可对接 Prometheus、Grafana 等监控系统。
     * </p>
     */
    private String collectType = "micrometer";

    /**
     * 监控数据采集间隔时间
     * <p>
     * 设置后台采集线程池运行状态的时间间隔，单位为秒（Seconds）。
     * 默认值为 10 秒。间隔越短，数据的实时性越高，但对系统性能的影响也相对越大。
     * </p>
     */
    private Long collectInterval = 10L;
}
