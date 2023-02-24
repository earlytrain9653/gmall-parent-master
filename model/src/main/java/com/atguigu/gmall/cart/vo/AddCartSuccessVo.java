package com.atguigu.gmall.cart.vo;

import com.atguigu.gmall.product.entity.SkuInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 杨林
 * @create 2022-12-16 18:53 星期五
 * description:封装添加购物车成功后 给前端返回的参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCartSuccessVo {

    private SkuInfo skuInfo;  //sku的信息
    private Integer skuNum;   //添加到购物车的数量

}
