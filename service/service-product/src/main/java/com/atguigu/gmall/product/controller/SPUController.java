package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.entity.BaseSaleAttr;
import com.atguigu.gmall.product.entity.BaseTrademark;
import com.atguigu.gmall.product.entity.SpuInfo;
import com.atguigu.gmall.product.entity.SpuPoster;
import com.atguigu.gmall.product.service.BaseSaleAttrService;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.product.service.SpuPosterService;
import com.atguigu.gmall.product.vo.SpuSaveInfoVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品属性SPU管理")
@RestController
@RequestMapping("/admin/product")
public class SPUController {

    @Autowired
    SpuInfoService spuInfoService;

    @Autowired
    BaseSaleAttrService baseSaleAttrService;

    @Autowired
    BaseTrademarkService baseTrademarkService;

    @Autowired
    SpuPosterService spuPosterService;

    /**
     * 获取spu分页列表
     * @param page
     * @param limit
     * @param category3Id
     * @return
     */
    @ApiOperation("分页查询Spu")
    @GetMapping("/{page}/{limit}")
    public Result getSpuInfo(@PathVariable("page") Long page,
                             @PathVariable("limit") Long limit,
                             @RequestParam("category3Id") Long category3Id){

        Page<SpuInfo> infoPage = spuInfoService.getSpuInfoByC3Id(page, limit, category3Id);

        //使用spuInfoService.lambdaQuery方法进行分页查询
//        Page<SpuInfo> infoPage = spuInfoService.lambdaQuery()
//                .eq(SpuInfo::getCategory3Id, category3Id)
//                .page(new Page<SpuInfo>(page, limit));

        return Result.ok(infoPage);
    }


    /**
     * 获取销售属性的列表  共前端添加销售属性是选择
     * @return
     */
    @ApiOperation("获取销售属性的列表")
    @GetMapping("/baseSaleAttrList")
    public Result getBaseSaleAttr(){
        List<BaseSaleAttr> baseSaleAttrs = baseSaleAttrService.list();
        return Result.ok(baseSaleAttrs);
    }

    /**
     * 获取品牌列表  用于前端页面添加时选取商品类型
     * @return
     */
    @ApiOperation("查询所有的品牌列表")
    @GetMapping("/baseTrademark/getTrademarkList")
    public Result getTrademark(){
        List<BaseTrademark> trademarks = baseTrademarkService.list();
        return Result.ok(trademarks);
    }

    /**
     * 保存spuInfo信息
     * @param vo  前端传来模型固定则 自定义 vo 接前端的请求参数
     *            前端传来的模型不固定，则可用map接收：map<String, Object> jsonMap
     * @return
     */
    @ApiOperation("保存Spu")
    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuSaveInfoVo vo){
        //保存spuInfo
        spuInfoService.saveSpuInfoData(vo);
        return Result.ok();
    }
}
