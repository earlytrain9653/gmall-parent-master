package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.entity.BaseAttrInfo;
import com.atguigu.gmall.product.entity.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 杨林
 * @create 2022-11-29 23:10 星期二
 * description:
 */

/*
    平台属性
 */

@RestController
@RequestMapping("/admin/product")
public class BaseAttrController {

    @Autowired
    BaseAttrInfoService baseAttrInfoService;

    @Autowired
    BaseAttrValueService baseAttrValueService;


    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result getBaseAttr(@PathVariable("category1Id") Long category1Id,
                              @PathVariable("category2Id") Long category2Id,
                              @PathVariable("category3Id") Long category3Id){

        List<BaseAttrInfo> attrInfos = baseAttrInfoService.getBaseAttrAndValue(category1Id,
                category2Id, category3Id);
        return Result.ok(attrInfos);
    }


    /**
     * 保存或者修改属性
     * @param baseAttrInfo
     * @return
     */
    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        //判断是保存操作还是修改操作
        if (baseAttrInfo.getId() != null){
            //说明这是修改操作
            baseAttrInfoService.updateAttrInfo(baseAttrInfo);
        }else{
            //说明是保存操作
            //保存AttrInfo
            baseAttrInfoService.saveAttrInfo(baseAttrInfo);
        }
        return Result.ok();
    }


    @GetMapping("/getAttrValueList/{attrId}")
    public Result getBaseAttrInfo(@PathVariable("attrId") Long attrId){
        List<BaseAttrValue> baseAttrValues = baseAttrValueService.getAttrValueLitByAttrId(attrId);
        return Result.ok(baseAttrValues);
    }
}
