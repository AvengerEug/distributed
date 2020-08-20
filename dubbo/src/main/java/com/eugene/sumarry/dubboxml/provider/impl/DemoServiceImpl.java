package com.eugene.sumarry.dubboxml.provider.impl;

import com.eugene.sumarry.dubboxml.provider.DemoService;

public class DemoServiceImpl implements DemoService {

    @Override
    public String sayHello() {
        return "hello";
    }
}
