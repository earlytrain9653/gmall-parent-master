package com.atguigu.gmall.feign.cart;

import com.atguigu.gmall.cart.entity.CartInfo;
import com.atguigu.gmall.cart.vo.AddCartSuccessVo;
import com.atguigu.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-16 19:01 星期五
 * description:购物车远程调用接口
 */
@FeignClient("service-cart")
@RequestMapping("/api/inner/rpc/cart")
public interface CartFeignClient {

    /**
     * 把商品添加到购物车
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("/add/{skuId}/{num}")
    public Result<AddCartSuccessVo> addToCart(@PathVariable("skuId") Long skuId,
                                              @PathVariable("num") Integer num);

    /**
     * 删除选中的商品
     * @return
     */
    @DeleteMapping("/deleteChecked")
    public Result deleteChecked();


    /**
     * 获取选中的所有商品
     * @return
     */
    @GetMapping("/checkeds")
    public Result<List<CartInfo>> getChecked();

}
