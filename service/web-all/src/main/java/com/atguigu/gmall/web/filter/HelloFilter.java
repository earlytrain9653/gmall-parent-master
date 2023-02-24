package com.atguigu.gmall.web.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 杨林
 * @create 2022-12-15 19:29 星期四
 * description:  测试Filter的适用
 */
public class HelloFilter extends HttpFilter {

    /**
     *
     * @param request  请求
     * @param response  响应
     * @param chain  filter链
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {

        //放行请求
        //放行前 修改请求信息
        chain.doFilter(request,response);

        //放行后 修改响应信息

        super.doFilter(request, response, chain);
    }
}
