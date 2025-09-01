package com.tjetc.common.admin.vo.param;

import lombok.Data;

//用于接受前端传给controller方法的参数
@Data
public class AdminParam {
    private Long id;
    private String username;
    private String password;
    private String imagePath;
    private String email;
    private String phone;
    private Integer role;
}
