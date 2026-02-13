package com.liang.onethread.core.config.notify;

import lombok.Data;

/**
 * 通知平台配置参数对象
 * <p>
 * 该类用于配置动态线程池报警通知的发送渠道信息。
 * 支持配置报警平台类型（如钉钉、企业微信、飞书等）以及对应的 WebHook 地址或 Token。
 * 当线程池发生异常或指标超阈值时，将通过此配置发送报警信息。
 * </p>
 */
@Data
public class NotifyPlatformsConfig {

    /**
     * 报警通知平台类型
     * <p>
     * 标识当前使用的通知渠道，例如：
     * <ul>
     *     <li>DING: 钉钉</li>
     *     <li>WECHAT: 企业微信</li>
     *     <li>LARK: 飞书</li>
     * </ul>
     * </p>
     */
    private String platform;

    /**
     * 报警通知 WebHook URL 或 Token
     * <p>
     * 配置通知平台的完整 WebHook 地址，或者仅配置 Token（取决于具体实现）。
     * 系统将向该地址发送报警消息。
     * </p>
     */
    private String url;
}