package com.liang.onethread.core.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 配置解析器处理器（门面模式/策略模式）
 * <p>
 * 该类负责管理系统中所有可用的配置解析器，并根据配置文件类型自动选择合适的解析器进行解析。
 * 采用了单例模式（Singleton）确保全局只有一个处理器实例。
 * </p>
 */
public final class ConfigParserHandler {
    
    /**
     * 注册的解析器列表
     */
    private static final List<ConfigParser> PARSERS = new ArrayList<>();
    
    /**
     * 私有构造函数，初始化默认的解析器。
     * 目前支持 YAML 和 Properties 两种格式。
     */
    private ConfigParserHandler() {
        PARSERS.add(new YamlConfigParser());
        PARSERS.add(new PropertiesConfigParser());
    }
    
    /**
     * 解析配置内容。
     * <p>
     * 遍历所有注册的解析器，找到支持当前文件类型的解析器并执行解析。
     * 如果没找到合适的解析器，则返回空 Map。
     * </p>
     *
     * @param content 配置文件内容字符串
     * @param type    配置文件类型
     * @return 解析后的 Map 对象
     * @throws IOException 解析异常时抛出
     */
    public Map<Object, Object> parseConfig(String content, ConfigFileTypeEnum type) throws IOException {
        for (ConfigParser parser : PARSERS)
            if (parser.supports(type))
                return parser.doParse(content);
        
        return Collections.emptyMap();
    }
    
    /**
     * 获取 ConfigParserHandler 单例实例。
     *
     * @return 全局唯一的 ConfigParserHandler 实例
     */
    public static ConfigParserHandler getInstance() {
        return  ConfigParserHandlerHolder.INSTANCE;
    }
    
    /**
     * 静态内部类实现单例模式（Lazy Initialization Holder Idiom）。
     */
    private static class ConfigParserHandlerHolder {
        private static final ConfigParserHandler INSTANCE = new ConfigParserHandler();
    }
}
