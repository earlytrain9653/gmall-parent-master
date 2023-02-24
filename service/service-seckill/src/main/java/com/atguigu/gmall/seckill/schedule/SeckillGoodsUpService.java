package com.atguigu.gmall.seckill.schedule;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.seckill.entity.SeckillGoods;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 杨林
 * @create 2022-12-27 21:06 星期二
 * description:
 */
@Slf4j
@Service
public class SeckillGoodsUpService {

    @Autowired
    SeckillGoodsService seckillGoodsService;

    @Autowired
    StringRedisTemplate redisTemplate;

    //每天晚上两点上架当天（两天内）参与秒杀的所有商品
    //秒  分  时  日   月   周
//    @Scheduled(cron = "0 0 2 * * ?")
    @Scheduled(cron = "0 * * * * ?")
    public void upgoods(){
        log.info("上架当天参与秒杀的所有商品");
        String date = DateUtil.formatDate(new Date());
        //查询当天参与秒杀的商品
        List<SeckillGoods> goodsByDay = seckillGoodsService.getSeckillGoodsByDay(date);

        String cacheKey = RedisConst.SECKILL_GOODS_CACHE + date;

        //缓存到Redis中
        for (SeckillGoods goods : goodsByDay) {
            redisTemplate.opsForHash().put(cacheKey,
                    goods.getSkuId().toString(),
                    JSON.toJSONString(goods));
        }
        redisTemplate.expire(cacheKey,2, TimeUnit.DAYS);

        //3.把当天参与秒杀的所有商品同步到本地缓存中
        seckillGoodsService.saveToLocalCache(goodsByDay);
    }
}
