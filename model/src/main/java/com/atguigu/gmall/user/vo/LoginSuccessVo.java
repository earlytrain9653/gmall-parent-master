package com.atguigu.gmall.user.vo;

import lombok.Data;

/**
 * @author 杨林
 * @create 2022-12-15 10:45 星期四
 * description:登录成功的vo
 */
@Data
public class LoginSuccessVo {
    private String token;
    private Long userId;
    private String nickName;
}
