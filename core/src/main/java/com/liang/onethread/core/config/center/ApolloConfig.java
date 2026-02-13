package com.liang.onethread.core.config.center;

import lombok.Data;

/**
 * Apollo 配置中心参数对象
 * <p>
 * 该类用于在使用 Apollo 作为动态配置中心时，接收和存储相关的配置参数。
 * 目前主要包含命名空间（Namespace）的配置，用于隔离不同的配置环境或模块。
 * </p>
 */
@Data
public class ApolloConfig {

    /**
     * Apollo 的命名空间（Namespace）
     */
    private String namespace;
}
