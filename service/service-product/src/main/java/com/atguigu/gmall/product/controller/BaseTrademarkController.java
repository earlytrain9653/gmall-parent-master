package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.entity.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "品牌管理")
@RequestMapping("/admin/product")
public class BaseTrademarkController {

    @Autowired
    BaseTrademarkService baseTrademarkService;

    /**
     * 分页获取所有品牌信息
     *
     * @return
     */
    @GetMapping("/baseTrademark/{pn}/{ps}")
    public Result getBaseSaleAttr(@PathVariable("pn") Long pn, @PathVariable("ps") Long ps) {

        Page page = new Page(pn, ps);

        //分页信息 及 查询结果
        Page result = baseTrademarkService.page(page);

        return Result.ok(result);
    }

    /**
     * 添加品牌
     *
     * @return
     */
    @ApiOperation("保存品牌")
    @PostMapping("/baseTrademark/save")
    public Result saveBaseTrademark(@RequestBody BaseTrademark baseTrademark) {
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }

    /**
     * 根据id删除商品
     *
     * @param id
     * @return
     */
    @ApiOperation("删除品牌")
    @DeleteMapping("/baseTrademark/remove/{id}")
    public Result deleteBaseTrademark(@PathVariable("id") Long id) {
        boolean b = baseTrademarkService.removeById(id);
        return Result.ok();
    }

    /**
     * 根据id获取品牌 (用于修改时数据回显)
     *
     * @return
     */
    @ApiOperation("根据id获取品牌")
    @GetMapping("/baseTrademark/get/{id}")
    public Result getTrademarkById(@PathVariable("id") Long id) {
        BaseTrademark baseTrademark = baseTrademarkService.getById(id);
        return Result.ok(baseTrademark);
    }

    /**
     * 修改品牌
     *
     * @param baseTrademark
     * @return
     */
    @PutMapping("/baseTrademark/update")
    public Result updateTrademark(@RequestBody BaseTrademark baseTrademark) {
        boolean b = baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }
}
