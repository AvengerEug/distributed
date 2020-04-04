package com.eugene.sumarry.aopsumioc;

import org.apache.dubbo.common.extension.SPI;

@SPI
public interface OrderService {

    void findOrders();
}
