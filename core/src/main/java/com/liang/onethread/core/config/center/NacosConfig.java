package com.liang.onethread.core.config.center;

import lombok.Data;

/**
 * Nacos 配置中心参数对象
 * <p>
 * 该类用于在使用 Nacos 作为动态配置中心时，接收和存储配置文件的定位信息。
 * 核心参数包括配置 ID（DataId）和分组（Group），用于唯一确定一个配置文件。
 * </p>
 */
@Data
public class NacosConfig {

    /**
     * Nacos 配置文件的 DataId
     */
    private String dataId;

    /**
     * Nacos 配置文件所属的 Group
     */
    private String group;
}
