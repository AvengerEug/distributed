package com.eugene.sumarry.dubbostudy.consumer.service.impl;

import com.eugene.sumarry.dubbostudy.consumer.service.ConsumerService;
import com.eugene.sumarry.dubbostudy.provider.service.DemoService;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ConsumerServiceImpl implements ConsumerService {

    @Reference
    private DemoService demoService;

    @Override
    public void consumer() throws InterruptedException {
        int count = 0;
        while (true) {
            System.out.println(demoService.say("world" + ++count));
            TimeUnit.SECONDS.sleep(2);
        }
    }
}
