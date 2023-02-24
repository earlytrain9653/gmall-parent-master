package com.atguigu.gmall.feign.ware;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 杨林
 * @create 2022-12-22 15:50 星期四
 * description:
 */
//url:精确指定当前feignClient以后发送请求的基准地址
@FeignClient(value = "ware-manage",url = "http://localhost:9001")
public interface WareFeignClient {

    @GetMapping("/hasStock")
    String hasStock(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num);
}
