package com.liang.onethread.core.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通知配置类
 * <p>
 * 用于配置线程池告警触发后的通知策略，包括通知接收人和通知频率等。
 * 该配置决定了当线程池发生异常或达到告警阈值时，系统如何将告警信息推送给相关人员。
 * <p>
 * 使用场景：
 * <ul>
 *     <li>配置告警消息的接收人（如运维人员、开发负责人等）</li>
 *     <li>控制告警通知的发送频率，避免短时间内重复告警造成信息轰炸</li>
 * </ul>
 * <p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotifyConfig {

    /**
     * 接收人集合
     */
    private String receives;

    /**
     * 告警间隔，单位：分钟
     */
    private Integer interval;
}
