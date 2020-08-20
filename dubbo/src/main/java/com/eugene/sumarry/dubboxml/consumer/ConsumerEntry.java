package com.eugene.sumarry.dubboxml.consumer;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class ConsumerEntry {

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:dubboxml/consumer/dubbo.xml");
        context.start();
        TestService bean = context.getBean(TestService.class);
        bean.test();
    }
}
