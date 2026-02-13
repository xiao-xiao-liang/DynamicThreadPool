package com.liang.onethread.core.parser;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 配置解析器接口
 * <p>
 * 定义了配置文件解析器的统一行为规范。
 * 不同格式的配置文件（如 Properties, YAML）需要实现该接口来提供具体的解析逻辑。
 * </p>
 */
public interface ConfigParser {

    /**
     * 判断是否支持指定类型的配置文件解析。
     *
     * @param type 配置文件类型枚举 {@link ConfigFileTypeEnum}
     * @return 如果支持该类型则返回 {@code true}，否则返回 {@code false}
     */
    boolean supports(ConfigFileTypeEnum type);

    /**
     * 解析配置内容字符串为 Map 结构。
     * <p>
     * 将字符串格式的配置内容解析为键值对形式，便于后续进行数据绑定。
     * </p>
     * 
     * @param content 配置文件内容字符串
     * @return 解析后的键值对 Map，Key 和 Value 通常为 String 类型
     * @throws IOException 当解析过程中发生 I/O 异常或格式错误时抛出
     */
    Map<Object, Object> doParse(String content) throws IOException;

    /**
     * 获取当前解析器支持的配置文件类型列表。
     *
     * @return 支持的配置文件类型集合，例如 [YAML, YML]
     */
    List<ConfigFileTypeEnum> getConfigFileTypes();
}
