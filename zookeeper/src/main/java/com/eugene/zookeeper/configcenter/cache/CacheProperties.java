package com.eugene.zookeeper.configcenter.cache;

import com.eugene.zookeeper.configcenter.zookeeper.CuratorClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ConcurrencyThrottleSupport;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheProperties {

    private Map<String, Object> cache = new HashMap<>();
    private CuratorFramework zookeeperClient;
    private final String CONFIG_NODE = "/distributed_config";

    {
        init();
    }

    private void connectZookeeper() {
        zookeeperClient = CuratorClient.getClient();
    }

    private void init() {
        connectZookeeper();
        prepareEnv();
        syncCache();
    }

    private void prepareEnv() {
        try {
            Stat stat = zookeeperClient.checkExists().forPath(CONFIG_NODE);
            if (ObjectUtils.isEmpty(stat)) {
                zookeeperClient.create().forPath(CONFIG_NODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将zookeeper中/distributed_config节点下的所有子节点以及存储的key全部加载到内存中
     * 并对所有子节点添加监听者，当子节点 增加、删除、更新时再同步至本地的内存
     */
    public void syncCache() {
        try {
            // 获取/distributed_config节点下的所有子节点
            List<String> childrenNode = zookeeperClient.getChildren().forPath(CONFIG_NODE);
            for (String configKey : childrenNode) {
                String pathKey = "/" + configKey;
                String value = new String(zookeeperClient.getData().forPath(CONFIG_NODE + pathKey));
                set(configKey, value);
            }

            PathChildrenCache pathChildrenCache = new PathChildrenCache(zookeeperClient, CONFIG_NODE, true);
            pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    // 全路径, 类似于 /distributed_config/test
                    String childrenName = event.getData().getPath();

                    // 获取子节点的名字当成配置的key
                    String propertyKey = childrenName.replace(CONFIG_NODE + "/", "");
                    switch (event.getType()) {
                        case CHILD_ADDED:
                            set(propertyKey, new String(event.getData().getData()));
                            break;
                        case CHILD_UPDATED:
                            set(propertyKey, new String(event.getData().getData()));
                            break;
                        case CHILD_REMOVED:
                            delete(propertyKey);
                            break;
                            default: // Nothing to do
                                break;
                    }
                }
            });

            // 要记得开启监听器
            pathChildrenCache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object get(String key) {
        return cache.get(key);
    }

    public void set(String key, Object value) {
        cache.put(key, value);
    }

    public void delete(String key) {
        cache.remove(key);
    }

    public void println() {
        System.out.println(cache);
    }
}
