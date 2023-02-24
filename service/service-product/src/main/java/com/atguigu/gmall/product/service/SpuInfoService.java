package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.entity.SpuInfo;
import com.atguigu.gmall.product.vo.SpuSaveInfoVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【spu_info(商品表)】的数据库操作Service
* @createDate 2022-11-29 11:42:45
*/
public interface SpuInfoService extends IService<SpuInfo> {

    /**
     * 获取spu分页列表
     * @param page
     * @param limit
     * @param category3Id
     * @return
     */
    Page<SpuInfo> getSpuInfoByC3Id(Long page, Long limit, Long category3Id);

    void saveSpuInfoData(SpuSaveInfoVo vo);
}
