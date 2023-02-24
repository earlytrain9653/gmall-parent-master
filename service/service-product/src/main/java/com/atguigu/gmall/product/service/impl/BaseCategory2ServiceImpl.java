package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.product.vo.CategoryTreeVo;
import com.atguigu.gmall.starter.cache.aspect.annotation.MallCache;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.entity.BaseCategory2;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import com.atguigu.gmall.product.mapper.BaseCategory2Mapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_category2(二级分类表)】的数据库操作Service实现
* @createDate 2022-11-29 11:42:46
*/
@Service
public class BaseCategory2ServiceImpl extends ServiceImpl<BaseCategory2Mapper, BaseCategory2>
    implements BaseCategory2Service{

    @Override
    public List<BaseCategory2> getCategory2sByC1Id(Long category1Id) {
        QueryWrapper<BaseCategory2> wrapper = new QueryWrapper<>();
        wrapper.eq("category1_id",category1Id);
        List<BaseCategory2> category2s = list(wrapper);
        return category2s;
    }

    /**
     * 获取三级分类的所有数据  并封装成水树形结构
     * @return
     */
    @MallCache(cacheKey = RedisConst.CATEGORY_CACHE)
    @Override
    public List<CategoryTreeVo> getCategoryTreeVo() {
        List<CategoryTreeVo> list = baseMapper.getCategoryTreeVoData();
        return list;
    }

    /**
     * 根据三级分类id得到商品分类的完整信息
     * @return
     */
    @Override
    public CategoryTreeVo getCategoryTreeVoByC3Id(Long c3Id) {
        CategoryTreeVo categoryTreeVo = baseMapper.getCategoryTreeVoByC3Id(c3Id);
        return categoryTreeVo;
    }
}




