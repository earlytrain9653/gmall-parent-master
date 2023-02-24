package com.atguigu.gmall.item;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.product.entity.SkuImage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 杨林
 * @create 2022-12-09 20:34 星期五
 * description:
 */
public class GenericTest {

    /**
     * 表达式进阶测试
     */
    @Test
    public void test4(){
        //1.创建一个表达式解析器
        SpelExpressionParser parser = new SpelExpressionParser();

        //2.解析一个表达式:定界符   默认：#{ }
        //Expression expression = parser.parseExpression("sku:#{10/2}:#{1+1}", ParserContext.TEMPLATE_EXPRESSION);
        Expression expression = parser.parseExpression("sku:info:#{#args[1]}",
                ParserContext.TEMPLATE_EXPRESSION);

        //计算上下文:保存计算期间的数据
        EvaluationContext ec = new StandardEvaluationContext();
        ec.setVariable("args",new long[] {88L,66L,77L});
        //3.得到值
        String value = expression.getValue(ec, String.class);
        System.out.println(value);

    }


    /**
     * 测试表达式的使用
     */
    @Test
    public void test3(){
        //1.创建一个表达式解析器
        SpelExpressionParser parser = new SpelExpressionParser();

        //2.解析一个表达式
        Expression expression = parser.parseExpression("1+1");

        //3.得到表达式计算出来的值
        Object value = expression.getValue();

        System.out.println(value);
    }


    @Test
    public void test2(){
        List<SkuImage> images = new ArrayList<>();
        SkuImage image1 = new SkuImage();
        image1.setId(0L);
        image1.setSkuId(0L);
        image1.setImgName("0");
        image1.setImgUrl("0");
        image1.setSpuImgId(0L);
        image1.setIsDefault("0");
        SkuImage image2 = new SkuImage();
        image2.setId(1L);
        image2.setSkuId(1L);
        image2.setImgName("1");
        image2.setImgUrl("1");
        image2.setSpuImgId(1L);
        image2.setIsDefault("1");
        images.add(image1);
        images.add(image2);
        String s = JSON.toJSONString(images);
        //System.out.println(s);
        
        //将json字符串逆转回去
        Type type = null;
        Method[] methods = GenericTest.class.getMethods();
        for (Method method : methods) {
            if (method.getName().equals("haha")){
                type = method.getGenericReturnType();
            }
        }

        List<SkuImage> o = (List<SkuImage>)JSON.parseObject(s, type);
        System.out.println(o);

    }

    @Test
    public void test(){
        Method[] methods = GenericTest.class.getDeclaredMethods();
        for (Method method : methods) {
            System.out.println(method.getName() + "=====>" + method.getGenericReturnType());
        }
    }

    List<String> haha(){
        return  null;
    }

    String hehe(){
        return "";
    }

    Map<String,List<String>> heihei(){

        return null;
    }
}
