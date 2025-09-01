package com.tjetc.common.user.vo.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 返回前端页面的数据对象
 */
@Data
public class UserView {
    private Long id;
    private String username;
    private String password;
    private String imagePath;
    private String phone;
    private String email;
    private String location;
    private String title;
    private int status;
    //json日期格式化
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastlogTime;

}
