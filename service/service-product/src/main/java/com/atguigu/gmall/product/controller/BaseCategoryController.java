package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.entity.BaseCategory1;
import com.atguigu.gmall.product.entity.BaseCategory2;
import com.atguigu.gmall.product.entity.BaseCategory3;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 杨林
 * @create 2022-11-29 18:41 星期二
 * description:
 */
@RestController
@RequestMapping("/admin/product")
@Api(tags = "三级分类管理")
public class BaseCategoryController  {

    @Autowired
    BaseCategory1Service baseCategory1Service;

    @Autowired
    BaseCategory2Service baseCategory2Service;

    @Autowired
    BaseCategory3Service baseCategory3Service;

    /**
     * 查询所有一级分类
     * @return
     */
    @ApiOperation("查询所有一级分类")
    @GetMapping("/getCategory1")
    public Result getCategory1(){
        List<BaseCategory1> category1s = baseCategory1Service.list();
        return Result.ok(category1s);
    }


    /**
     * 查询某个一级分类下的所有二级分类
     * @return
     */
    @ApiOperation("根据一级分类的Id查询所有的二级分类")
    @GetMapping("/getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable("category1Id") Long category1Id){
        List<BaseCategory2> category2s = baseCategory2Service.getCategory2sByC1Id(category1Id);
        return Result.ok(category2s);
    }

    /**
     * 查询某一个二级分类下的所有三级分类
     * @param category2Id
     * @return
     */
    @ApiOperation("根据二级分类id查询所有三级分类")
    @GetMapping("/getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable("category2Id") Long category2Id){
        List<BaseCategory3> category3s = baseCategory3Service.getCategory3ByC2Id(category2Id);
        return Result.ok(category3s);
    }

}
