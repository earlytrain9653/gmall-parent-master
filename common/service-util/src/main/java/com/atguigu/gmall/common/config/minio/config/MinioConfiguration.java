package com.atguigu.gmall.common.config.minio.config;

import com.atguigu.gmall.common.config.minio.properties.MinioProperties;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfiguration {

    @Autowired()
    MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() throws Exception {

        MinioClient client = new MinioClient(this.minioProperties.getEndpoint(),
                this.minioProperties.getAccessKey(),
                this.minioProperties.getSecretKey());
        //判断桶是否存在
        boolean exists = client.bucketExists(this.minioProperties.getBucketName());
        if (!exists){   //如果桶不存在则创建桶
            client.makeBucket(this.minioProperties.getBucketName());
        }
        return client;
    }

}
