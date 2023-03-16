package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.cart.vo.AddCartSuccessVo;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import feign.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 杨林
 * @create 2022-12-16 18:47 星期五
 * description:购物车业务处理
 */
@Controller
public class CartController {

    @Autowired
    CartFeignClient cartFeignClient;

    //public static final Map<Thread,HttpServletRequest> requestMap = new ConcurrentHashMap<>();

    /**
     * 把商品添加到购物车，并跳转到添加成功页面
     * @return
     */
    @GetMapping("/addCart.html")
    public String addCart(@RequestParam("skuId") Long skuId,
                          @RequestParam("skuNum") Integer skuNum,
                          //HttpServletRequest request,
                          Model model){
        //Tomcat每次接到一个请求 都会分配一个线程来处理
        //利用线程绑定机制共享老请求  同一个线程从请求开始 到请求处理结束  用的都是同一个线程
        //requestMap.put(Thread.currentThread(),request);

        Result<AddCartSuccessVo> result = cartFeignClient.addToCart(skuId, skuNum);

        model.addAttribute("skuInfo",result.getData().getSkuInfo());
        model.addAttribute("skuNum",skuNum);

        return "cart/addCart";
    }

    /**
     * 购物车商品列表页
     * @return
     */
    @GetMapping("/cart.html")
    public String toCart(){

        return "cart/index";
    }

    /**
     * 删除选中的商品
     * @return
     */
    @GetMapping("/cart/deleteChecked")
    public String deleteChecked(){

        cartFeignClient.deleteChecked();
        return "redirect:http://cart.gmall.com/cart.html";
    }
}
