package com.eugene.sumarry.dubbostudy.provider.config;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.*;

@Configuration
@EnableDubbo(scanBasePackages = "com.eugene.sumarry.dubbostudy.provider.service.impl")
@ComponentScan(
        basePackages = "com.eugene.sumarry.dubbostudy.provider",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProviderClusterConfig1.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProviderClusterConfig2.class)
        }
)
@PropertySource("classpath:dubbo-cluster-3/dubbo.properties")
public class ProviderClusterConfig3 {


}
