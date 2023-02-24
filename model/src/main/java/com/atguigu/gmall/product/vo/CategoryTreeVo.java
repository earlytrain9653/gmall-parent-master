package com.atguigu.gmall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-03 10:52 星期六
 * description:
 */
@Data
//嵌套的一个无限级树形分类
public class CategoryTreeVo {

    private Long categoryId;  //分类的id
    private String categoryName;  //分类的名字
    private List<CategoryTreeVo> categoryChild;  //子分类

}
