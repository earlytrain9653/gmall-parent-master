package com.atguigu.gmall.common.config.exception.handler;

import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 杨林
 * @create 2022-12-10 21:59 星期六
 * description:  全局异常处理类
 */
//@ResponseBody
//@ControllerAdvice  //aop中advice代表通知  这个注解相当于controller的异常切面
@RestControllerAdvice //等于上面两个注解
public class GlobalExceptionHandler {


    /**
     * 处理和业务有关的异常  使用GmallException
     * @param e
     * @return
     */
    @ExceptionHandler(GmallException.class)
    public Result handlerGmallException(GmallException e){
        Result<Object> fail = Result.fail();
        fail.setCode(e.getCode());
        fail.setMessage(e.getMessage());
        return fail;
    }


    /**
     * 处理所有controller出现的其他异常
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result handlerException(Exception e){
        Result<Object> fail = Result.fail();
        fail.setMessage(e.getMessage());
        return fail;
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException exception){

        //1.从这个异常中拿到校验结果
        BindingResult bindingResult = exception.getBindingResult();
        //2.把结果整理好返回给前端
        Map<String,String> errorMap = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            String field = fieldError.getField();  //错误发生的属性
            String message = fieldError.getDefaultMessage();  //错误信息
            errorMap.put(field,message);
        }
        return Result.build(errorMap, ResultCodeEnum.INVALID_PARAM);
    }
}
