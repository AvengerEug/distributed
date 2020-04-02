package com.eugene.sumarry.ioc.impl;

import com.eugene.sumarry.ioc.IndexService;
import org.apache.dubbo.common.URL;

public class IndexServiceImpl1 implements IndexService {


    @Override
    public void index(URL url) {
        System.out.println("IndexServiceImpl1 index");
    }

    @Override
    public void index2(URL url) {
        System.out.println("IndexServiceImpl1 index2");
    }

    @Override
    public void index3(URL url) {
        System.out.println("IndexServiceImpl1 index3");
    }

    @Override
    public void index4() {
        System.out.println("IndexServiceImpl1 index4");
    }

    @Override
    public void index5(URL url) {
        System.out.println("IndexServiceImpl1 index5");
    }
}
