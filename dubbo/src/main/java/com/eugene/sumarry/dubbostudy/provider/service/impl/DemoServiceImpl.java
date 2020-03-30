package com.eugene.sumarry.dubbostudy.provider.service.impl;

import com.eugene.sumarry.dubbostudy.provider.service.DemoService;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.RpcContext;

public class DemoServiceImpl implements DemoService {

    @Override
    public String say(String content) {
        URL url = RpcContext.getContext().getUrl();

        return "调用者: " + url.getAddress() + " 传入参数: " + content + " 协议: " + url.getProtocol();
    }
}
