package com.liang.onethread.springbase.enable;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 OneThread 动态线程池功能注解
 *
 * <p>在 Spring Boot 启动类或配置类上添加此注解，即可启用动态线程池的管理功能。
 * 该注解会通过 {@link Import} 机制注册 {@link MarkerConfiguration}，从而在 Spring 容器中
 * 创建一个标记 Bean（Marker），后续的自动配置类（如 OneThreadAutoConfiguration）
 * 可通过 {@code @ConditionalOnBean(Marker.class)} 来判断是否启用了动态线程池功能。</p>
 *
 * <p>示例：</p>
 * <pre>{@code
 * @SpringBootApplication
 * @EnableOneThread
 * public class MyApplication {
 *     public static void main(String[] args) {
 *         SpringApplication.run(MyApplication.class, args);
 *     }
 * }
 * }</pre>
 *
 * @see MarkerConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MarkerConfiguration.class)
public @interface EnableOneThread {
}
