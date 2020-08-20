package com.eugene.sumarry.dubboxml.provider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class ProviderEntry {

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:dubboxml/provider/dubbo.xml");
        context.start();
        System.in.read();
    }
}
