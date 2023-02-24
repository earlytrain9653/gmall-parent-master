package com.atguigu.gmall.common.constant;

/**
 * 定义redis常量
 */
public class RedisConst {
    public static final String SKUKEY_PREFIX = "sku:info:";
    
    //设置保存时间 单位：秒
    public static final Integer SKUKEY_TIMEOUT = 24 * 60 * 60;

    public static final String SKUID_BITMAP = "skuids:bitmap";

    public static final String LOCK_SKU = "lock:sku:";
    public static final String CATEGORY_CACHE = "categorys";
    public static final String LOGIN_USER = "login:user:";
    public static final String CART_INFO = "cart:info:";
    public static final Integer CART_ITEM_NUM_LIMIT = 200;
    public static final String REPEAT_TOKEN = "repeat:token:";
    public static final String SECKILL_GOODS_CACHE = "seckill:goods:";
    public static final String SECKILL_CODE = "seckill:code:";
    public static final String SECKILL_ORDER = "seckill:order:";
}
