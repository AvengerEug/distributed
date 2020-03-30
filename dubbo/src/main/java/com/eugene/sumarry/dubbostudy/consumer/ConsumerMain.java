package com.eugene.sumarry.dubbostudy.consumer;

import com.eugene.sumarry.dubbostudy.consumer.config.ConsumerConfig;
import com.eugene.sumarry.dubbostudy.consumer.service.ConsumerService;
import com.eugene.sumarry.dubbostudy.provider.service.DemoService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

public class ConsumerMain {

    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConsumerConfig.class);
        ConsumerService consumerService = context.getBean(ConsumerService.class);
        consumerService.consumer();
    }

}
