package com.liang.onethread.common.starter.config;

import com.liang.onethread.core.config.BootstrapConfigProperties;
import com.liang.onethread.springbase.config.OneThreadBaseConfig;
import com.liang.onethread.springbase.enable.MarkerConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

/**
 * OneThread 通用自动配置类
 *
 * <p>负责加载通用配置，如 {@link BootstrapConfigProperties} 的绑定与单例初始化。
 * 该配置类在 {@link OneThreadBaseConfig} 之后自动装配，确保基础组件已就绪。
 * 当配置文件中存在 {@code onethread.enable=true} (默认) 时生效。</p>
 */
@ConditionalOnBean(MarkerConfiguration.Marker.class)
@Import(OneThreadBaseConfig.class)
@AutoConfigureAfter(OneThreadBaseConfig.class)
@ConditionalOnProperty(prefix = BootstrapConfigProperties.PREFIX, value = "enable", matchIfMissing = true, havingValue = "true")
public class CommonAutoConfig {
    
    @Bean
    public BootstrapConfigProperties bootstrapConfigProperties(Environment environment) {
        BootstrapConfigProperties bootstrapConfigProperties = Binder.get(environment)
                .bind(BootstrapConfigProperties.PREFIX, Bindable.of(BootstrapConfigProperties.class))
                .get();
        BootstrapConfigProperties.setInstance(bootstrapConfigProperties);
        return bootstrapConfigProperties;
    }
}
