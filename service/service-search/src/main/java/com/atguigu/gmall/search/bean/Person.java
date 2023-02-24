package com.atguigu.gmall.search.bean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author 杨林
 * @create 2022-12-12 16:50 星期一
 * description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "person")  //代表这是一个文档
public class Person {

    @Id  //主键
    private Long id;

    @Field(value = "name",type = FieldType.Text)  //文本字段能全文检索
    private String name;

    @Field(value = "age",type = FieldType.Byte)
    private Integer age;
}
