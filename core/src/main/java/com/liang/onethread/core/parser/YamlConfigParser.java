package com.liang.onethread.core.parser;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * YAML 配置文件解析器
 * <p>
 * 该解析器负责将 YAML 格式的配置字符串解析为扁平化的 Map 结构，
 * 以便系统的其他部分可以通过键值对的方式（类似 Properties）访问配置。
 * 支持处理嵌套的对象结构和列表结构。
 * </p>
 */
public class YamlConfigParser extends AbstractConfigParser {

    /**
     * 列表索引的前缀符号，例如 "items[0]" 中的 "["
     */
    private static final String INDEX_PREFIX = "[";

    /**
     * 列表索引的后缀符号，例如 "items[0]" 中的 "]"
     */
    private static final String INDEX_SUFFIX = "]";

    /**
     * 路径分隔符，用于连接父子节点，例如 "parent.child"
     */
    private static final String PATH_SEPARATOR = ".";

    /**
     * 执行配置文件解析的主入口方法
     * <p>
     * 首先将 YAML 字符串解析为嵌套的 Map/List 结构，
     * 然后调用 {@link #normalizeHierarchy(Map)} 将其扁平化为单层 Map。
     * </p>
     *
     * @param configuration 从配置中心或文件读取到的原始 YAML 格式字符串
     * @return 解析并扁平化后的 Map 对象。如果输入为空或解析结果为空，则返回空 Map。
     * @throws IOException 本实现实际上不会抛出 IOException，但为了适配接口签名保留声明
     */
    @Override
    public Map<Object, Object> doParse(String configuration) throws IOException {
        return Optional.ofNullable(configuration)
                .filter(StrUtil::isNotEmpty)
                .map(this::parseYamlDocument)
                .map(this::normalizeHierarchy)
                .orElseGet(Collections::emptyMap);
    }

    /**
     * 获取当前解析器支持的配置文件类型
     *
     * @return 包含 YAML 和 YML 枚举值的列表
     */
    @Override
    public List<ConfigFileTypeEnum> getConfigFileTypes() {
        return List.of(ConfigFileTypeEnum.YAML, ConfigFileTypeEnum.YML);
    }

    /**
     * 使用 SnakeYAML 库将字符串解析为嵌套的 Map 结构
     *
     * @param content YAML 格式的字符串内容
     * @return 解析后的原始 Map 对象（可能包含嵌套的 Map 或 List）
     */
    private Map<Object, Object> parseYamlDocument(String content) {
        return Optional.ofNullable(new Yaml().load(content))
                .filter(obj -> obj instanceof Map)  // 确保解析结果是 Map 类型
                .map(obj -> (Map<Object, Object>) obj)  // 强制转换为 Map
                .filter(map -> !MapUtil.isEmpty(map)) // 过滤掉空 Map
                .orElseGet(Collections::emptyMap);
    }

    /**
     * 将解析后的嵌套结构扁平化为单层 Map。
     * <p>
     * 例如将:
     * <pre>
     * server:
     *   port: 8080
     * </pre>
     * 转换为: {@code server.port = 8080}
     * </p>
     *
     * @param nestedData 原始的嵌套 Map 数据
     * @return 扁平化后的 LinkedHashMap，保持插入顺序
     */
    private Map<Object, Object> normalizeHierarchy(Map<Object, Object> nestedData) {
        Map<Object, Object> flattenedData = new LinkedHashMap<>();
        processNestedElements(flattenedData, nestedData, null);
        return flattenedData;
    }

    /**
     * 递归处理嵌套元素的核心方法
     *
     * @param target      用于收集结果的目标 Map
     * @param current     当前正在处理的对象（Map、List 或具体的值）
     * @param currentPath 当前对象在结构树中的累积路径
     */
    private void processNestedElements(Map<Object, Object> target, Object current, String currentPath) {
        if (current instanceof Map)
            // 如果当前节点是 Map，递归处理其条目
            handleMapEntries(target, (Map<?, ?>) current, currentPath);
        else if (current instanceof Iterable)
            // 如果当前节点是集合/列表，递归处理其元素
            handleCollectionItems(target, (Iterable<?>) current, currentPath);
        else
            // 如果是叶子节点（具体值），则存入结果集
            persistLeafValue(target, currentPath, current);
    }

    /**
     * 处理 Map 类型的子节点
     *
     * @param target     目标 Map
     * @param entries    当前的 Map 节点数据
     * @param parentPath 父级路径
     */
    private void handleMapEntries(Map<Object, Object> target, Map<?, ?> entries, String parentPath) {
        entries.forEach((key, value) ->
                processNestedElements(target, value, buildPathSegment(parentPath, key))
        );
    }

    /**
     * 处理集合类型的子节点（如 List）
     * <p>
     * 集合元素将被转换为带索引的键，例如 items[0]
     * </p>
     *
     * @param target   目标 Map
     * @param items    当前的集合数据
     * @param basePath 集合的基础路径
     */
    private void handleCollectionItems(Map<Object, Object> target, Iterable<?> items, String basePath) {
        List<?> elements = StreamSupport.stream(items.spliterator(), false).toList();
        IntStream.range(0, elements.size())
                .forEach(index -> processNestedElements(
                        target,
                        elements.get(index),
                        createIndexedPath(basePath, index)
                ));
    }

    /**
     * 构建路径片段（处理点号分隔符）
     *
     * @param existingPath 当前已有的路径
     * @param key          当前节点的键
     * @return 拼接后的新路径
     */
    private String buildPathSegment(String existingPath, Object key) {
        return existingPath == null ?
                key.toString() :
                existingPath + PATH_SEPARATOR + key;
    }

    /**
     * 构建带索引的路径（用于数组/列表）
     *
     * @param basePath 基础路径
     * @param index    当前元素的索引
     * @return 带索引后缀的路径，如 "key[0]"
     */
    private String createIndexedPath(String basePath, int index) {
        return basePath + INDEX_PREFIX + index + INDEX_SUFFIX;
    }

    /**
     * 将最终的叶子节点值存入 Map
     *
     * @param target 目标 Map
     * @param path   完整的键路径
     * @param value  配置值
     */
    private void persistLeafValue(Map<Object, Object> target, String path, Object value) {
        if (path != null) {
            // 规范化路径：防止出现 "key.[0]" 的情况，统一替换为 "key[0]"
            String normalizedPath = path.replace(PATH_SEPARATOR + INDEX_PREFIX, INDEX_PREFIX);
            target.put(normalizedPath, value != null ? value.toString() : null);
        }
    }
}
