package com.atguigu.gmall.product.init;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.service.SkuInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-06 21:16 星期二
 * description:
 */
@Component
@Slf4j
public class InitRunner implements CommandLineRunner {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    SkuInfoService skuInfoService;

    public void initBitMap(){
        log.info("正在初始化 skuid-bitmap");
        //将上架的商品放入到bitmap中
        List<SkuInfo> list = skuInfoService.lambdaQuery()
                .eq(SkuInfo::getIsSale,1)
                .select(SkuInfo::getId)
                .list();
        list.stream().forEach((item) -> {
            redisTemplate.opsForValue().setBit(RedisConst.SKUID_BITMAP,item.getId(),true);
        });
        log.info("初始化 skuid-bitmap 完成");
    }
    @Override
    public void run(String... args) throws Exception {
        log.info("InitRunner  启动run");
        this.initBitMap();
    }
}
