package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.mq.ware.WareStockMsg;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.order.vo.OrderSplitResp;
import com.atguigu.gmall.order.vo.OrderSubmitVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-23 17:27 星期五
 * description:
 */
@RestController
@RequestMapping("/api/order")
public class OrderApiController {

    @Autowired
    OrderBizService orderBizService;

    /**
     * 拆单
     * @return
     */
    @PostMapping("/orderSplit")
    public List<OrderSplitResp> orderSplit(@RequestParam("orderId") Long orderId,
                                           @RequestParam("wareSkuMap") String json){

        List<OrderSplitResp> splitReps = orderBizService.orderSplit(orderId,json);
        return splitReps;
    }

    /**
     * 提交订单
     * @param tradeNo
     * @param submitVo
     * @return
     */
    @PostMapping("/auth/submitOrder")
    public Result submitOrder(@RequestParam("tradeNo") String tradeNo,
                              @Valid @RequestBody OrderSubmitVo submitVo){

        //下单方法
        Long orderId = orderBizService.submitOrder(submitVo,tradeNo);
        //订单id由雪花算法生成，返回前端时返回字符串
        return Result.ok(orderId + "");
    }

}
