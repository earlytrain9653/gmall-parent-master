package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.entity.BaseCategory2;
import com.atguigu.gmall.product.vo.CategoryTreeVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_category2(二级分类表)】的数据库操作Service
* @createDate 2022-11-29 11:42:46
*/
public interface BaseCategory2Service extends IService<BaseCategory2> {


    /**
     * 根据一级分类id，查询所有的二级分类
     * @param category1Id 一级分类id
     * @return
     */
    List<BaseCategory2> getCategory2sByC1Id(Long category1Id);

    /**
     * 获取三级分类的所有数据  并封装成水树形结构
     * @return
     */
    List<CategoryTreeVo> getCategoryTreeVo();

    /**
     * 根据三级分类id得到商品分类的完整路径
     * @return
     */
    CategoryTreeVo getCategoryTreeVoByC3Id(Long c3Id);

}
