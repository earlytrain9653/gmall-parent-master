package com.atguigu.gmall.feign.product;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.entity.SkuImage;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.product.vo.CategoryTreeVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-03 23:06 星期六
 * description:
 */
@RequestMapping("/api/inner/rpc/product")
@FeignClient("service-product")
public interface ProductSkuDetailFeignClient {

    /**
     * 根据三级分类id得到商品分类的完整路径
     * @param c3Id
     * @return
     */
    @GetMapping("/category/view/{c3Id}")
    public Result<CategoryTreeVo> getCategoryTreeVoByC3Id(@PathVariable("c3Id") Long c3Id);

    /**
     * 获取skuInfo
     * @return
     */
    @GetMapping("/sku/{skuId}")
    public Result<SkuInfo> getSkuInfo(@PathVariable("skuId") Long skuId);

    /**
     * 获取sku的图片信息
     * @param skuId
     * @return
     */
    @GetMapping("/skuImage/{skuId}")
    public Result<List<SkuImage>> getSkuInages(@PathVariable("skuId") Long skuId);

    /**
     * 获取商品的实时价格
     * @param skuId
     * @return
     */
    @GetMapping("/price/{skuId}")
    public Result<BigDecimal> getPrice(@PathVariable("skuId") Long skuId);

    /**
     * 获取spu销售属性名和值
     * @param spuId
     * @return
     */
    @GetMapping("/spusaleattr/{spuId}/{skuId}")
    public Result<List<SpuSaleAttr>> getSpuSaleAttr(@PathVariable("spuId") Long spuId,
                                                    @PathVariable("skuId") Long skuId);

    /**
     * 获取spu的json字符串
     * @return
     */
    @GetMapping("/valuespujson/{spuId}")
    public Result<String> getValueSpuJson(@PathVariable("spuId") Long spuId);

}
