package com.eugene.sumarry.dubboxml.consumer.impl;

import com.eugene.sumarry.dubboxml.consumer.TestService;
import com.eugene.sumarry.dubboxml.provider.DemoService;

public class TestServiceImpl implements TestService {

    private DemoService demoService;

    public void setDemoService(DemoService demoService) {
        this.demoService = demoService;
    }

    @Override
    public void test() {
        System.out.println("调用远程服务，返回值为：" + demoService.sayHello());
    }
}
