package com.atguigu.gmall.seckill.biz.impl;
import java.math.BigDecimal;

import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.feign.user.UserFeignClient;
import com.atguigu.gmall.seckill.vo.SeckillOrderSubmitVo;
import com.atguigu.gmall.user.entity.UserAddress;
import com.google.common.collect.Lists;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.config.mq.MQService;
import com.atguigu.gmall.common.constant.MqConst;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.common.util.UserAuthUtils;
import com.atguigu.gmall.mq.seckill.SeckillOrderMsg;
import com.atguigu.gmall.order.entity.OrderInfo;
import com.atguigu.gmall.seckill.biz.SeckillBizService;
import com.atguigu.gmall.seckill.entity.SeckillGoods;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import com.atguigu.gmall.seckill.vo.SeckillOrderConfirmVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 杨林
 * @create 2022-12-27 22:47 星期二
 * description:
 */
@Slf4j
@Service
public class SeckillBizServiceImpl implements SeckillBizService {


    @Autowired
    SeckillGoodsService seckillGoodsService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    MQService mqService;

    @Autowired
    UserFeignClient userFeignClient;

    @Autowired
    OrderFeignClient orderFeignClient;

    /**
     * 生成秒杀码
     * @param skuId
     * @return
     */
    @Override
    public String generateSeckillCode(Long skuId) {
        //做一堆前置的合法性校验
        //1.先获取秒杀的商品数据
        SeckillGoods detail = seckillGoodsService.getDetail(skuId);

        //2.校验
        Date current = new Date();
        //【开始时间校验】
        if (!current.after(detail.getStartTime())){
            //秒杀还没开始
            throw new GmallException(ResultCodeEnum.SECKILL_NO_START);
        }

        //【结束时间校验】
        if (!current.before(detail.getEndTime())){
            //秒杀已经结束
            throw new GmallException(ResultCodeEnum.SECKILL_END);
        }

        //【快速校验库存】 只需要看本地缓存
        //本地内存都没有库存  数据库一定没有  本地内存有库存   数据库不一定有
        if (detail.getStockCount() <= 0) {
            throw new GmallException(ResultCodeEnum.SECKILL_FINISH);
        }

        //允许参与后续秒杀  生成一个秒杀码
        String code = generateCode(skuId);

        //给Redis缓存一下这个码  后续用户带的码和Redis中的码进行比对  防止秒杀脚本
        redisTemplate.opsForValue().setIfAbsent(RedisConst.SECKILL_CODE + code,"1",2, TimeUnit.DAYS);

        return code;
    }

    /**
     * 秒杀下单
     * @param skuId
     * @param skuIdStr
     */
    //1.大家都去seckill_goods里面抢库存  不能扣超了
    //2.不要把100w并发抢商品都放给数据库扣库存  数据库受不了
    @Override
    public void seckillOrder(Long skuId, String skuIdStr) {
        //合法性校验

        SeckillGoods detail = seckillGoodsService.getDetail(skuId);
        Date date = new Date();
        //1.校验商品是否秒杀开始了
        if (!date.after(detail.getStartTime())){
            //秒杀还没开始
            throw new GmallException(ResultCodeEnum.SECKILL_NO_START);
        }

        //2.校验秒杀商品是否结束了
        if (!date.before(detail.getEndTime())){
            //秒杀结束了
            throw new GmallException(ResultCodeEnum.SECKILL_END);
        }

        //3.快速校验库存:内存说没库存  就一定没库存
        if (detail.getStockCount() <= 0) {
            throw new GmallException(ResultCodeEnum.SECKILL_FINISH);
        }

        //【秒杀码要进行双校验  （不仅客户端带来的码与用算法生成的当前skuId对应的码要一样 而且Redis中也要有）】
        //4.校验秒杀码
        String generateCode = generateCode(skuId);
        if (!generateCode.equals(skuIdStr)){
            //说明秒杀码是伪造的
            throw new GmallException(ResultCodeEnum.SECKILL_ILLEGAL);
        }

        //5.Redis中是否存在
        if (!redisTemplate.hasKey(RedisConst.SECKILL_CODE+skuIdStr)) {
            //Redis中没有  还是非法请求
            throw new GmallException(ResultCodeEnum.SECKILL_ILLEGAL);
        }

        //统计同一个秒杀请求的数量   幂等快速扣库存
        Long increment = redisTemplate.opsForValue().increment(RedisConst.SECKILL_CODE + skuIdStr);
        if (increment <= 2){
            //以上校验都正确  说明秒杀请求是合法的  就可以开始排队（下秒杀单）
            //给MQ发送一个排队消息  并且内存库存状态-1
            detail.setStockCount(detail.getStockCount() - 1);  //同一个用户同一个商品只扣一次
            SeckillOrderMsg orderMsg = new SeckillOrderMsg();
            orderMsg.setUserId(UserAuthUtils.getUserId());
            orderMsg.setCode(skuIdStr);
            orderMsg.setSkuId(skuId);
            orderMsg.setDate(DateUtil.formatDate(new Date()));

            //发送消息：如果这个用户已经发过这个秒杀请求了  就不用在发下秒杀单的消息了
            mqService.send(orderMsg, MqConst.SECKILL_EVENT_EXCHANGE,MqConst.SECKILL_ORDER_RK);
        }else {
            log.info("请求已经发过了。。。。");
        }
    }

    /**
     * 检查秒杀单状态
     * @param skuId
     * @return
     */
    @Override
    public ResultCodeEnum checkOrder(Long skuId) {

        String code = generateCode(skuId);

        //1.去Redis中先看看有没有秒杀单临时数据   如果有 就说明秒杀成功了
        String json = redisTemplate.opsForValue().get(RedisConst.SECKILL_ORDER + code);
        if ("x".equals(json)){
            //之前秒杀的时候扣库存失败了
            return ResultCodeEnum.SECKILL_FINISH;
        }

        if (!StringUtils.isEmpty(json)) {
            OrderInfo orderInfo = JSON.parseObject(json, OrderInfo.class);
            if (StringUtils.isEmpty(orderInfo.getDeliveryAddress())) {
                //没有收货地址
                return ResultCodeEnum.SECKILL_SUCCESS;
            } else {
                //有收货地址
                return ResultCodeEnum.SECKILL_ORDER_SUCCESS;
            }

        }

        String count = redisTemplate.opsForValue().get(RedisConst.SECKILL_CODE + code);
        if (Long.parseLong(count) > 1){
            return ResultCodeEnum.SECKILL_RUN;
        }

        return ResultCodeEnum.SECKILL_FAIL;
    }

    /**
     * 获取秒杀单数据
     * @param code
     * @return
     */
    @Override
    public SeckillOrderConfirmVo getSeckillOrderInfo(String code) {
        String json = redisTemplate.opsForValue().get(RedisConst.SECKILL_ORDER + code);
        OrderInfo orderInfo = JSON.parseObject(json, OrderInfo.class);
        //准备数据
        SeckillOrderConfirmVo confirmVo = new SeckillOrderConfirmVo();
        confirmVo.setDetailArrayList(orderInfo.getOrderDetails());
        confirmVo.setTotalNum(1);
        confirmVo.setTotalAmount(orderInfo.getTotalAmount());

        Long userId = UserAuthUtils.getUserId();
        List<UserAddress> data = userFeignClient.getUserAddress(userId).getData();
        confirmVo.setUserAddressList(data);

        return confirmVo;
    }

    /**
     * 提交秒杀单
     * @param submitVo
     * @return
     */
    @Override
    public Long submitOrder(SeckillOrderSubmitVo submitVo) {
        //1.获取到秒杀码
        String code = submitVo.getCode();

        //2.去Redis拿到秒杀单数据
        String json = redisTemplate.opsForValue().get(RedisConst.SECKILL_ORDER + code);

        //3.转成orderInfo
        OrderInfo orderInfo = JSON.parseObject(json, OrderInfo.class);
        orderInfo.setConsignee(submitVo.getConsignee());
        orderInfo.setDeliveryAddress(submitVo.getDeliveryAddress());
        orderInfo.setConsigneeTel(submitVo.getConsigneeTel());
        orderInfo.setOrderComment(submitVo.getOrderComment());

        //5.远程调用订单服务   创建（保存）秒杀订单
        Long orderId = orderFeignClient.saveSeckillOrder(orderInfo).getData();
        orderInfo.setId(orderId);

        //4.保存到Redis中
        redisTemplate.opsForValue().set(RedisConst.SECKILL_ORDER+code,JSON.toJSONString(orderInfo));

        return orderId;
    }

    //这个算法不要泄露
    private String generateCode(Long skuId){

        String date = DateUtil.formatDate(new Date());
        Long userId = UserAuthUtils.getUserId();
        String str = date + "_" + userId + "_" + skuId;
        String code = MD5.encrypt(str);
        return code;
    }
}
