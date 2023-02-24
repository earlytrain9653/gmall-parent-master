package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.seckill.SeckillFeignClient;
import com.atguigu.gmall.seckill.entity.SeckillGoods;
import com.atguigu.gmall.seckill.vo.SeckillOrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-27 20:14 星期二
 * description:
 */
@Controller
public class SeckillController {

    @Autowired
    SeckillFeignClient seckillFeignClient;


    @GetMapping("/seckill.html")
    public String seckillPage(Model model){

        //远程调用秒杀服务  获取当天参与秒杀的所有商品
        Result<List<SeckillGoods>> seckillGoods = seckillFeignClient.getTodaySeckillGoods();
        //{skuId,skuDefaultImg,skuName,costPrice,price,num,stockCount}
        model.addAttribute("list",seckillGoods.getData());
        return "seckill/index";
    }

    /**
     * 秒杀商品详情
     * @param model
     * @return
     */
    @GetMapping("/seckill/{skuId}.html")
    public String seckillDetail(Model model, @PathVariable("skuId") Long skuId){
        Result<SeckillGoods> goodsDetail = seckillFeignClient.getSeckillGoodsDetail(skuId);
        model.addAttribute("item",goodsDetail.getData());
        return "seckill/item";
    }

    /**
     * 来到秒杀排队页
     * @param skuId
     * @param skuIdStr
     * @return
     */
    @GetMapping("/seckill/queue.html")
    public String queuePage(@RequestParam("skuId") Long skuId,
                            @RequestParam("skuIdStr") String skuIdStr,
                            Model model){
        model.addAttribute("skuId",skuId);
        model.addAttribute("skuIdStr",skuIdStr);
        return "seckill/queue";
    }


    /**
     * 秒杀订单确认页
     * @param model
     * @return
     */
    @GetMapping("/seckill/trade.html")
    public String tradePage(Model model,@RequestParam("code") String code){

        //远程调用秒杀服务获取订单确认页数据
        SeckillOrderConfirmVo confirmVo = seckillFeignClient.getSeckillOrderInfo(code).getData();

        //商品清单
        model.addAttribute("detailArrayList",confirmVo.getDetailArrayList());
        model.addAttribute("totalNum",confirmVo.getTotalNum());
        model.addAttribute("totalAmount",confirmVo.getTotalAmount());
        model.addAttribute("userAddressList",confirmVo.getUserAddressList());
        return "seckill/trade";
    }
}
