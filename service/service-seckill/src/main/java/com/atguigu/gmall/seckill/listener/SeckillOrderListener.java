package com.atguigu.gmall.seckill.listener;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.config.mq.MQService;
import com.atguigu.gmall.common.constant.MqConst;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.mq.seckill.SeckillOrderMsg;
import com.atguigu.gmall.seckill.entity.SeckillGoods;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


/**
 * @author 杨林
 * @create 2022-12-28 18:41 星期三
 * description:监听秒杀排队请求  并下秒杀单
 */
@Slf4j
@Service
public class SeckillOrderListener {

    @Autowired
    SeckillGoodsService seckillGoodsService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    MQService mqService;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = MqConst.SECKILL_ORDER_QUEUE,durable = "true",
                            autoDelete = "false",exclusive = "false"),
                    exchange = @Exchange(value = MqConst.SECKILL_EVENT_EXCHANGE,
                            durable = "true",autoDelete = "false",type = "topic"),
                    key = MqConst.SECKILL_ORDER_RK
            )
    })
    public void listener(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        String content = new String(message.getBody());
        SeckillOrderMsg msg = JSON.parseObject(content, SeckillOrderMsg.class);
        log.info("秒杀单排队请求：{}",msg);

        SeckillGoods detail = seckillGoodsService.getDetail(msg.getSkuId());

        try {
            //1.扣库存
            seckillGoodsService.deduceStock(detail.getId());
            //2.临时保存一个秒杀单数据
            seckillGoodsService.saveSeckillOrder(msg);
            //如果秒杀成功  除了扣库存 redis中还有临时的订单数据

            //4.跟新Redis中的库存标识
            seckillGoodsService.updateRedisStock(msg);
            channel.basicAck(tag,false);
        }catch (Exception e){
            //扣库存异常   说明库存没有了
            //3.给Redis随便保存一个占位符
            redisTemplate.opsForValue().set(RedisConst.SECKILL_ORDER+msg.getCode(),"x",
                    2, TimeUnit.DAYS);
            log.warn("秒杀失败");
        }

    }
}
