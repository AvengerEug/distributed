package com.eugene.sumarry.aopsumioc.impl;

import com.eugene.sumarry.aopsumioc.GoodsService;
import com.eugene.sumarry.aopsumioc.OrderService;
import org.apache.dubbo.common.URL;

public class OrderServiceImpl implements OrderService {

    private GoodsService goodsService;

    public void setGoodsService(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @Override
    public void findOrders() {
        URL url = new URL("", "", -1);
        url = url.addParameter("goodsService", "goodsService");
        goodsService.findGoods(url);
        System.out.println("查询出订单详细信息");
    }
}
