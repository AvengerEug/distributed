package com.eugene.zookeeper.distributedlock;

import com.eugene.zookeeper.distributedlock.conf.AppConfig;
import com.eugene.zookeeper.distributedlock.model.Goods;
import com.eugene.zookeeper.distributedlock.service.GoodsService;
import com.eugene.zookeeper.distributedlock.thread.UserThread;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.concurrent.ThreadPoolExecutor;

public class Entry {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        for (int i = 1; i <= 30; i++) {
            new Thread(new UserThread(), "用户" + i).start();
        }

    }
}
