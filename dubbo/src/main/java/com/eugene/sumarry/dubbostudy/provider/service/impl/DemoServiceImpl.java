package com.eugene.sumarry.dubbostudy.provider.service.impl;

import com.eugene.sumarry.dubbostudy.provider.service.DemoService;

public class DemoServiceImpl implements DemoService {

    @Override
    public void say(String content) {
        System.out.println("Hello " + content);
    }
}
