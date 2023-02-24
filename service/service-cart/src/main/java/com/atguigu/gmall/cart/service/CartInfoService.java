package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.cart.entity.CartInfo;
import com.atguigu.gmall.cart.vo.AddCartSuccessVo;
import com.atguigu.gmall.product.entity.SkuInfo;

import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-17 17:18 星期六
 * description:
 */
public interface CartInfoService {

    /**
     * 添加购物车
     * @param skuId
     * @param num
     * @return
     */
    AddCartSuccessVo addToCart(Long skuId, Integer num);

    /**
     * 决定购物车用哪个key
     * @return
     */
    public String determinCartKey();

    /**
     * 给购物车添加商品
     * @param cartKey
     * @param skuId
     * @param num
     * @return
     */
    public SkuInfo addCartItem(String cartKey, Long skuId, Integer num);

    /**
     * 从指定购物车中得到一个商品信息
     * @param cartKey
     * @param skuId
     * @return
     */
    public CartInfo getCartInfo(String cartKey, Long skuId);


    /**
     * 查询某个购物车的商品列表
     * @param cartKey
     * @return
     */
    List<CartInfo> getCartInfoList(String cartKey);

    /**
     * 修改购物车商品数量
     * @param cartKey
     * @param skuId
     * @param num
     */
    void addToCartList(String cartKey, Long skuId, Integer num);

    /**
     * 修改选中状态
     * @param cartKey
     * @param skuId
     * @param isChecked
     */
    void checkCart(String cartKey, Long skuId, Integer isChecked);

    /**
     * 删除购物车中的某个商品
     * @param cartKey
     * @param skuId
     */
    void delete(String cartKey, Long skuId);

    /**
     * 删除购物车中选中的方法
     * @param cartKey
     */
    void deleteChecked(String cartKey);

    /**
     * 专供购物车列表使用  展示购物车中所有商品
     * @return
     */
    List<CartInfo> displayItems();

    /**
     * 获取选中的商品
     * @param cartKey
     * @return
     */
    List<CartInfo> getChecked(String cartKey);

}
