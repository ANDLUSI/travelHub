package com.tjetc.controller;

import com.tjetc.common.JsonResult;
import com.tjetc.common.user.vo.param.UserParam;
import com.tjetc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterController {
    @Value("${file.basePath}")
    private String fileBasePath;
    @Autowired
    private UserService userService;

    @RequestMapping("register")
    public JsonResult save(@RequestBody UserParam userParam) {
        return userService.save(userParam);
    }
}
