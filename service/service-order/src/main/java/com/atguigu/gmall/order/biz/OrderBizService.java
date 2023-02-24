package com.atguigu.gmall.order.biz;

import com.atguigu.gmall.mq.ware.WareStockResultMsg;
import com.atguigu.gmall.order.entity.OrderInfo;
import com.atguigu.gmall.order.vo.OrderConfirmRespVo;
import com.atguigu.gmall.order.vo.OrderSplitResp;
import com.atguigu.gmall.order.vo.OrderSubmitVo;

import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-21 22:06 星期三
 * description:
 */
public interface OrderBizService {
    /**
     * 获取订单确认数据
     * @return
     */
    OrderConfirmRespVo getConfirmData();

    /**
     * 下订单
     * @param submitVo
     * @param tradeNo
     * @return
     */
    Long submitOrder(OrderSubmitVo submitVo, String tradeNo);


    /**
     * 关闭订单
     * @param id
     * @param userId
     */
    void closeOrder(Long id, Long userId);

    /**
     * 订单修改为已支付
     * @param out_trade_no
     * @param userId
     */
    void payedOrder(String out_trade_no, Long userId);

    void updateOrderStockStatus(WareStockResultMsg result);

    /**
     * 拆单
     * @param orderId
     * @param json
     * @return
     */
    List<OrderSplitResp> orderSplit(Long orderId, String json);

    /**
     * 保存秒杀单
     * @param info
     * @return
     */
    Long saveSeckillOrder(OrderInfo info);
}
