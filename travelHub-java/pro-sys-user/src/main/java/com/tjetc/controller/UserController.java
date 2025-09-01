package com.tjetc.controller;

import com.tjetc.common.FileUploadUtil;
import com.tjetc.common.JsonResult;
import com.tjetc.common.user.vo.param.UserParam;
import com.tjetc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("user")
public class UserController {
    @Value("${file.basePath}")
    private String fileBasePath;

    @Autowired
    private UserService userService;

    @RequestMapping("detail/{id}")
    public JsonResult detail(@PathVariable("id") Long id) {
        return userService.findById(id);
    }
    //
    //@RequestMapping("page")
    //public JsonResult page(@RequestParam(value="username",required = false,defaultValue = "")String username,
    //                       @RequestParam(value="pageNo",required = false,defaultValue = "1")Integer pageNo,
    //                       @RequestParam(value="pageSize",required = false,defaultValue = "2")Integer pageSize) {
    //    return adminService.findPage(username, pageNo, pageSize);
    //}
    //
    //@RequestMapping("pageRole")
    //public JsonResult pageRole(@RequestParam(value="username",required = false,defaultValue = "")String username,
    //                           @RequestParam(value="role",required = false,defaultValue = "")Integer role,
    //                       @RequestParam(value="pageNo",required = false,defaultValue = "1")Integer pageNo,
    //                       @RequestParam(value="pageSize",required = false,defaultValue = "2")Integer pageSize) {
    //    return adminService.findPageRole(username, role, pageNo, pageSize);
    //}
    //
    //@RequestMapping("delete/{id}")
    //public JsonResult delete(@PathVariable("id") Long id) {
    //    return adminService.deleteById(id);
    //}
    //
    @RequestMapping("upload/image")
    public JsonResult uploadImage(@RequestParam("imagefile") MultipartFile imageFile) {
        String imagePath = FileUploadUtil.upload(imageFile,fileBasePath,"image");
        return JsonResult.success(imagePath);
    }

    //接收参数格式是json
    @RequestMapping("saveUser")
    public JsonResult save(@RequestBody UserParam userParam) {
        return userService.saveUser(userParam);
    }
    //
    //更新登录时间
    @RequestMapping("updateTime/{id}")
    public JsonResult updateById(@PathVariable("id") Long id){
        return userService.updateLogTime(id);
    }

}
