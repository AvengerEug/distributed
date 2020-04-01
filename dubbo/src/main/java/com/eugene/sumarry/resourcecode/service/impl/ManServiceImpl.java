package com.eugene.sumarry.resourcecode.service.impl;

import com.eugene.sumarry.resourcecode.service.IndexService;
import com.eugene.sumarry.resourcecode.service.PersonService;
import org.apache.dubbo.common.URL;

public class ManServiceImpl implements PersonService {

    private IndexService indexService;

    public void setIndexService(IndexService indexService) {
        this.indexService = indexService;
    }

    @Override
    public void say(URL url) {
        System.out.println("ManServiceImpl say method");
        indexService.index(url);
    }
}
