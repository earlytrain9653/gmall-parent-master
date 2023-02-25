package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.entity.SpuImage;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.product.entity.SpuSaleAttrValue;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrValueService;
import com.atguigu.gmall.product.vo.SpuSaveInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.entity.SpuInfo;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.product.mapper.SpuInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Administrator
 * @description 针对表【spu_info(商品表)】的数据库操作Service实现
 * @createDate 2022-11-29 11:42:45
 */
@Slf4j
@Service
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo>
        implements SpuInfoService {

    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    @Autowired
    SpuSaleAttrValueService spuSaleAttrValueService;

    @Autowired
    SpuImageService spuImageService;

    /**
     * 获取spu分页列表
     *
     * @param page
     * @param limit
     * @param category3Id
     * @return
     */
    @Override
    public Page<SpuInfo> getSpuInfoByC3Id(Long page, Long limit, Long category3Id) {
        Page page1 = new Page(page, limit);
        QueryWrapper<SpuInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_id", category3Id);
        Page page2 = baseMapper.selectPage(page1, wrapper);
        return page2;
    }

    /**
     * 保存spu
     *
     * @param vo
     */
    @Transactional
    @Override
    public void saveSpuInfoData(SpuSaveInfoVo vo) {
        SpuInfo spuInfo = new SpuInfo();
        BeanUtils.copyProperties(vo, spuInfo);

        //入库
        boolean save = save(spuInfo);
        if (!save) {
            log.debug("保存Spu: {} 失败", vo.toString());
        }

        //获取spu存入数据库后的自增id
        Long spuId = spuInfo.getId();

        //存图片
//        List<SpuSaveInfoVo.ImageVo> spuImageList = vo.getSpuImageList();
//        List<SpuImage> imageList = new ArrayList<>();
//        //遍历spu图片  存入数据库
//        for (SpuSaveInfoVo.ImageVo imageVo : spuImageList) {
//            SpuImage spuImage = new SpuImage();
//            BeanUtils.copyProperties(imageVo,spuImage);
//            //最后在回填该值  防止被一些错误数据覆盖
//            spuImage.setSpuId(spuId);
//            //入库
//            //spuImageService.save(spuImage);
//            //将每个image放入集合  批量添加到数据库  提高性能
//            imageList.add(spuImage);
//        }

        //StreamAPI的方式
        List<SpuImage> imageList = vo.getSpuImageList().stream()
                .map(item -> {
                    SpuImage spuImage = new SpuImage();
                    BeanUtils.copyProperties(item, spuImage);
                    spuImage.setSpuId(spuId);
                    return spuImage;
                })
                .collect(Collectors.toList());
        //批量对图片进行入库操作
        spuImageService.saveBatch(imageList);

//        //存销售属性和销售属性值
//        List<SpuSaveInfoVo.SpuSaleAttrVo> spuSaleAttrList = vo.getSpuSaleAttrList();
//        for (SpuSaveInfoVo.SpuSaleAttrVo spuSaleAttrVo : spuSaleAttrList) {
//            //存销售属性
//            SpuSaleAttr spuSaleAttr = new SpuSaleAttr();
//            BeanUtils.copyProperties(spuSaleAttrVo,spuSaleAttr);
//            //回填id
//            spuSaleAttr.setSpuId(spuId);
//            //入库
//            spuSaleAttrService.save(spuSaleAttr);
//
//            List<SpuSaveInfoVo.SpuSaleAttrValueVo> spuSaleAttrValueList = spuSaleAttrVo.getSpuSaleAttrValueList();
//            for (SpuSaveInfoVo.SpuSaleAttrValueVo spuSaleAttrValueVo : spuSaleAttrValueList) {
//                //存销售属性值
//                SpuSaleAttrValue spuSaleAttrValue = new SpuSaleAttrValue();
//                BeanUtils.copyProperties(spuSaleAttrValueVo,spuSaleAttrValue);
//                //回填id
//                spuSaleAttrValue.setSpuId(spuId);
//                //入库
//                spuSaleAttrValueService.save(spuSaleAttrValue);
//            }
//        }

        //StreamAPI 存销售属性名
        List<SpuSaleAttr> saleAttrList = vo.getSpuSaleAttrList()
                .stream()
                .map((item) -> {
                    //存销售属性
                    SpuSaleAttr spuSaleAttr = new SpuSaleAttr();
                    BeanUtils.copyProperties(item, spuSaleAttr);
                    //回填id
                    spuSaleAttr.setSpuId(spuId);
                    return spuSaleAttr;
                }).collect(Collectors.toList());
        spuSaleAttrService.saveBatch(saleAttrList);

        //存销售属性值
        List<SpuSaleAttrValue> attrValueList = vo.getSpuSaleAttrList().stream()
                .flatMap(item -> {
                    //item是每个销售属性  带了很多销售属性值
                    return item.getSpuSaleAttrValueList()
                            .stream()
                            .map(val -> {
                                SpuSaleAttrValue spuSaleAttrValue = new SpuSaleAttrValue();

                                spuSaleAttrValue.setSpuId(spuId);
                                spuSaleAttrValue.setBaseSaleAttrId(val.getBaseSaleAttrId());
                                spuSaleAttrValue.setSaleAttrValueName(val.getSaleAttrValueName());
                                spuSaleAttrValue.setSaleAttrName(item.getSaleAttrName());
                                return spuSaleAttrValue;
                            });
                }).collect(Collectors.toList());

        spuSaleAttrValueService.saveBatch(attrValueList);
    }
}




