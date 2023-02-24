package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.vo.CategoryTreeVo;
import com.atguigu.gmall.feign.product.CategoryFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-03 10:55 星期六
 * description:
 */
@Controller
public class WebIndexController {


    @Autowired
    CategoryFeignClient categoryFeignClient;

    /**
     * 跳转到尚品汇首页
     * @param model
     * @return
     */
    @GetMapping({"/","/index.html"})
    public String Index(Model model){
        /**
         * 远程调用 feign 的流程
         *      1.先到注册中心找到 @FeignClient("service-product") 注解 说明的 service-product 对应的ip地址
         *      2.给指定的IP地址发送 方法指定的这种请求方式  并且路径是声明的路径
         *      3.对方处理完成以后给feign返回json数据 feign把接收到的json数据自动转换成方法指定的类型
         *
         * 注解的多义性
         *  controller：
         *      @xxxMapping：接收各种方式的请求
         *      @RequestParam：接收各种参数中的值
         *      @RequestBody:接收请求体中的值
         *      @PathVarable：接收请求路径的值
         *      @RequestHeader：接收请求头中的值
         *  feignclient：
         *      @xxxMapping：发送各种方式的请求
         *      @RequestParam：方法传参的值  放到请求参数中发送出去
         *      @RequestBody：方法传参的值放到请求体中
         *      @PathVarable：方法传参的值放到请求路径中
         *      @RequestHeader：方法传参的值放到请求头中发送出去
         */

        //远程调用service-product获取所有商品的三级分类数据
        Result<List<CategoryTreeVo>> category = categoryFeignClient.getCategory();
        List<CategoryTreeVo> list = category.getData();
        model.addAttribute("list",list);
        return "index/index";
    }
}
