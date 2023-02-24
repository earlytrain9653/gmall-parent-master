package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.search.vo.SearchParamVo;
import com.atguigu.gmall.search.vo.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author 杨林
 * @create 2022-12-12 18:47 星期一
 * description:
 */
@Controller
public class SearchController {

    @Autowired
    SearchFeignClient searchFeignClient;

    @GetMapping("/list.html")
    public String search(SearchParamVo paramVo, Model model){

        //远程调用检索服务
        SearchResponseVo responseVo = searchFeignClient.search(paramVo).getData();

        //1.检索参数
        model.addAttribute("searchParam",paramVo);

        //2.品牌面包屑  字符串
        model.addAttribute("trademarkParam",responseVo.getTrademarkParam());

        //3.平台属性面包屑  集合[{attrName,attrValue,attrId}]
        model.addAttribute("propsParamList",responseVo.getPropsParamList());

        //4.品牌列表  集合[{tmId,tmName,tmLogoUrl}]
        model.addAttribute("trademarkList",responseVo.getTrademarkList());

        //5.属性列表  集合 [{attrName,attrValueList(字符串集合),attrId}]
        model.addAttribute("attrsList",responseVo.getAttrsList());

        //6.url参数
        model.addAttribute("urlParam",responseVo.getUrlParam());

        //7.排序信息[{type,sort}]
        model.addAttribute("orderMap",responseVo.getOrderMap());

        //8.商品列表  集合（每个商品信息）
        model.addAttribute("goodsList",responseVo.getGoodsList());

        //9.页码
        model.addAttribute("pageNo",responseVo.getPageNo());

        //10.总页码
        model.addAttribute("totalPages",responseVo.getTotalPages());

        return "list/index";  //检索结果展示页
    }
}
