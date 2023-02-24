package com.atguigu.gmall.product;

import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class Test {


    /**
     * StreamAPI 的使用
     */
    @org.junit.jupiter.api.Test
    public void test2(){
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 4, 6, 7));
        Integer integer = list.stream()
                .parallel()  //并发流
                .map((item) -> {
                    return item * 2;
                })
                .filter(item -> item % 3 == 0)  //过滤调不能被3整除的数
                .reduce((o1, o2) -> o1 + o2).get();
        System.out.println(integer);

        //flatmap操作
        List<Integer> collect = list.stream()
                .flatMap(item -> Arrays.asList(item + 6, item + 8).stream())
                .collect(Collectors.toList());
        System.out.println(collect);
    }


    @org.junit.jupiter.api.Test
    public void test() throws Exception{
        // 1、创建MinioClient
        MinioClient minioClient =
                new MinioClient("http://192.168.10.129:9000",
                        "admin",
                        "admin123456");

        //2、判断 bucket是否存在
        boolean isExist = minioClient.bucketExists("mall-oss");
        if(isExist) {
            System.out.println("Bucket 已经存在,可以直接上传...");
        } else {
            // 3、创建一个名为 mall-oss 的存储桶
            minioClient.makeBucket("mall-oss");
        }

        String path = "D:\\shangguigu\\尚品汇\\尚品汇\\资料\\03 商品图片\\4.png";
        //3、使用putObject上传一个文件到存储桶中。

        /**
         * long objectSize,  对象大小
         * long partSize,    部分大小(分片上传) -1L
         */
        FileInputStream stream = new FileInputStream(path);

        PutObjectOptions options = new PutObjectOptions(stream.available(),-1L); //PutObjectOptions //上传参数项
        options.setContentType("image/png"); //指定内容类型
        minioClient.putObject("mall-oss",
                "4.png",
                new FileInputStream(path),
                options);
        System.out.println("上传成功...");
    }
}
