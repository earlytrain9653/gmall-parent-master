package com.atguigu.gmall.order.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.UserAuthUtils;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.order.entity.OrderInfo;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.vo.OrderConfirmRespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 杨林
 * @create 2022-12-21 22:03 星期三
 * description:
 */
@RestController
@RequestMapping("/api/inner/rpc/order")
public class OrderRpcController {

    @Autowired
    OrderBizService orderBizService;

    @Autowired
    OrderInfoService orderInfoService;

    /**
     * 获取订单确认页数据
     * @return
     */
    @GetMapping("/confirmdata")
    public Result<OrderConfirmRespVo> orderConfirmData(){

        OrderConfirmRespVo respVo = orderBizService.getConfirmData();
        return Result.ok(respVo);
    }


    /**
     * 根据订单id查询订单
     * @param id
     * @return
     */
    @GetMapping("/info/{id}")
    public Result<OrderInfo> grtOrderInfoById(@PathVariable("id") Long id){
        Long userId = UserAuthUtils.getUserId();
        OrderInfo orderInfo = orderInfoService.getOrderInfoById(id,userId);
        return Result.ok(orderInfo);
    }


    /**
     * 保存秒杀单数据
     * @param info
     * @return
     */
    @PostMapping("/seckill/order")
    public Result<Long> saveSeckillOrder(@RequestBody OrderInfo info){

        Long orderId = orderBizService.saveSeckillOrder(info);
        return Result.ok();
    }

}
