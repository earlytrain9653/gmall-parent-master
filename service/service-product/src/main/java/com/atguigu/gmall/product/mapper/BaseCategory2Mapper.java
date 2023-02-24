package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.product.entity.BaseCategory2;
import com.atguigu.gmall.product.vo.CategoryTreeVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_category2(二级分类表)】的数据库操作Mapper
* @createDate 2022-11-29 11:42:46
* @Entity com.atguigu.gmall.product.entity.BaseCategory2
*/
public interface BaseCategory2Mapper extends BaseMapper<BaseCategory2> {

    /**
     * 获取所有三级分类的所有属性 并封装成树形结构
     * @return
     */
    List<CategoryTreeVo> getCategoryTreeVoData();

    /**
     * 根据商品的三级id获取该商品的属性
     * @param c3Id
     * @return
     */
    CategoryTreeVo getCategoryTreeVoByC3Id(Long c3Id);
}




