package com.eugene.sumarry.resourcecode.service.impl;

import com.eugene.sumarry.resourcecode.service.IndexService;
import org.apache.dubbo.common.URL;

public class IndexServiceImpl implements IndexService {

    @Override
    public void index(URL url) {
        System.out.println("IndexServiceImpl index");
    }
}
