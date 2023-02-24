package com.atguigu.gmall.seckill.service.impl;
import java.math.BigDecimal;

import com.atguigu.gmall.enums.OrderStatus;
import com.atguigu.gmall.enums.PaymentWay;
import com.atguigu.gmall.enums.ProcessStatus;
import com.atguigu.gmall.order.entity.OrderDetail;
import com.google.common.collect.Lists;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.mq.seckill.SeckillOrderMsg;
import com.atguigu.gmall.order.entity.OrderInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.seckill.entity.SeckillGoods;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import com.atguigu.gmall.seckill.mapper.SeckillGoodsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author 华为YL
* @description 针对表【seckill_goods】的数据库操作Service实现
* @createDate 2022-12-27 20:02:31
*/
@Slf4j
@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods>
    implements SeckillGoodsService{

    @Autowired
    StringRedisTemplate redisTemplate;

    Map<Long,SeckillGoods> localCache = new ConcurrentHashMap<>();

    /**
     * 查询指定日期参与秒杀的所有商品
     * @param date
     * @return
     */
    @Override
    public List<SeckillGoods> getSeckillGoodsByDay(String date) {

        return baseMapper.getSeckillGoodsByDay(date);

    }

    /**
     * 从缓存中查询当天需要参与秒杀的数据
     * @param date
     * @return
     */
    @Override
    public List<SeckillGoods> getSeckillGoodsByDayFromCache(String date) {
        //如果有了多级缓存；先查询离自己最近的缓存
        List<SeckillGoods> cache = localCache.values().stream()
                .sorted((o1, o2) -> o1.getStartTime().compareTo(o2.getStartTime()))
                .collect(Collectors.toList());

        if (cache == null || cache.size() == 0){
            //本地缓存没有命中
            log.info("本地缓存未命中  正在远程查询");
            List<Object> values = redisTemplate.opsForHash().values(RedisConst.SECKILL_GOODS_CACHE + date);
            List<SeckillGoods> goods = values.stream()
                    .map(item -> item.toString())
                    .map(item -> JSON.parseObject(item, SeckillGoods.class))
                    .sorted((o1, o2) -> o1.getStartTime().compareTo(o2.getStartTime()))
                    .collect(Collectors.toList());
            //给本地缓存保存一份
            saveToLocalCache(goods);
            return goods;
        }
        log.info("本地缓存命中。。。。");
        return cache;
    }

    /**
     * 把参与秒杀的商品保存到本地缓存
     * @param goodsByDay
     */
    @Override
    public void saveToLocalCache(List<SeckillGoods> goodsByDay) {
        for (SeckillGoods goods : goodsByDay) {
            localCache.put(goods.getSkuId(),goods);
        }
    }

    /**
     * 查询秒杀商品详情
     * @param skuId
     * @return
     */
    @Override
    public SeckillGoods getDetail(Long skuId) {
        SeckillGoods goods = localCache.get(skuId);
        if (goods != null){
            log.info("本地缓存详情命中");
            return goods;
        }

        //同步redis和本地缓存
        String date = DateUtil.formatDate(new Date());
        getSeckillGoodsByDayFromCache(date);
        goods = localCache.get(skuId);

        return goods;
    }

    /**
     * 扣库存
     * @param id
     */
    @Override
    public void deduceStock(Long id) {
        baseMapper.updateStock(id);
    }


    /**
     * 临时保存秒杀单
     * @param msg
     */
    @Override
    public void saveSeckillOrder(SeckillOrderMsg msg) {

        Long skuId = msg.getSkuId();
        SeckillGoods detail = getDetail(skuId);

        //准备一个订单数据
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setTotalAmount(detail.getCostPrice());
        orderInfo.setOrderStatus(OrderStatus.UNPAID.name());
        orderInfo.setUserId(msg.getUserId());
        orderInfo.setPaymentWay(PaymentWay.ONLINE.name());
        orderInfo.setOutTradeNo("ATGUIGU-"+System.currentTimeMillis()+"-" + msg.getUserId());
        orderInfo.setTradeBody(detail.getSkuName());
        orderInfo.setCreateTime(new Date());
        orderInfo.setExpireTime(new Date(System.currentTimeMillis()+1000 * 60 * 30));
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());

        orderInfo.setParentOrderId(0L);
        orderInfo.setImgUrl(detail.getSkuDefaultImg());

        orderInfo.setOperateTime(new Date());
        orderInfo.setActivityReduceAmount(new BigDecimal("0"));

        //设置秒杀优惠金额
        orderInfo.setCouponAmount(detail.getPrice().subtract(detail.getCostPrice()));

        orderInfo.setOriginalTotalAmount(detail.getCostPrice());
        orderInfo.setFeightFee(new BigDecimal("0"));

        //设置这次秒杀的所有商品
        List<OrderDetail> orderDetails = new ArrayList<>();
        OrderDetail orderDetail = new OrderDetail();

        orderDetail.setUserId(msg.getUserId());
        orderDetail.setSkuId(msg.getSkuId());
        orderDetail.setSkuName(detail.getSkuName());
        orderDetail.setImgUrl(detail.getSkuDefaultImg());
        orderDetail.setOrderPrice(detail.getCostPrice());
        orderDetail.setSkuNum(1);
        orderDetail.setCreateTime(new Date());
        orderDetail.setSplitTotalAmount(detail.getCostPrice());
        orderDetail.setSplitActivityAmount(new BigDecimal("0"));
        orderDetail.setSplitCouponAmount(detail.getPrice().subtract(detail.getCostPrice()));

        orderDetails.add(orderDetail);
        orderInfo.setOrderDetails(orderDetails);
        orderInfo.setWareId(0L);

        redisTemplate.opsForValue().set(RedisConst.SECKILL_ORDER + msg.getCode(),
                JSON.toJSONString(orderInfo),2, TimeUnit.DAYS);

    }

    /**
     * 秒杀成功  更新Redis中的库存
     * @param msg
     */
    @Override
    public void updateRedisStock(SeckillOrderMsg msg) {
        Object json = redisTemplate.opsForHash().get(RedisConst.SECKILL_GOODS_CACHE + msg.getDate(),
                msg.getSkuId().toString());

        SeckillGoods goods = JSON.parseObject(json.toString(), SeckillGoods.class);

        //更新了Redis中的库存量
        goods.setStockCount(goods.getStockCount() - 1);
        redisTemplate.opsForHash().put(RedisConst.SECKILL_GOODS_CACHE + msg.getDate(),
                msg.getSkuId().toString(),JSON.toJSONString(goods));
    }
}




