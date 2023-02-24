package com.atguigu.gmall.order.listener;
import com.atguigu.gmall.order.entity.OrderDetail;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.gmall.common.config.mq.MQService;
import com.atguigu.gmall.common.constant.MqConst;
import com.atguigu.gmall.mq.ware.WareStockMsg;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.order.entity.OrderInfo;
import com.atguigu.gmall.order.entity.PaymentInfo;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.service.PaymentInfoService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 杨林
 * @create 2022-12-25 20:38 星期日
 * description:
 */
@Slf4j
@Service
public class OrderPayedListener {

    @Autowired
    MQService mqService;

    @Autowired
    OrderBizService orderBizService;

    @Autowired
    PaymentInfoService paymentInfoService;

    @Autowired
    OrderInfoService orderInfoService;

    @Autowired
    OrderDetailService orderDetailService;

    /**
     * 监听所有成功单
     */
    @RabbitListener(queues = MqConst.ORDER_PAYED_QUEUE)
    public void listener(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        String json = new String(message.getBody());
        try {

           Map<String, String> content = JSON.parseObject(json, new TypeReference<Map<String, String>>() {
           });

           log.info("修改订单状态为已支付");
           //1.订单唯一对外交易号
           String out_trade_no = content.get("out_trade_no");

           String[] split = out_trade_no.split("-");
           //2.还知道用户id
           Long userId = Long.parseLong(split[split.length - 1]);

           //根据唯一对外交易号和用户id  修改订单为已支付状态
           orderBizService.payedOrder(out_trade_no,userId);

           // 保存此次支付回调的数据信息
            PaymentInfo info = preparePaymentInfo(json, content, out_trade_no, userId);

            paymentInfoService.save(info);

            //怎么实现支付后锁定库存  解决：发送消息 通知库存系统扣减库存
            WareStockMsg msg = prepareWareStockMsg(out_trade_no,userId);
            mqService.send(msg,MqConst.WARE_STOCK_EXCHANGE,MqConst.WARE_STOCK_RK);
            channel.basicAck(tag,false);

       }catch (Exception e){
            mqService.retry(channel,tag,json,5);
       }

    }


    private WareStockMsg prepareWareStockMsg(String out_trade_no, Long userId) {
        OrderInfo info = orderInfoService.lambdaQuery()
                .eq(OrderInfo::getOutTradeNo, out_trade_no)
                .eq(OrderInfo::getUserId, userId)
                .one();

        WareStockMsg msg = new WareStockMsg();
        //订单基本信息
        msg.setOrderId(info.getId());
        msg.setUserId(userId);
        msg.setConsignee(info.getConsignee());
        msg.setConsigneeTel(info.getConsigneeTel());
        msg.setOrderComment(info.getOrderComment());
        msg.setOrderBody(info.getTradeBody());
        msg.setDeliveryAddress(info.getDeliveryAddress());
        msg.setPaymentWay("2");


        //2.订单的明细（购买了哪些商品）
        List<OrderDetail> details = orderDetailService.lambdaQuery()
                .eq(OrderDetail::getOrderId, info.getId())
                .eq(OrderDetail::getUserId, info.getUserId())
                .list();

        List<WareStockMsg.Sku> skus = details.stream().map(item -> {
            WareStockMsg.Sku sku = new WareStockMsg.Sku();
            sku.setSkuId(item.getSkuId());
            sku.setSkuNum(item.getSkuNum());
            sku.setSkuName(item.getSkuName());
            return sku;
        }).collect(Collectors.toList());

        msg.setDetails(skus);

        return msg;
    }

    private PaymentInfo preparePaymentInfo(String json, Map<String, String> content, String out_trade_no, Long userId) {
        OrderInfo orderInfo = orderInfoService.lambdaQuery()
                .eq(OrderInfo::getUserId, userId)
                .eq(OrderInfo::getOutTradeNo, out_trade_no)
                .one();
        PaymentInfo info = new PaymentInfo();
        info.setUserId(userId);
        info.setOutTradeNo(out_trade_no);
        info.setOrderId(orderInfo.getId());
        info.setPaymentType(orderInfo.getPaymentWay());
        String trade_no = content.get("trade_no");
        info.setTradeNo(trade_no);
        String total_amount = content.get("total_amount");
        info.setTotalAmount(new BigDecimal(total_amount));
        info.setSubject(content.get("subject"));
        info.setPaymentStatus(content.get("trade_status"));
        info.setCreateTime(new Date());
        info.setCallbackTime(new Date());
        info.setCallbackContent(json);
        return info;
    }
}
