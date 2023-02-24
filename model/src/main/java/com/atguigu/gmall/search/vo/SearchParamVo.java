package com.atguigu.gmall.search.vo;

import lombok.Data;

/**
 * @author 杨林
 * @create 2022-12-12 18:55 星期一
 * description: 封装检索用的参数
 */
@Data
public class SearchParamVo {
    private Long category1Id;
    private Long category2Id;
    private Long category3Id;
    //以上是分类相关参数

    //关键字
    private String keyword;

    //品牌检索
    private String trademark;

    //平台属性
    private String[] props;

    //排序方式
    private String order = "1:desc";

    //页码
    private Integer pageNo = 1;
}
