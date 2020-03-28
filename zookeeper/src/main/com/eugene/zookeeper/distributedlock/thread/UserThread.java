package com.eugene.zookeeper.distributedlock.thread;

import com.eugene.zookeeper.distributedlock.context.SpringContextHolder;
import com.eugene.zookeeper.distributedlock.model.Goods;
import com.eugene.zookeeper.distributedlock.service.GoodsService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class UserThread implements Runnable {

    private static final long GOODS_ID = 1L;

    private static final String ZOOKEEPER_HOST = "192.168.111.145:2181";

    private static final String ROOT_NODE = "/distributed_lock";

    /**
     * 分布式锁思路:
     *   所有的用户在下单前，都在zookeeper的某个根节点中创建一个临时顺序节点,
     *   定义这样一个规则: 一次选取节点名字最小的顺序节点作为锁
     *
     *   若200个线程进来, 他们同时会在zookeeper创建200个临时顺序节点，
     *   要怎样定义线程获取到锁了呢。
     *   创建成功,
     *
     */
    @Override
    public void run() {
        createOrder();
    }

    private void createOrder() {
        // 开始下单，准备减库存
        GoodsService goodsService = SpringContextHolder.getBean(GoodsService.class);
        Goods goods = goodsService.get(GOODS_ID);
        if (goods.getGoodsStock() > 0) {
            // 开始下单，准备减库存
            goodsService.reduce(1L, GOODS_ID);
            System.out.println(Thread.currentThread().getName() + "减库存成功");
        }
    }

}
