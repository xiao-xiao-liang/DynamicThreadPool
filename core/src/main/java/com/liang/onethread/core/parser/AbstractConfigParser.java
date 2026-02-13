package com.liang.onethread.core.parser;

/**
 * 配置解析器抽象基类
 * <p>
 * 提供配置解析器的通用实现逻辑，旨在减少重复代码。
 * 目前主要实现了 {@link #supports(ConfigFileTypeEnum)} 方法，
 * 用于判断当前解析器是否支持特定的配置文件类型。
 * </p>
 */
public abstract class AbstractConfigParser implements ConfigParser {

    /**
     * 判断当前解析器是否支持指定的配置文件类型。
     * <p>
     * 通过调用 {@link #getConfigFileTypes()} 获取支持的类型列表，
     * 并检查传入的类型是否存在于该列表中。
     * </p>
     *
     * @param type 待检查的配置文件类型枚举
     * @return 如果支持该类型返回 {@code true}，否则返回 {@code false}
     */
    @Override
    public boolean supports(ConfigFileTypeEnum type) {
        return getConfigFileTypes().contains(type);
    }
}