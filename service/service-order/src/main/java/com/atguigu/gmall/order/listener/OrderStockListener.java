package com.atguigu.gmall.order.listener;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.config.mq.MQService;
import com.atguigu.gmall.enums.ProcessStatus;
import com.atguigu.gmall.mq.ware.WareStockResultMsg;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author 杨林
 * @create 2022-12-26 20:43 星期一
 * description:  监听库存扣减结果
 */
@Slf4j
@Service
public class OrderStockListener {

    @Autowired
    OrderBizService orderBizService;

    @Autowired
    MQService mqService;

    /**
     * 也可以用注解的方式声明监听哪个队列   如果没有也会自动创建
     */
    @RabbitListener(bindings = {
            @QueueBinding(value = @Queue(value = "queue.ware.order",durable = "true",exclusive = "false",autoDelete = "false"),
                            exchange = @Exchange(value = "exchange.direct.ware.order",durable = "true",autoDelete = "false"),
                            key = "ware.order"
            )
    })
    public void listen(Message message, Channel channel) throws IOException {
        String content = new String(message.getBody());
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            WareStockResultMsg result = JSON.parseObject(content, WareStockResultMsg.class);
            //2.修改订单状态
            orderBizService.updateOrderStockStatus(result);
            log.info("监听到库存扣减结果：{}",content);
            channel.basicAck(tag,false);
        }catch (Exception e){
            mqService.retry(channel,tag,content,5);
        }


    }
}
