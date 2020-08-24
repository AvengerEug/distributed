package com.eugene.sumarry.dubbostudy.provider.service.impl;

import com.eugene.sumarry.dubbostudy.provider.service.DemoService;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.rpc.RpcContext;

// 指定为轮询，可能会出现缓存的原因，猜测是zookeeper配置没更新过来
@Service(loadbalance = "roundrobin")
public class DemoServiceImpl implements DemoService {

    @Override
    public String say(String content) {
        URL url = RpcContext.getContext().getUrl();

        return "调用者: " + url.getAddress() + " 传入参数: " + content + " 协议: " + url.getProtocol();
    }

    @Override
    public String say2(String content) {
        URL url = RpcContext.getContext().getUrl();

        return "调用者: " + url.getAddress() + " 传入参数: " + content + " 协议: " + url.getProtocol();
    }
}
