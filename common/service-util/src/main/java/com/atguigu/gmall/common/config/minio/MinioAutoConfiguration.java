package com.atguigu.gmall.common.config.minio;

import com.atguigu.gmall.common.config.minio.config.MinioConfiguration;
import com.atguigu.gmall.common.config.minio.properties.MinioProperties;
import io.minio.MinioClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


//1、MinioProperties和配置文件绑定
//2、MinioProperties注册到容器中
@EnableConfigurationProperties(MinioProperties.class)

@Import({MinioConfiguration.class})
@Configuration
public class MinioAutoConfiguration {


}