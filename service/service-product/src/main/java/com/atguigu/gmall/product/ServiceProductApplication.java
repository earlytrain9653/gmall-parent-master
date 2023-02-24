package com.atguigu.gmall.product;

import com.atguigu.gmall.common.config.Swagger2Config;
import com.atguigu.gmall.common.config.minio.config.MinioConfiguration;
import com.atguigu.gmall.common.config.minio.config.annotation.EnableMinio;
import com.atguigu.gmall.common.config.minio.properties.MinioProperties;
import io.minio.MinioClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * @author 杨林
 * @create 2022-11-29 16:21 星期二
 * description:
 */

//启动时只扫描主程序所在的包及其子包
//@ComponentScan("com.atguigu")
//@SpringBootApplication
//@EnableDiscoveryClient
//@EnableCircuitBreaker  //熔断器
@SpringCloudApplication  //这是一个SpringCloud服务，会自动开启服务发现和服务熔断
@MapperScan(basePackages = "com.atguigu.gmall.product.mapper")
@Import({Swagger2Config.class})
@EnableMinio
@EnableFeignClients(basePackages = {"com.atguigu.gmall.feign.search"})
public class ServiceProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceProductApplication.class,args);
    }
}
