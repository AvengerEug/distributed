package com.eugene.sumarry.ioc.impl;

import com.eugene.sumarry.ioc.IndexService;
import org.apache.dubbo.common.URL;

public class IndexServiceImpl implements IndexService {

    private IndexService indexService;

    public void setIndexService(IndexService indexService) {
        this.indexService = indexService;
    }

    @Override
    public void index(URL url) {
        System.out.println("IndexServiceImpl index");
        indexService.index(url);
    }

    @Override
    public void index2(URL url) {
        System.out.println("IndexServiceImpl index");
        indexService.index2(url);
    }

    @Override
    public void index3(URL url) {
        System.out.println("IndexServiceImpl index3");
    }

    @Override
    public void index4() {
        System.out.println("IndexServiceImpl index4");
    }

    @Override
    public void index5(URL url) {
        System.out.println("IndexServiceImpl index5");
    }
}
