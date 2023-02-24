package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.FileService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Api(tags = "文件管理")
@RestController
@RequestMapping("/admin/product")
public class FileController {

    @Autowired
    FileService fileService;

    /**
     * 文件上传:
     *   多部件上传
     *   web开发
     *      请求
     *          请求首行（请求方式  请求协议  请求地址）
     *              @PathVarable：请求首行工作 获取路径位置参数
     *              @RequestParam：查询字符串或请求体
     *          请求头
     *              @RequestHeader:获取请求头
     *              @CookieValue:获取kookie的 值
     *          请求体
     *               @RequestBody获取请求体的所有参数
     *               @RequestPart：获取请求体的文件项
     *      响应
     *          响应首行（响应状态码）
     *          响应头
     *          响应体
     *  完整的请求地址：
     *      http://api.gmall.com:80/admin/prduct/xxx?key=value
     *     协议://主机:端口/路径？查询字符串   @PathVarable：请求首行工作 获取路径位置参数
     *     请求体中带参数
     *
     *   @RequestParam
     *   @RequestPart：
     *   @RequestBody:
     *   @PathVarable
     * @return
     */
    @PostMapping("/fileUpload")
    public Result fileUpload(@RequestPart("file")MultipartFile file){
        log.info("文件上传：文件大小：{}",file.getSize());
        String url = null;
        try {
            url = fileService.upload(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.ok(url);
    }

}
