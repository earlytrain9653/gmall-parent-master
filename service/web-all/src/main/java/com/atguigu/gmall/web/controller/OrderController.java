package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.order.vo.OrderConfirmRespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨林
 * @create 2022-12-21 21:07 星期三
 * description:
 */
@Controller
public class OrderController {

    @Autowired
    OrderFeignClient orderFeignClient;

    /**
     * 订单确认页
     * @param model
     * @return
     */
    @GetMapping("/trade.html")
    public String trade(Model model){

        // 远程调用订单  获取订单确认页的数据  并展示
        OrderConfirmRespVo data = orderFeignClient.orderConfirmData().getData();
        //详情列表  购买了哪些商品{skuId,imgUrl,skuName,orderPrice,skuNum}
        model.addAttribute("detailArrayList",data.getDetailArrayList());

        model.addAttribute("totalNum",data.getTotalNum());
        model.addAttribute("totalAmount",data.getTotalAmount());
        model.addAttribute("userAddressList",data.getUserAddressList());
        model.addAttribute("tradeNo",data.getTradeNo());


        return "order/trade";
    }

    @GetMapping("/myOrder.html")
    public String orderListPage(){

        return "order/myOrder";
    }
}
