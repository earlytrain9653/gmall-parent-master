package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.entity.SpuImage;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.vo.SkuSaveVo;
import com.atguigu.gmall.starter.cache.service.CacheService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "sku管理")
@RestController
@RequestMapping("/admin/product")
public class SKUController {

    @Autowired
    SpuImageService spuImageService;

    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    CacheService cacheService;

    /**
     * 修改SKUInfo
     *      测试延迟双删
     * @return
     */
    @GetMapping("/updateSkuInfo")
    public Result updateSkuInfo(@RequestBody SkuSaveVo vo){
        //1.修改数据库

        //2.更新缓存
        cacheService.delayDoubleDel("sku:info:49");

        return Result.ok();
    }


    /**
     * 根据spuId获取图片列表
     * @return
     */
    @GetMapping("/spuImageList/{spuId}")
    public Result getImageBySpuId(@PathVariable("spuId") Long spuId){
        List<SpuImage> images = spuImageService.getImageListBySpuId(spuId);
        return Result.ok(images);
    }

    /**
     * 根据spuId获取销售属性
     * @param spuId
     * @return
     */
    @ApiOperation("查询Spu定义的所有销售属性名和值")
    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result getSpuSaleAttr(@PathVariable("spuId") Long spuId){
        List<SpuSaleAttr> attrList = spuSaleAttrService.getSpuSaleAttrList(spuId);
        return Result.ok(attrList);
    }

    /**
     * 分页查询Sku列表
     * @param pn
     * @param ps
     * @return
     */
    @ApiOperation("分页查询Sku列表")
    @GetMapping("/list/{pn}/{ps}")
    public Result getSpuList(@PathVariable("pn") Long pn,@PathVariable("ps") Long ps){
        Page<SkuInfo> page = new Page<>(pn, ps);
        Page<SkuInfo> skuInfoPage = skuInfoService.page(page);
        return Result.ok(skuInfoPage);
    }


    /**
     * 保存
     * @return
     */
    @ApiOperation("保存sku")
    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuSaveVo vo){
        skuInfoService.saveSkuInfoData(vo);
        return Result.ok();
    }


    /**
     * 商品上架
     * @param skuId
     * @return
     */
    @ApiOperation("商品上架")
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId){
        skuInfoService.onSale(skuId);
        return Result.ok();
    }

    /**
     * 商品下架
     * @param skuId
     * @return
     */
    @ApiOperation("商品下架")
    @GetMapping("/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") Long skuId){
        skuInfoService.cancelSale(skuId);
        return Result.ok();
    }
}
