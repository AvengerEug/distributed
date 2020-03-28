package com.eugene.zookeeper.distributedlock.dao;

import com.eugene.zookeeper.distributedlock.model.Goods;

import java.util.Map;

public interface GoodsDao {

    Goods getById(Long goodsId);

    void reduceStock(Map<String, Object> map);
}
