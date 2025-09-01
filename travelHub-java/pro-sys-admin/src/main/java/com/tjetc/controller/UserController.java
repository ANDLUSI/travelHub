package com.tjetc.controller;

import com.tjetc.common.JsonResult;
import com.tjetc.common.admin.vo.param.AdminParam;
import com.tjetc.common.user.vo.param.UserParam;
import com.tjetc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserController {
    @Value("${file.basePath}")
    private String fileBasePath;
    @Autowired
    private UserService userService;

    @RequestMapping("page")
    public JsonResult page(@RequestParam(value="username",required = false,defaultValue = "")String username,
                           @RequestParam(value="pageNo",required = false,defaultValue = "1")Integer pageNo,
                           @RequestParam(value="pageSize",required = false,defaultValue = "2")Integer pageSize) {
        return userService.findPage(username,pageNo, pageSize);
    }

    @RequestMapping("detail/{id}")
    public JsonResult detail(@PathVariable("id") Long id) {
        return userService.findById(id);
    }

    @RequestMapping("editStatus/{id}/{type}")
    public JsonResult editTime(@PathVariable("id") Long id, @PathVariable("type") int type){
        return userService.editStatus(id,type);
    }

    @RequestMapping("delete/{id}")
    public JsonResult delete(@PathVariable("id") Long id) {
        return userService.deleteById(id);
    }

    @RequestMapping("saveUser")
    public JsonResult save(@RequestBody UserParam userParam) {
        return userService.saveUser(userParam);
    }

    @RequestMapping("pageStatus")
    public JsonResult pageStatus(@RequestParam(value="username",required = false,defaultValue = "")String username,
                           @RequestParam(value="pageNo",required = false,defaultValue = "1")Integer pageNo,
                           @RequestParam(value="pageSize",required = false,defaultValue = "2")Integer pageSize,
                           @RequestParam(value="status",required = false,defaultValue = "")Integer status) {
        return userService.findPageStatus(username,pageNo, pageSize,status);
    }

    @RequestMapping("findUser")
    public JsonResult findUser() {
        return userService.getUserInfo();
    }
}
