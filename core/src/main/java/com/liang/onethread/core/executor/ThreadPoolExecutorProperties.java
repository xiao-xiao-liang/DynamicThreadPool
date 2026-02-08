package com.liang.onethread.core.executor;

import com.liang.onethread.core.alarm.dto.AlarmConfig;
import com.liang.onethread.core.notification.dto.NotifyConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 线程池属性配置类
 * <p>
 * 封装动态线程池的所有可配置参数，包括核心线程数、最大线程数、队列类型、拒绝策略等基础配置，
 * 以及告警配置和通知配置等扩展功能。该类是动态线程池框架中配置管理的核心数据结构。
 * <p>
 * 配置项说明：
 * <ul>
 *     <li><b>基础配置</b> - 核心线程数、最大线程数、队列容量、空闲线程存活时间等</li>
 *     <li><b>队列配置</b> - 支持多种阻塞队列类型（如 LinkedBlockingQueue、ArrayBlockingQueue 等）</li>
 *     <li><b>拒绝策略</b> - 支持标准拒绝策略（如 CallerRunsPolicy、AbortPolicy 等）</li>
 *     <li><b>告警配置</b> - 队列使用率告警、活跃线程数告警等监控阈值</li>
 *     <li><b>通知配置</b> - 告警触发后的通知接收人和通知频率</li>
 * </ul>
 * <p>
 * 使用场景：
 * <ul>
 *     <li>创建动态线程池时指定初始配置参数</li>
 *     <li>运行时动态修改线程池参数（如扩容、缩容）</li>
 *     <li>配置中心（如 Nacos）推送配置变更时作为载体</li>
 *     <li>持久化线程池配置到数据库或配置文件</li>
 * </ul>
 *
 * @see com.liang.onethread.core.alarm.dto.AlarmConfig
 * @see com.liang.onethread.core.notification.dto.NotifyConfig
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ThreadPoolExecutorProperties {

    /**
     * 线程池唯一标识
     */
    private String threadPoolId;

    /**
     * 核心线程数
     */
    private Integer corePoolSize;

    /**
     * 最大线程数
     */
    private Integer maximumPoolSize;

    /**
     * 队列容量
     */
    private Integer queueCapacity;

    /**
     * 阻塞队列类型
     */
    private String workQueue;

    /**
     * 拒绝策略类型
     */
    private String rejectedHandler;

    /**
     * 空闲线程存活时间，单位：秒
     */
    private Long keepAliveTime;

    /**
     * 是否允许核心线程超时
     */
    private Boolean allowCoreThreadTimeOut;

    /**
     * 通知配置
     */
    private NotifyConfig notify;

    /**
     * 告警配置，默认设置
     */
    private AlarmConfig alarm = new AlarmConfig();
}
