package com.eugene.sumarry.ioc.impl;

import com.eugene.sumarry.ioc.PersonService;
import org.apache.dubbo.common.URL;

public class WomanServiceImpl implements PersonService {

    @Override
    public void say(URL url) {
        System.out.println("WomanServiceImpl say");
    }
}
