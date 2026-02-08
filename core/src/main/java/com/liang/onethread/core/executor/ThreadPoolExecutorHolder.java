package com.liang.onethread.core.executor;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池执行器持有者
 * <p>
 * 该类是线程池及其配置的包装容器，作为 {@link OneThreadRegistry} 中管理的基本单元。
 * 通过将线程池实例、唯一标识和配置属性封装在一起，便于统一管理和动态调整线程池参数。
 * <p>
 * 主要功能：
 * <ul>
 *     <li>持有线程池执行器实例 {@link java.util.concurrent.ThreadPoolExecutor}</li>
 *     <li>维护线程池的唯一标识，用于在注册中心中检索</li>
 *     <li>关联线程池的属性配置，支持动态参数变更</li>
 * </ul>
 * <p>
 * 使用场景：
 * <ul>
 *     <li>在 {@link OneThreadRegistry} 中作为线程池的存储载体</li>
 *     <li>获取线程池时同时获取其关联的配置信息</li>
 *     <li>动态修改线程池参数时，通过此类同步更新配置</li>
 * </ul>
 *
 * @see OneThreadRegistry
 * @see ThreadPoolExecutorProperties
 */
@Data
@AllArgsConstructor
public class ThreadPoolExecutorHolder {

    /**
     * 线程池唯一标识
     */
    private String threadPoolId;

    /**
     * 线程池
     */
    private ThreadPoolExecutor executor;

    /**
     * 线程池属性参数
     */
    private ThreadPoolExecutorProperties executorProperties;
}
