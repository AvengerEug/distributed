package com.eugene.sumarry.dubbostudy.provider.config;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableDubbo(scanBasePackages = "com.eugene.sumarry.dubbostudy.provider.service.impl")
@ComponentScan(
        basePackages = "com.eugene.sumarry.dubbostudy.provider",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProviderClusterConfig1.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProviderClusterConfig3.class)
        }
)
@PropertySource("classpath:dubbo-cluster-2/dubbo.properties")
public class ProviderClusterConfig2 {

}
