package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.entity.SpuImage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【spu_image(商品图片表)】的数据库操作Service
* @createDate 2022-11-29 11:42:45
*/
public interface SpuImageService extends IService<SpuImage> {

    /**
     * 根据spuId获取图片列表
     * @return
     */
    List<SpuImage> getImageListBySpuId(Long spuId);

}
