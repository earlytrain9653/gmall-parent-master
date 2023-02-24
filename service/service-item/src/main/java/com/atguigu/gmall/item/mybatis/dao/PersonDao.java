package com.atguigu.gmall.item.mybatis.dao;

import com.atguigu.gmall.item.mybatis.annotation.MySQL;

/**
 * @author 杨林
 * @create 2022-12-14 15:10 星期三
 * description:
 */
public interface PersonDao {
    @MySQL("select * from person")
    Integer getAllPersonCount();

    @MySQL("insert into person('age','email') values(?,?)")
    void insertPerson(Integer age,String email);
}
