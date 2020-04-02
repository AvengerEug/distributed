package com.eugene.sumarry.ioc;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;

@SPI("test")
public interface IndexService {

    @Adaptive("indexServiceImpl")
    void index(URL url);

    @Adaptive("indexServiceImpl")
    void index2(URL url);

    void index3(URL url);

    void index4();

    @Adaptive
    void index5(URL url);
}
