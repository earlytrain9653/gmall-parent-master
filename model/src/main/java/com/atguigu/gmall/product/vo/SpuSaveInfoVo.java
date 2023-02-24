package com.atguigu.gmall.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class SpuSaveInfoVo {

    private Long id;
    private String spuName;
    private String description;
    private Long category3Id;
    private Long tmId;
    private List<ImageVo> spuImageList;
    private List<SpuSaleAttrVo> spuSaleAttrList;

    @Data
    public static class ImageVo{
        private String imgName;
        private String imgUrl;
    }

    @Data
    public static class SpuSaleAttrVo{
        private Long baseSaleAttrId;
        private String saleAttrName;
        private List<SpuSaleAttrValueVo> spuSaleAttrValueList;
    }

    @Data
    public static class SpuSaleAttrValueVo{

        private Long baseSaleAttrId;
        private String saleAttrValueName;
    }
}
