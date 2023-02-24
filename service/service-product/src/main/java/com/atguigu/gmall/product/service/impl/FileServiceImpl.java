package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.common.config.minio.properties.MinioProperties;
import com.atguigu.gmall.product.service.FileService;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

//    //从配置文件中获取参数
//    @Value("${app.minio.endpoint}")
//    String endpoint;
//    @Value("${app.minio.access-key}")
//    String accessKey;
//    @Value("${app.minio.secret-key}")
//    String secretKey;
//    @Value("${app.minio.bucket-name}")
//    String bucketName;

    //优化3：将属性放入Javabean中完成自动注入
    @Autowired
    MinioProperties minioProperties;

    //    //优化4：封装获取Minion的方法
    @Autowired
    MinioClient client;

    /**
     * 上传文件返回访问地址
     *
     * @param file
     * @return
     * @throws Exception
     */
    @Override
    public String upload(MultipartFile file) throws Exception {
        //1.创建Minio的客户端
//        MinioClient client = new MinioClient(
//                "http://192.168.10.129:9000",
//                "admin",
//                "admin123456"
//        );
//        MinioClient client = new MinioClient(minioProperties.getEndpoint(),
//                minioProperties.getAccessKey(),
//                minioProperties.getSecretKey());

//        //判断桶是否存在  提取到创建客户端时判断
//        boolean exists = client.bucketExists(minioProperties.getBucketName());
//        if (!exists){   //如果桶不存在则创建桶
//            client.makeBucket(minioProperties.getBucketName());
//        }

        //获取要上传的文件的文件名
        //优化1：为每个文件加上唯一前缀 防止同名文件覆盖
        //优化2：以当天时间作为文件夹进行组织
        String date = DateUtil.formatDate(new Date());
        String filename = date + "/" + UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        InputStream stream = file.getInputStream();
        PutObjectOptions options = new PutObjectOptions(file.getSize(), -1L);
        options.setContentType(file.getContentType());

        //上传文件
        client.putObject(minioProperties.getBucketName(), filename, stream, options);

        //返回文件的访问地址
        String url = minioProperties.getEndpoint() + "/" + minioProperties.getBucketName() + "/" + filename;
        return url;
    }
}
