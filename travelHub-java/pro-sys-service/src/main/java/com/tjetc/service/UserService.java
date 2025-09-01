package com.tjetc.service;

import com.tjetc.common.JsonResult;
import com.tjetc.common.user.vo.param.UserParam;

public interface UserService {
    JsonResult login(String username, String password);

    JsonResult findById(Long id);
    JsonResult findPage(String username,Integer pageNo, Integer pageSize);
    JsonResult editStatus(Long id, int role);
    JsonResult deleteById(Long id);
    JsonResult saveUser(UserParam userParam);
    JsonResult save(UserParam userParam);
    JsonResult findPageStatus(String username,Integer pageNo, Integer pageSize,Integer status);
    JsonResult updateLogTime(Long id);
    JsonResult getNewUserStats();
    JsonResult getUserInfo();
}
