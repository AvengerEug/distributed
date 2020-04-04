package com.eugene.sumarry.aopsumioc.impl;

import com.eugene.sumarry.aopsumioc.GoodsService;
import org.apache.dubbo.common.URL;

public class GoodsServiceImpl implements GoodsService {

    @Override
    public void findGoods(URL url) {
        System.out.println("find goods");
    }
}
