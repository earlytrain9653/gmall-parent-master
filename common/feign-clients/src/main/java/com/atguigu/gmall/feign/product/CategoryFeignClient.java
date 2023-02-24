package com.atguigu.gmall.feign.product;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.vo.CategoryTreeVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-03 19:58 星期六
 * description:
 */
@RequestMapping("/api/inner/rpc/product")
//@FeignClient注解的两个作用
//1.说清楚要调用哪个服务
//2.service-product也是feign客户端的名字
//3.每个feign客户端的所有配置都集中在一个bean中  service-product
@FeignClient("service-product")
public interface CategoryFeignClient {

    //给远程的 service_product 发送Get请求
    @GetMapping("/category/tree")
    Result<List<CategoryTreeVo>> getCategory();
}
