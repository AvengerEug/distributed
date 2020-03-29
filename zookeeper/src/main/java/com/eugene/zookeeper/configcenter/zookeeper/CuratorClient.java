package com.eugene.zookeeper.configcenter.zookeeper;

import com.eugene.zookeeper.configcenter.constants.Constants;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;

public class CuratorClient {

    public static CuratorFramework getClient() {
        CuratorFramework client = CuratorFrameworkFactory.newClient(Constants.ZOOKEEPER_HOST, new RetryNTimes(2, 2000));
        client.start();
        return client;

    }
}
