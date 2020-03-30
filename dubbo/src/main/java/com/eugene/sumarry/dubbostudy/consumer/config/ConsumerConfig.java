package com.eugene.sumarry.dubbostudy.consumer.config;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableDubbo(scanBasePackages = "com.eugene.sumarry.dubbostudy.consumer.service.impl")
@ComponentScan("com.eugene.sumarry.dubbostudy.consumer")
@PropertySource("classpath:dubbo-cluster-1/dubbo.properties")
public class ConsumerConfig {
}
