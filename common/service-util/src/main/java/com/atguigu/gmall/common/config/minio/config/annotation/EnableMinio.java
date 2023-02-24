package com.atguigu.gmall.common.config.minio.config.annotation;

import com.atguigu.gmall.common.config.minio.MinioAutoConfiguration;
import com.atguigu.gmall.common.config.minio.config.MinioConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurationSelector;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({MinioAutoConfiguration.class})
public @interface EnableMinio {
}
