package com.atguigu.gmall.feign.order;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.order.entity.OrderInfo;
import com.atguigu.gmall.order.vo.OrderConfirmRespVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author 杨林
 * @create 2022-12-21 22:11 星期三
 * description:
 */
@RequestMapping("/api/inner/rpc/order")
@FeignClient("service-order")
public interface OrderFeignClient {

    /**
     * 获取订单确认页数据
     * @return
     */
    @GetMapping("/confirmdata")
    Result<OrderConfirmRespVo> orderConfirmData();


    /**
     * 根据订单id查询订单
     * @param id
     * @return
     */
    @GetMapping("/info/{id}")
    public Result<OrderInfo> grtOrderInfoById(@PathVariable("id") Long id);

    /**
     * 保存秒杀单数据
     * @param info
     * @return
     */
    @PostMapping("/seckill/order")
    Result<Long> saveSeckillOrder(@RequestBody OrderInfo info);

}
