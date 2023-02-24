package com.atguigu.gmall.product.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import com.atguigu.gmall.product.vo.CategoryTreeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-03 16:33 星期六
 * description:
 */
@RestController
@RequestMapping("/api/inner/rpc/product")  //远程调用的基准路径 一般命名规则： ”/api/inner/rpc/微服务名“
public class CategoryRpcController {

    @Autowired
    BaseCategory2Service baseCategory2Service;

    /**
     * 获取三级分类的全部数据 并封装成树形结构
     * @return
     */
    @GetMapping("/category/tree")
    public Result getCategoryTree(){
        List<CategoryTreeVo> list = baseCategory2Service.getCategoryTreeVo();
        return Result.ok(list);
    }

    /**
     * 根据三级分类id得到商品分类的完整路径
     * @param c3Id
     * @return
     */
    @GetMapping("/category/view/{c3Id}")
    public Result<CategoryTreeVo> getCategoryTreeVoByC3Id(@PathVariable("c3Id") Long c3Id){
        CategoryTreeVo categoryTreeVo = baseCategory2Service.getCategoryTreeVoByC3Id(c3Id);
        return Result.ok(categoryTreeVo);
    }

}
