package com.atguigu.gmall.seckill.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.seckill.biz.SeckillBizService;
import com.atguigu.gmall.seckill.vo.SeckillOrderSubmitVo;
import feign.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 杨林
 * @create 2022-12-27 22:41 星期二
 * description:
 */
@RestController
@RequestMapping("/api/activity/seckill/auth")
public class SeckillApiController {

    @Autowired
    SeckillBizService seckillBizService;

    /**
     * 为某个商品生成一个秒杀码，用秒杀码控制后续的所有秒杀流程
     * @param skuId
     * @return
     */
    @GetMapping("/getSeckillSkuIdStr/{skuId}")
    public Result getSeckillCode(@PathVariable("skuId") Long skuId){

        String code = seckillBizService.generateSeckillCode(skuId);

        return Result.ok(code);
    }


    /**
     * 下秒杀单：秒杀开始排队
     * @param skuId
     * @param skuIdStr
     * @return
     */
    @PostMapping("/seckillOrder/{skuId}")
    public Result seckillOrder(@PathVariable("skuId") Long skuId,
                               @RequestParam("skuIdStr") String skuIdStr){

        //秒杀下单
        seckillBizService.seckillOrder(skuId,skuIdStr);
        return Result.ok();
    }


    /**
     * 检查秒杀单
     * @param skuId
     * @return
     */
    @GetMapping("/checkOrder/{skuId}")
    public Result checkOrder(@PathVariable("skuId") Long skuId){

        //检查秒杀单状态
        ResultCodeEnum codeEnum = seckillBizService.checkOrder(skuId);
        return Result.build("",codeEnum);
    }


    /**
     * 提交秒杀单
     * @return
     */
    @PostMapping("/submitOrder")
    public Result submitOrder(@RequestBody SeckillOrderSubmitVo submitVo){

        Long orderId = seckillBizService.submitOrder(submitVo);
        return Result.ok(orderId.toString());
    }

}
