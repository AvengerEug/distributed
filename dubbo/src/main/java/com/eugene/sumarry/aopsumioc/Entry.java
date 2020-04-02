package com.eugene.sumarry.aopsumioc;

import org.apache.dubbo.common.extension.ExtensionLoader;

public class Entry {

    public static void main(String[] args) {
        ExtensionLoader<OrderService> extensionLoader = ExtensionLoader.getExtensionLoader(OrderService.class);

        OrderService orderService = extensionLoader.getExtension("orderService");
        orderService.findOrders();

    }
}
