package com.eugene.zookeeper.distributedlock.service;

import com.eugene.zookeeper.distributedlock.model.Goods;

public interface GoodsService {

    Goods get(Long goodsId);

    void reduce(Long count, Long goodsId);
}
