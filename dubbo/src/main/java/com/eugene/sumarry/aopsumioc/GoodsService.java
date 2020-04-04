package com.eugene.sumarry.aopsumioc;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;

@SPI
public interface GoodsService {

    @Adaptive("goodsService")
    void findGoods(URL url);
}
