package com.liang.onethread.core.parser;

import lombok.Getter;

/**
 * 配置文件类型枚举
 * <p>
 * 定义了系统支持的配置文件格式，如 properties, yml, yaml 等。
 * 该枚举用于在解析配置时识别文件类型，以便选择合适的解析器。
 * </p>
 */
@Getter
public enum ConfigFileTypeEnum {

    /**
     * Properties 格式配置文件
     */
    PROPERTIES("properties"),
    
    /**
     * YML 格式配置文件
     */
    YML("yml"),
    
    /**
     * YAML 格式配置文件
     */
    YAML("yaml");

    private final String value;

    ConfigFileTypeEnum(String value) {
        this.value = value;
    }

    /**
     * 根据字符串值获取对应的枚举实例。
     * <p>
     * 如果未找到匹配的枚举值，默认返回 {@link #PROPERTIES}。
     * </p>
     *
     * @param value 配置文件类型字符串（如 "yml", "properties"）
     * @return 对应的枚举实例，默认为 PROPERTIES
     */
    public static ConfigFileTypeEnum of(String value) {
        for (ConfigFileTypeEnum typeEnum : ConfigFileTypeEnum.values()) {
            if (typeEnum.value.equals(value)) {
                return typeEnum;
            }
        }
        return PROPERTIES;
    }
}