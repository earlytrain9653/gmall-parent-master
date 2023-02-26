package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.ProductSkuDetailFeignClient;
import com.atguigu.gmall.product.vo.SkuDetailVo;
import com.atguigu.gmall.feign.item.SkuDetailFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

/**
 * @author 杨林
 * @create 2022-12-03 20:34 星期六
 * description:
 */
@Controller
public class ItemController {

    @Autowired
    SkuDetailFeignClient skuDetailFeignClient;

    @Autowired
    ProductSkuDetailFeignClient productSkuDetailFeignClient;

    /**
     * 商品详情页
     * 发请求到item详情服务，用feign远程调用查询商品详情页面controller；
     * 详情服务远程调用商品服务请求返回详情页面数据
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String getItemPage(@PathVariable("skuId") Long skuId,
                              Model model){
        Result<SkuDetailVo> skuDetails = skuDetailFeignClient.getSkuDetails(skuId);
        SkuDetailVo skuDetailVo = skuDetails.getData();
        //1.分类视图  {category1Id,category2Id,category3Id,category1Name,category2Name,category3Name}
        model.addAttribute("categoryView",skuDetailVo.getCategoryView());

        //2.sku信息
        model.addAttribute("skuInfo",skuDetailVo.getSkuInfo());

        //3.skuImageList
        //model.addAttribute("skuImageList","");

        //4.实时价格
        BigDecimal price = productSkuDetailFeignClient.getPrice(skuId).getData();
        model.addAttribute("price",price);


        //5.所有销售属性集合
        model.addAttribute("valuesSkuJson",skuDetailVo.getValuesSkuJson());

        //6.销售属性集合
        model.addAttribute("spuSaleAttrList",skuDetailVo.getSpuSaleAttrList());

        //TODO 6.sku的规格；平台属性


        return "item/index";
    }
}
