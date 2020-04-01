package com.eugene.sumarry.resourcecode.service.impl;

import com.eugene.sumarry.resourcecode.service.PersonService;
import org.apache.dubbo.common.URL;

public class WomanServiceImpl implements PersonService {

    @Override
    public void say(URL url) {
        System.out.println("WomanServiceImpl say");
    }
}
