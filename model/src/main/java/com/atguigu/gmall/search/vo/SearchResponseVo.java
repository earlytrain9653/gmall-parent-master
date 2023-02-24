package com.atguigu.gmall.search.vo;

import com.atguigu.gmall.search.Goods;
import lombok.Data;

import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-12 19:34 星期一
 * description: 封装检索完成响应数据的vo
 */
@Data
public class SearchResponseVo {
    //检索用的所有参数
    private SearchParamVo searchParam;

    //品牌面包屑
    private String trademarkParam;

    //属性面包屑
    private List<Props> propsParamList;

    //品牌列表
    private List<Trademark> trademarkList;

    //属性列表
    private List<Attr> attrsList;

    //url参数
    private String urlParam;

    //排序信息
    private OrderMap orderMap;

    //TODO 商品集合
    private List<Goods> goodsList;

    //页码
    private Integer pageNo;

    //总计页数
    private Long totalPages;


    @Data
    public static class OrderMap{
        private String type;
        private String sort;
    }

    @Data
    public static class Attr{
        private String attrName;
        private List<String> attrValueList;
        private Long attrId;
    }

    @Data
    public static class Trademark{
        private Long tmId;
        private String tmName;
        private String tmLogoUrl;
    }

    @Data
    public static class Props {
        private String attrName;
        private String attrValue;
        private Long attrId;
    }
}
