package com.atguigu.gmall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @author 杨林
 * @create 2022-12-12 9:26 星期一
 * description:
 */
@EnableElasticsearchRepositories   //开启es自动仓库功能
@SpringBootApplication
public class SearchServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SearchServiceApplication.class,args);
    }
}
