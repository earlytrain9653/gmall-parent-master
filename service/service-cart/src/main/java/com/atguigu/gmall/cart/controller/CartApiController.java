package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.entity.CartInfo;
import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-18 14:13 星期日
 * description:
 */
@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    @Autowired
    CartInfoService cartInfoService;


    /**
     * 获取购物车商品列表
     * @return
     */
    @GetMapping("/cartList")
    public Result toCartList(){

        List<CartInfo> cartInfos = cartInfoService.displayItems();

//        //先获取购物车的键
//        String cartKey = cartInfoService.determinCartKey();
//        //拿到购物车中所有商品列表数据
//        List<CartInfo> cartInfos = cartInfoService.getCartInfoList(cartKey);
        return Result.ok(cartInfos);
    }

    /**
     * 修改购物车商品数量
     * @param skuId
     * @param num
     * @return
     */
    @PostMapping("/addToCart/{skuId}/{num}")
    public Result addToCartList(@PathVariable("skuId") Long skuId,
                            @PathVariable("num") Integer num){
        String cartKey = cartInfoService.determinCartKey();
        cartInfoService.addToCartList(cartKey,skuId,num);
        return Result.ok();
    }


    @GetMapping("/checkCart/{skuId}/{isChecked}")
    public Result checkCart(@PathVariable("skuId") Long skuId,
                            @PathVariable("isChecked") Integer isChecked){
        String cartKey = cartInfoService.determinCartKey();
        cartInfoService.checkCart(cartKey,skuId,isChecked);
        return Result.ok();
    }

    /**
     * 删除购物车中某个商品
     * @param skuId
     * @return
     */
    @DeleteMapping("/deleteCart/{skuId}")
    public Result delete(@PathVariable("skuId") Long skuId){
        String cartKey = cartInfoService.determinCartKey();
        cartInfoService.delete(cartKey,skuId);
        return Result.ok();
    }
}
