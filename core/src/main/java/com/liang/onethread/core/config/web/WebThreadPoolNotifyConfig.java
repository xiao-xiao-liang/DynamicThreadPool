package com.liang.onethread.core.config.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Web 线程池报警通知接收配置对象
 * <p>
 * 该类定义了 Web 容器线程池报警信息的具体接收策略。
 * 可以指定报警信息的接收人列表，支持以逗号分隔的多个接收人标识。
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebThreadPoolNotifyConfig {

    /**
     * 报警接收人集合
     * <p>
     * 配置报警信息的接收目标，通常为用户 ID、手机号或群组 Key。
     * 多个接收人之间使用英文逗号 "," 分隔。
     * </p>
     */
    private String receives;
}
