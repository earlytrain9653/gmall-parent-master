package com.atguigu.gmall.product.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ComponentScan("com.atguigu.gmall.product.mapper")
@SpringBootConfiguration
@EnableTransactionManagement  //开启基于注解的事务
public class MybatisPlusConfig {

    /**
     * 分页配置
     * mybatisPlus的总拦截器
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //分页拦截器
        PaginationInnerInterceptor innerInterceptor = new PaginationInnerInterceptor();

        //是否对页码溢出后进行处理
        innerInterceptor.setOverflow(true);
        //将分页拦截器加入总拦截器
        interceptor.addInnerInterceptor(innerInterceptor);
        //interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }
}
