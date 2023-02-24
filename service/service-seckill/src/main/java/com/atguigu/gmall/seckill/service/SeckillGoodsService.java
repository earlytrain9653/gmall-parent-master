package com.atguigu.gmall.seckill.service;

import com.atguigu.gmall.mq.seckill.SeckillOrderMsg;
import com.atguigu.gmall.seckill.entity.SeckillGoods;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 华为YL
* @description 针对表【seckill_goods】的数据库操作Service
* @createDate 2022-12-27 20:02:31
*/
public interface SeckillGoodsService extends IService<SeckillGoods> {

    /**
     * 查询指定日期参与秒杀的所有商品
     * @param date
     * @return
     */
    List<SeckillGoods> getSeckillGoodsByDay(String date);

    /**
     * 从缓存中查询当天需要参与秒杀的数据
     * @param date
     * @return
     */
    List<SeckillGoods> getSeckillGoodsByDayFromCache(String date);

    /**
     * 把参与秒杀的商品保存到本地缓存
     * @param goodsByDay
     */
    void saveToLocalCache(List<SeckillGoods> goodsByDay);

    /**
     * 查询秒杀商品详情
     * @param skuId
     * @return
     */
    SeckillGoods getDetail(Long skuId);

    /**
     * 扣库存
     * @param id
     */
    void deduceStock(Long id);

    /**
     * 临时保存秒杀单
     * @param msg
     */
    void saveSeckillOrder(SeckillOrderMsg msg);

    /**
     * 秒杀成功  更新Redis中的库存
     * @param msg
     */
    void updateRedisStock(SeckillOrderMsg msg);
}
