package com.tjetc.common.user.vo.param;

import lombok.Data;

//用于接受前端传给controller方法的参数
@Data
public class UserParam {
    private Long id;
    private String username;
    private String password;
    private String imagePath;
    private String email;
    private String phone;
    private Integer status;
    private String location;
    private String title;

}
