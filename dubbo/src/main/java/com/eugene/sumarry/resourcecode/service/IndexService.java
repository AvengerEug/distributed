package com.eugene.sumarry.resourcecode.service;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;

@SPI
public interface IndexService {

    @Adaptive("aaaaa")
    void index(URL url);
}
