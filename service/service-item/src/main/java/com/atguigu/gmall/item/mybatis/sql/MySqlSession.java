package com.atguigu.gmall.item.mybatis.sql;

import com.atguigu.gmall.item.mybatis.annotation.MySQL;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author 杨林
 * @create 2022-12-14 15:18 星期三
 * description:
 */
public class MySqlSession {
    public<T> T getMapper(Class<T> dao) {
        //返回mapper实例操作数据库
        T instance = (T)Proxy.newProxyInstance(dao.getClassLoader(),
                new Class[]{dao},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        //获取数据库连接
                        System.out.println("获取数据库连接。。。。");

                        //拿到注解：
                        MySQL mySQL = method.getDeclaredAnnotation(MySQL.class);
                        //获取注解上的SQL
                        String value = mySQL.value();
                        //System.out.println("hhh");
                        System.out.println("执行SQL");
                        System.out.println("获取返回结果");
                        System.out.println("分析ResultSet  并封装为方法的返回值类型");
                        System.out.println(value);
                        return null;
                    }
                }
        );
        return instance;
    }
}
