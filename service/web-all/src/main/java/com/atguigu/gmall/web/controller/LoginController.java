package com.atguigu.gmall.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 杨林
 * @create 2022-12-15 10:34 星期四
 * description:跳转到登录页面
 */
@Controller
public class LoginController {

    //originUrl:保证登录成功后跳回到登录前的页面

    /**
     * 跳转到登录页面
     * @param originUrl
     * @return
     */
    @GetMapping("/login.html")
    public String loginPage(@RequestParam("originUrl") String originUrl,
                            Model model){

        model.addAttribute("originUrl",originUrl);
        return "login";
    }
}
