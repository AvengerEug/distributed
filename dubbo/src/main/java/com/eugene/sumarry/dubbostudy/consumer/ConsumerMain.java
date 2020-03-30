package com.eugene.sumarry.dubbostudy.consumer;

import com.eugene.sumarry.dubbostudy.provider.service.DemoService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

public class ConsumerMain {

    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:consumer.xml");
        rpcCall(context);
    }

    private static void rpcCall(ClassPathXmlApplicationContext context) throws InterruptedException {
        int count = 0;
        while (true) {
            DemoService demoService = (DemoService)context.getBean("demoService"); // 获取远程服务代理
            System.out.println(demoService.say("world" + ++count));
            TimeUnit.SECONDS.sleep(2);
        }
    }
}
