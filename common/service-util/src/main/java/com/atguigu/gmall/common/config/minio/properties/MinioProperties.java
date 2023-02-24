package com.atguigu.gmall.common.config.minio.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Component
@ConfigurationProperties(prefix = "app.minio")  //读取配置文件app.minio下的所有配置值 和JavaBean属性进行绑定
@Data
public class MinioProperties {

    //从配置文件中获取参数
    String endpoint;
    String accessKey;
    String secretKey;
    String bucketName;
}
