package com.eugene.zookeeper.distributedlock.service.impl;

import com.eugene.zookeeper.distributedlock.dao.GoodsDao;
import com.eugene.zookeeper.distributedlock.model.Goods;
import com.eugene.zookeeper.distributedlock.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    @Override
    public Goods get(Long goodsId) {
        return goodsDao.getById(goodsId);
    }

    @Override
    public void reduce(Long count, Long goodsId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("count", count);
        map.put("goodsId", goodsId);

        goodsDao.reduceStock(map);
    }
}
