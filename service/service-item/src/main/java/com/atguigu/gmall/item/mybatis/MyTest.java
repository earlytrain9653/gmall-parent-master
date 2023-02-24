package com.atguigu.gmall.item.mybatis;

import com.atguigu.gmall.item.mybatis.dao.PersonDao;
import com.atguigu.gmall.item.mybatis.sql.MySqlSession;

/**
 * @author 杨林
 * @create 2022-12-14 15:11 星期三
 * description:
 */
public class MyTest {
    public static void main(String[] args) {
        //得到一个代理对象
        MySqlSession sqlSession = new MySqlSession();
        PersonDao mapper = sqlSession.getMapper(PersonDao.class);
        //System.out.println(mapper);
        mapper.getAllPersonCount();
        mapper.insertPerson(18,"marong@qq.com");
    }
}
