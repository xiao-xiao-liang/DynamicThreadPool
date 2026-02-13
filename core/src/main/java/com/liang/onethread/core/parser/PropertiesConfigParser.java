package com.liang.onethread.core.parser;

import cn.hutool.core.collection.CollectionUtil;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Properties 配置文件解析器
 * <p>
 * 专门用于解析 .properties 格式的配置文件。
 * 继承自 {@link AbstractConfigParser}。
 * </p>
 */
public class PropertiesConfigParser extends AbstractConfigParser {
    
    /**
     * 解析 Properties 格式的字符串内容。
     * <p>
     * 利用 Java 原生 {@link Properties} 类进行加载和解析。
     * </p>
     *
     * @param content Properties 格式的字符串
     * @return 包含配置键值对的 Map
     * @throws IOException 解析失败时抛出
     */
    @Override
    public Map<Object, Object> doParse(String content) throws IOException {
        Properties properties = new Properties();
        properties.load(new StringReader(content));
        return properties;
    }

    /**
     * 获取支持的配置文件类型。
     *
     * @return 仅包含 {@link ConfigFileTypeEnum#PROPERTIES}
     */
    @Override
    public List<ConfigFileTypeEnum> getConfigFileTypes() {
        return CollectionUtil.newArrayList(ConfigFileTypeEnum.PROPERTIES);
    }
}
