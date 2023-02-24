package com.atguigu.gmall.starter.cache.annotation;

import com.atguigu.gmall.starter.cache.CacheAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author 杨林
 * @create 2022-12-10 16:10 星期六
 * description:
 */
@Import(CacheAutoConfiguration.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EnableAppCache {
}
