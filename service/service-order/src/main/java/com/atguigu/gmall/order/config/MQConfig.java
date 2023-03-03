package com.atguigu.gmall.order.config;

import com.atguigu.gmall.common.constant.MqConst;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 杨林
 * @create 2022-12-24 13:47 星期六
 * description:
 */
@Configuration
public class MQConfig {


    @Bean
    public Exchange orderEventExchange(){
        return new TopicExchange(MqConst.ORDER_EVENT_EXCHANGE,true,false,
                null);
    }

    @Bean  //延迟队列  不能让任何人监听
    public Queue orderDelayQueue(){
        Map<String,Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange",MqConst.ORDER_EVENT_EXCHANGE);
        arguments.put("x-dead-letter-routing-key",MqConst.ORDER_TIMEOUT_RK);
        arguments.put("x-message-ttl",MqConst.ORDER_TTL);
        return new Queue(MqConst.ORDER_DELAY_QUEUE,true,false,false,arguments);
    }

    //订单到延时队列的键
    @Bean
    public Binding delayBinding(){
        return new Binding(MqConst.ORDER_DELAY_QUEUE,
                Binding.DestinationType.QUEUE,
                MqConst.ORDER_EVENT_EXCHANGE,
                MqConst.ORDER_CREATE_RK,
                null);
    }

    @Bean   //死信队列  消费者监听
    public Queue orderDeadQueue(){
        return new Queue(MqConst.ORDER_DEAD_QUEUE,true,false,false,null);
    }

    //延时队列到死信队列的键
    @Bean
    public Binding deadBinding(){
        return new Binding(MqConst.ORDER_DEAD_QUEUE,
                Binding.DestinationType.QUEUE,
                MqConst.ORDER_EVENT_EXCHANGE,
                MqConst.ORDER_TIMEOUT_RK,
                null);
    }


    /**
     * 支付成功单队列
     * @return
     */
    @Bean
    public Queue payedQueue(){
        return new Queue(MqConst.ORDER_PAYED_QUEUE,
                true,false,false);
    }


    @Bean
    public Binding payedBinding(){
        return new Binding(
                MqConst.ORDER_PAYED_QUEUE,
                Binding.DestinationType.QUEUE,
                MqConst.ORDER_EVENT_EXCHANGE,
                MqConst.ORDER_PAYED_RK,
                null
        );
    }

}
