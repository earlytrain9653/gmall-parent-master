package com.atguigu.gmall.starter.cache.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.starter.cache.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.concurrent.*;

/**
 * @author 杨林
 * @create 2022-12-06 15:19 星期二
 * description: 封装获取缓存的方法
 */
@Service
@Slf4j
public class CacheServiceImpl implements CacheService {

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 获取Redis中的缓存
     * @return
     */
//    @Override
//    public SkuDetailVo getRedisCache(Long skuId) {
//        log.info("正在查询缓存");
//        String jsonString = redisTemplate.opsForValue().get(RedisConst.SKUKEY_PREFIX + skuId);
//        if (jsonString == null){
//            return null;
//        }else if("x".equals(jsonString)){
////            return null;
//            //如果是假没有  则返回一个空对象
//            return new SkuDetailVo();
//        }else{
//            SkuDetailVo skuDetailVo = JSON.parseObject(jsonString, SkuDetailVo.class);
//            return skuDetailVo;
//        }
//
//    }

//    /**
//     * 将数据保存到Redis中
//     * @param returnValue
//     */
//    @Override
//    public void saveData(Object returnValue,Long skuId) {
//        String json = null;
//        if (returnValue == null){
//            json = "x";
//        }else{
//            //将对象转换为json
//            json = JSON.toJSONString(returnValue);
//        }
//
//        //保存到redis中
//        redisTemplate.opsForValue().set(RedisConst.SKUKEY_PREFIX + skuId,json,7, TimeUnit.DAYS);
//
//    }

//    /**
//     * 判断位图中是否含有数据
//     * @param skuId
//     * @return
//     */
//    @Override
//    public Boolean mightContain(Long skuId) {
//        Boolean bit = redisTemplate.opsForValue().getBit(RedisConst.SKUID_BITMAP, skuId);
//        return bit;
//    }


    /**
     * 从缓存中获取指定类型的数据
     * @param key
     * @param returnType
     * @return
     */
    @Override
    public Object getCacheData(String key, Type returnType) {
        log.info("正在查询缓存");
        String jsonString = redisTemplate.opsForValue().get(key);
        if (jsonString == null){
            return null;
        }else if("x".equals(jsonString)){
//            return null;
            //如果是假没有  则返回一个空对象
            return new Object();
        }else{
            return JSON.parseObject(jsonString, returnType);
        }
    }

    /**
     * 判定指定的位图中 有没有bitmapIndex位置的数据
     * @param bitMapName
     * @param bitmapIndex
     * @return
     */
    @Override
    public boolean mightContain(String bitMapName, Long bitmapIndex) {
        Boolean bit = redisTemplate.opsForValue().getBit(bitMapName, bitmapIndex);
        return bit.booleanValue();
    }

    /**
     * 将指定key的数据放入缓存中
     * @param proceed
     * @param cacheKey
     */
    @Override
    public void saveCacheData(Object proceed, String cacheKey,long ttl, TimeUnit unit) {
        String json = null;
        if (proceed == null){
            json = "x";
        }else{
            //将对象转换为json
            json = JSON.toJSONString(proceed);
        }

        //保存到redis中
        redisTemplate.opsForValue().set(cacheKey,json,ttl, unit);
    }

    /**
     * 延迟双删的实现
     * @param cacheKey
     */
    //定时调度线程池
    ScheduledExecutorService pool = Executors.newScheduledThreadPool(16);
    @Override
    public void delayDoubleDel(String cacheKey) {

        //第一次删
        redisTemplate.delete(cacheKey);

        // 避免用户发送请求之后等待  使用异步处理任务
        //缺点：如果修改任务太多  睡眠时间占用大量资源
//        CompletableFuture.runAsync(() -> {
//            try {
//                TimeUnit.SECONDS.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            redisTemplate.delete(cacheKey);
//        });

        //这种方法是JVM利用时间片算法对CPU资源进行调度 不会因为休眠而造成CPU资源浪费
        pool.schedule(() -> {
            //第二次删
            redisTemplate.delete(cacheKey);
        },10,TimeUnit.SECONDS);
    }


    /**
     * 修改指定Bitmap中bitmapIndex位置的数据
     * @param bitMapName
     * @param bitmapIndex
     * @return
     */
    @Override
    public boolean updateBitmap(String bitMapName, Long bitmapIndex,boolean status) {
        Boolean setBit = redisTemplate.opsForValue().setBit(bitMapName, bitmapIndex, status);
        return setBit;
    }
}
