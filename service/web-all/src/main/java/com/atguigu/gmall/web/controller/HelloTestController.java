package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.config.exception.annotation.EnableAppException;
import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨林
 * @create 2022-12-10 21:35 星期六
 * description: 测试全局异常处理
 */
//@EnableAppException
@RestController
public class HelloTestController {

    @GetMapping("/div")
    public Result div(@RequestParam("num") Long num) {
//        try {
//            long i = 10 /num;
//            return Result.ok(10 / num);
//        }catch (Exception e){
//            return Result.fail();
//        }

        //只写业务逻辑  异常交给异常处理类
        long i = 10 / num;
        return Result.ok(10 / num);
    }

    /**
     * 处理本类的所有异常
     * @return
     */
//    @ExceptionHandler({Exception.class})
//    public Result handleException(Exception e){
//        Result<Object> fail = Result.fail();
//        fail.setMessage(e.getMessage());
//        return fail;
//    }
}
