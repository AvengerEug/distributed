package com.eugene.sumarry.resourcecode.service.impl;

import com.eugene.sumarry.resourcecode.service.PersonService;
import org.apache.dubbo.common.URL;

public class WomanWrapperServiceImpl implements PersonService {

    private PersonService personService;

    public WomanWrapperServiceImpl(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public void say(URL url) {
        System.out.println("before");
        personService.say(url);
        System.out.println("after");
    }
}
