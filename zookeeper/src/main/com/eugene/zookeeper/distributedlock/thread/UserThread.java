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

    private static final String ZOOKEEPER_HOST = "192.168.111.146:2181";

    private static final String ROOT_NODE = "/distributed_lock";

    private String createdNodeName = null;

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
        CuratorFramework curatorFramework = null;
        try {
            curatorFramework = connect();
            curatorFramework.start();

            if (lock(curatorFramework)) createOrder();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭当前的zookeeper连接，创建的临时节点就会消失
            if (!ObjectUtils.isEmpty(curatorFramework)) {
                curatorFramework.close();
            }
        }
    }

    private boolean lock(CuratorFramework curatorFramework) throws Exception {
        // 每个线程创建一个临时顺序节点, 返回的节点名是全名, eg: /distributed_local/goods_seq0000000001
        createdNodeName = curatorFramework
                .create()
                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath(ROOT_NODE + "/goods_seq");
        System.out.println(currentThreadName() + "创建临时顺序节点: " + createdNodeName);

        // 验证当前节点是不是最小的，若是，则进行减库存操作，否则监听它的上一个节点是否被删除(是否存在), 阻塞

        // 1. 获取根节点的所有孩子节点， 只能获取到孩子节点部分,
        // eg: /distributed_local/test10000, 只能获取到test10000
        // 虽然每个线程的启动顺序获取的子节点个数不同，但没关系，因为后面zookeeper顺序节点的特性
        // 后面创建的节点肯定在后面，至少对当前线程而言，获取到的节点的排序后最后一个一定是自己创建的
        List<String> childrenNodeNames = curatorFramework.getChildren().forPath(ROOT_NODE);
        System.out.println(currentThreadName() + " childrenNodeNames: 未排序" + childrenNodeNames);

        // 2. 排序，保证最小的在最前面
        Collections.sort(childrenNodeNames);
        System.out.println(currentThreadName() + " childrenNodeNames: 排序" + childrenNodeNames);

        // 第一个节点为最小的节点, 并判断是不是自己创建的, ***这里的节点名不带斜杠***
        String minNode = childrenNodeNames.get(0);
        if ((ROOT_NODE + "/" + minNode).equals(createdNodeName)) {
            return true;
        } else {
            // 先获取到当前创建节点的下标
            String createdNodeNamePart = createdNodeName.substring(createdNodeName.lastIndexOf("/") + 1);
            int createdNodeNameIndex = childrenNodeNames.indexOf(createdNodeNamePart);

            // 拿到当前节点的上一个节点 ===> 节点名不带斜杠
            String prevNode = childrenNodeNames.get(createdNodeNameIndex - 1);

            final CountDownLatch countDownLatch = new CountDownLatch(1);

            // 对/distributed_lock/goods_seqxxxxxxxx节点(就是前一个节点)添加监听事件
            // 当前一个节点被删除了, 则轮到自己去拿锁了
            curatorFramework.getData().usingWatcher(new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (Event.EventType.NodeDeleted == watchedEvent.getType()) {
                        countDownLatch.countDown();
                    }
                }
            }).forPath(ROOT_NODE + "/" + prevNode);

            countDownLatch.await();

            // 监听上一个节点是否还存在, 在这儿阻塞, 当线程创建的节点为最小的时候则开始下单
            return true;
        }
    }

    private CuratorFramework connect() {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(ZOOKEEPER_HOST, new RetryNTimes(2, 2000));
        return curatorFramework;
    }

    private void createOrder() {
        // 开始下单，准备减库存
        GoodsService goodsService = SpringContextHolder.getBean(GoodsService.class);
        Goods goods = goodsService.get(GOODS_ID);
        if (goods.getGoodsStock() > 0) {
            // 开始下单，准备减库存
            goodsService.reduce(1L, GOODS_ID);
            System.err.println(currentThreadName() + "减库存成功");
        }
    }

    private String currentThreadName() {
        return Thread.currentThread().getName();
    }

}
