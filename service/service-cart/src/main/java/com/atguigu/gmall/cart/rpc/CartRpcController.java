package com.atguigu.gmall.cart.rpc;

import com.atguigu.gmall.cart.entity.CartInfo;
import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.cart.vo.AddCartSuccessVo;
import com.atguigu.gmall.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-16 18:57 星期五
 * description:  提供远程调用
 */
@Slf4j
@RestController
@RequestMapping("/api/inner/rpc/cart")
public class CartRpcController {

    @Autowired
    CartInfoService cartInfoService;


    /**
     * 把商品添加到购物车
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("/add/{skuId}/{num}")
    public Result<AddCartSuccessVo> addToCart(@PathVariable("skuId") Long skuId,
                                              @PathVariable("num") Integer num,
                                              HttpServletRequest request){
        log.info("添加到购物车");

        //redis中保存key-value
        //跨层传递数据（1.直接方法参数传递   2.利用线程绑定机制）
        AddCartSuccessVo cartSuccessVo = cartInfoService.addToCart(skuId,num);

        return Result.ok(cartSuccessVo);
    }


    /**
     * 删除选中的商品
     * @return
     */
    @DeleteMapping("/deleteChecked")
    public Result deleteChecked(){

        String cartKey = cartInfoService.determinCartKey();
        cartInfoService.deleteChecked(cartKey);
        return Result.ok();
    }

    /**
     * 获取选中的所有商品
     * @return
     */
    @GetMapping("/checkeds")
    public Result<List<CartInfo>> getChecked(){
        String cartKey = cartInfoService.determinCartKey();
        List<CartInfo> checkedCartInfo = cartInfoService.getChecked(cartKey);
        return Result.ok(checkedCartInfo);
    }
}
