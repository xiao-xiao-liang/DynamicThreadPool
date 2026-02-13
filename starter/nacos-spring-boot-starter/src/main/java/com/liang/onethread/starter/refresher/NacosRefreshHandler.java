package com.liang.onethread.starter.refresher;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.liang.onethread.core.config.BootstrapConfigProperties;
import com.liang.onethread.core.config.center.NacosConfig;
import com.liang.onethread.core.executor.support.BlockingQueueTypeEnum;
import com.liang.onethread.core.parser.ConfigParserHandler;
import com.liang.onethread.core.tookit.ThreadPoolExecutorBuilder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Nacos 监听器，用于监听 Nacos 配置文件变化并实时更新动态线程池参数。
 * <p>
 * 该类实现了 {@link ApplicationRunner} 接口，在应用启动完成后自动执行。
 * 主要职责是从 Nacos 配置中心获取配置，并注册监听器以感知配置变更。
 * 当配置发生变化时，解析最新的配置内容并更新 {@link BootstrapConfigProperties}，
 * 进而刷新线程池的运行时参数。
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
public class NacosRefreshHandler implements ApplicationRunner {

    private final BootstrapConfigProperties properties;
    private final NacosConfigManager nacosConfigManager;

    private ConfigService configService;

    /**
     * 应用启动后执行，初始化 Nacos 配置服务并注册监听器。
     *
     * @param args 应用启动参数
     * @throws Exception 初始化过程中的异常
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 获取 Nacos 配置服务实例，用于后续的配置获取和监听注册
        configService = nacosConfigManager.getConfigService();
        // 注册监听器，开始监听配置变更
        registerListener();
    }

    /**
     * 注册 Nacos 监听器。
     * <p>
     * 根据 {@link BootstrapConfigProperties} 中配置的 DataId 和 Group，
     * 向 Nacos ConfigService 注册一个监听器。
     * 当监听到配置变更时，会回调 {@link Listener#receiveConfigInfo(String)} 方法。
     * </p>
     *
     * @throws NacosException Nacos 异常
     */
    public void registerListener() throws NacosException {
        NacosConfig nacosConfig = properties.getNacos();
        // 添加监听器，指定 DataId 和 Group
        configService.addListener(nacosConfig.getDataId(), nacosConfig.getGroup(), new Listener() {
             // 提供用于执行回调任务的 Executor。
             // 为了避免阻塞 Nacos 内部线程或主线程，这里提供一个独立的线程池来处理配置变更事件。该线程池配置为单线程，使用同步队列。
            @Override
            public Executor getExecutor() {
                return ThreadPoolExecutorBuilder.builder()
                        .corePoolSize(1)
                        .maximumPoolSize(1)
                        .keepAliveTime(9999L)
                        .workQueueType(BlockingQueueTypeEnum.SYNCHRONOUS_QUEUE)
                        .threadFactory("nacos-refresher-thread_")
                        .rejectedHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                        .build();
            }

            // 接收并处理最新的配置信息。
            @SneakyThrows
            @Override
            public void receiveConfigInfo(String configInfo) {
                // 解析配置文件，将字符串内容解析为 Map
                Map<Object, Object> configInfoMap = ConfigParserHandler.getInstance().parseConfig(configInfo, properties.getConfigFileType());
                MapConfigurationPropertySource sources = new MapConfigurationPropertySource(configInfoMap);
                Binder binder = new Binder(sources);
                // 绑定配置到 BootstrapConfigProperties，更新内存中的配置对象
                BootstrapConfigProperties refreshProperties = binder.bind(BootstrapConfigProperties.PREFIX, Bindable.ofInstance(properties)).get();
                log.info("Listen for changes in the Nacos configuration file: {}", refreshProperties);

                // TODO 刷新动态线程池对象核心参数
            }
        });
        log.info("Dynamic thread pool refresher, add nacos cloud listener success. data-id: {}, group: {}", nacosConfig.getDataId(), nacosConfig.getGroup());
    }
}
