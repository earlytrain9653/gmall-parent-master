package com.atguigu.gmall.common.config.exception;

import com.atguigu.gmall.common.config.exception.handler.GlobalExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 杨林
 * @create 2022-12-10 22:13 星期六
 * description:异常处理自动配置类
 */
@Configuration
public class ExceptionAutoConfiguration {

    @Bean
    public GlobalExceptionHandler globalExceptionHandler(){
        return new GlobalExceptionHandler();
    }
}
