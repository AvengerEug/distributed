package com.eugene.sumarry.dubbostudy.consumer;

import com.eugene.sumarry.dubbostudy.provider.service.DemoService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ConsumerMain {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:consumer.xml");
        context.start();
        DemoService demoService = (DemoService)context.getBean("demoService"); // 获取远程服务代理
        demoService.say("world"); // 执行远程方法
    }
}
