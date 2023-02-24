package com.atguigu.gmall.common.config.thread.annotation;

import com.atguigu.gmall.common.config.minio.MinioAutoConfiguration;
import com.atguigu.gmall.common.config.thread.AppThreadPoolAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author 杨林
 * @create 2022-12-05 23:19 星期一
 * description:
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({AppThreadPoolAutoConfiguration.class})
public @interface EnableAppThreadPool {
}
