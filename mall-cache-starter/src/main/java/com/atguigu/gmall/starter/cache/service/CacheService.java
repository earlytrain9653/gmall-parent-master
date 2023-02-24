package com.atguigu.gmall.starter.cache.service;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

/**
 * @author 杨林
 * @create 2022-12-06 15:19 星期二
 * description:
 */
public interface CacheService {
//    /**
//     * 获取Redis中的缓存
//     * @return
//     */
//    SkuDetailVo getRedisCache(Long skuId);

//
//    void saveData(Object returnValue,Long skuId);

//    /**
//     * 判断位图中是否含有数据
//     * @param skuId
//     * @return
//     */
//    Boolean mightContain(Long skuId);


    /**
     * 从缓存中获取指定类型的数据
     * @param s
     * @param returnType
     * @return
     */
    Object getCacheData(String s, Type returnType);

    /**
     * 判定指定的位图中 有没有bitmapIndex位置的数据
     * @param bitMapName
     * @param bitmapIndex
     * @return
     */
    boolean mightContain(String bitMapName, Long bitmapIndex);

    /**
     * 将指定key的数据放入缓存中
     * @param proceed
     * @param cacheKey
     */
    void saveCacheData(Object proceed, String cacheKey, long ttl, TimeUnit unit);


    /**
     * 延迟双删
     * @param cacheKey
     */
    void delayDoubleDel(String cacheKey);


    /**
     * 修改指定位图中 bitmapIndex位置的数据
     * @return
     */
    public boolean updateBitmap(String bitMapName, Long bitmapIndex,boolean status);


}
