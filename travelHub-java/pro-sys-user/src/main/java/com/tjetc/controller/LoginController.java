package com.tjetc.controller;

import com.tjetc.common.JsonResult;
import com.tjetc.service.AdminService;
import com.tjetc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
//CrossOrigin表示后端controller的方法允许跨域，不安全，不推荐
//CrossOrigin
public class LoginController {
    @Autowired
    private UserService userService;

    @RequestMapping("login")
    public JsonResult login(@RequestParam("username")String username,
                            @RequestParam("password")String password){
        return userService.login(username, password);
    }

}
