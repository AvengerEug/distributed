package com.eugene.sumarry.aopsumioc.impl;

import com.eugene.sumarry.aopsumioc.OrderService;
import org.apache.dubbo.common.URL;

public class OrderServiceImplLogWrapper implements OrderService {

    private OrderService orderService;

    public OrderServiceImplLogWrapper(OrderService orderService) {
        // 为OrderServiceImpl实现aop
        this.orderService = orderService;
    }

    @Override
    public void findOrders() {
        System.out.println("订单查询之前 。。。。");
        orderService.findOrders();
        System.out.println("订单查询之后");
    }
}
