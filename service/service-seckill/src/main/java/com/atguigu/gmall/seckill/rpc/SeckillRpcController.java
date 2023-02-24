package com.atguigu.gmall.seckill.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.seckill.biz.SeckillBizService;
import com.atguigu.gmall.seckill.entity.SeckillGoods;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import com.atguigu.gmall.seckill.vo.SeckillOrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-27 20:28 星期二
 * description:
 */
@RestController
@RequestMapping("/api/inner/rpc/seckill")
public class SeckillRpcController {

    @Autowired
    SeckillGoodsService seckillGoodsService;

    @Autowired
    SeckillBizService seckillBizService;

    /**
     * 查询当天参与秒杀的所有商品
     * @return
     */
    @GetMapping("/today/goods")
    public Result getTodaySeckillGoods(){
        String date = DateUtil.formatDate(new Date());
        List<SeckillGoods> goods = seckillGoodsService.getSeckillGoodsByDayFromCache(date);
        return Result.ok(goods);
    }


    /**
     * 查询秒杀商品详情
     * @param skuId
     * @return
     */
    @GetMapping("/detail/{skuId}")
    public Result<SeckillGoods> getSeckillGoodsDetail(@PathVariable("skuId") Long skuId){

        SeckillGoods seckillGoods = seckillGoodsService.getDetail(skuId);
        return Result.ok(seckillGoods);
    }


    /**
     * 获取秒杀单数据
     * @param code
     * @return
     */
    @GetMapping("/order/detail/{code}")
    public Result<SeckillOrderConfirmVo> getSeckillOrderInfo(@PathVariable("code") String code){

        SeckillOrderConfirmVo confirmVo = seckillBizService.getSeckillOrderInfo(code);
        return Result.ok(confirmVo);
    }
}
