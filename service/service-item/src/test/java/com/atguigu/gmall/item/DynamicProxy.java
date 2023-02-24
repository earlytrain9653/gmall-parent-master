package com.atguigu.gmall.item;

import lombok.Data;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author 杨林
 * @create 2022-12-09 9:27 星期五
 * description:测试动态代理
 */
@SpringBootTest
public class DynamicProxy {

    /**
     * 测试CGlib动态代理：
     *      步骤：
     *          1.创建一个增强器
     *          2.指定父类（即指定要代理的对象  认干爹模式）
     *          3.设置回调
     *          4.创建一个代理对象
     * @param args
     */
    public static void main(String[] args) {

        BMW bmw = new BMW();
        bmw.gogo();

        //1.创建一个增强器
        Enhancer enhancer = new Enhancer();
        //2.指定父类 (即要代理的类)
        enhancer.setSuperclass(BMW.class);
        //3.设置回调
        enhancer.setCallback(new MethodInterceptor() {
            /**
             *  调用每一个方法都会进入拦截器
             * @param o:当前创建好的代理对象
             * @param method：方法
             * @param args：参数
             * @param methodProxy：代理方法
             * @return
             * @throws Throwable
             */
            @Override
            public Object intercept(Object o,
                                    Method method,
                                    Object[] args,
                                    MethodProxy methodProxy) throws Throwable {
                //System.out.println(o);
                System.out.println(method);
                System.out.println(args);
                System.out.println(methodProxy);

                //执行目标方法
                method.invoke(bmw,args);

                methodProxy.invokeSuper(o,args);
                return null;
            }
        });

        System.out.println("====================");

        //4.创建一个代理对象
        BMW bmw1 = (BMW)enhancer.create();
        bmw1.gogo();
    }


    /**
     *
     * 测试JDK动态代理
     *   必须要实现接口
     * @param args
     */
    public static void JDKDynamic(String[] args) {

        //创建普通对象
        Wuling wuling1 = new Wuling();
        wuling1.setName("Mini EV1");
        wuling1.gogo();

//        Wuling wuling2 = new Wuling();
//        wuling2.setName("Mini EV2");
//        wuling2.gogo();
//
//        Wuling wuling3 = new Wuling();
//        wuling3.setName("Mini EV3");
//        wuling3.gogo();

        //创建代理对象：获得的是一个代理对象
        Car proxy = (Car)Proxy.newProxyInstance(Wuling.class.getClassLoader(),
                Wuling.class.getInterfaces(),
                new InvocationHandler() {  //执行处理器
                    /**
                     * @param proxy  代理对象：可以拦截真实对象的执行意图
                     * @param method  当前方法
                     * @param args  参数
                     * @return
                     * @throws Throwable
                     */
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        //1.利用反射执行方法
                        System.out.println("代理正在执行中");

                        //执行真正的目标方法
                        //1.方法执行之前：前置通知
                        Object invoke = null;
                        try {
                            invoke = method.invoke(wuling1, args);
                            //2.目标方法正常执行完成：返回通知
                        }catch (Exception e){
                            //3.异常通知
                        }finally{
                            //4.后置通知
                        }


                        return invoke;
                    }
                });
        System.out.println("==================");
        proxy.gogo();
    }


}

interface Car{
    void gogo();

}

@Data
class BMW{
    private String name;
    public void gogo(){
        System.out.println(name + "汽车行驶中");
    }
}

@Data
class Wuling implements Car {

    private String name;

    @Override
    public void gogo(){
        System.out.println(name + "汽车行驶中");
    }
}
