package com.atguigu.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.entity.SpuImage;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.mapper.SpuImageMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【spu_image(商品图片表)】的数据库操作Service实现
* @createDate 2022-11-29 11:42:45
*/
@Service
public class SpuImageServiceImpl extends ServiceImpl<SpuImageMapper, SpuImage>
    implements SpuImageService{

    /**
     * 根据spuId获取图片列表
     * @return
     */
    @Override
    public List<SpuImage> getImageListBySpuId(Long spuId) {
        QueryWrapper<SpuImage> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id",spuId);
        List<SpuImage> images = baseMapper.selectList(wrapper);
        return images;
    }
}




