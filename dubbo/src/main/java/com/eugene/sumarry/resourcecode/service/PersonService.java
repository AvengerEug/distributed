package com.eugene.sumarry.resourcecode.service;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.SPI;

@SPI
public interface PersonService {

    void say(URL url);
}
