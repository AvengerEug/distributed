package com.eugene.zookeeper.configcenter;

import com.eugene.zookeeper.configcenter.cache.CacheProperties;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Entry {

    public static void main(String[] args) throws InterruptedException {
        CacheProperties cacheProperties = new CacheProperties();

        while (true) {
            cacheProperties.println();
            TimeUnit.SECONDS.sleep(5);
        }
    }
}
