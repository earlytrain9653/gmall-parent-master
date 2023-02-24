package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.order.entity.OrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 杨林
 * @create 2022-12-23 20:43 星期五
 * description:
 */
@Controller
public class PayController {

    @Autowired
    OrderFeignClient orderFeignClient;

    @GetMapping("/pay.html")
    public String payPage(@RequestParam("orderId") Long orderId,
                          Model model){
        //远程调用订单 把订单数据查询出来
        OrderInfo orderInfo = orderFeignClient.grtOrderInfoById(orderId).getData();
        model.addAttribute("orderInfo",orderInfo);
        return "payment/pay";
    }

    /**
     * 支付成功提示页
     * @return
     */
    @GetMapping("/pay/success.html")
    public String paySuccess(){

        return "payment/success";
    }
}
