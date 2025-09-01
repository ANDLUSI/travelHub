package com.tjetc.service;

import com.tjetc.common.JsonResult;
import com.tjetc.common.admin.vo.param.AdminParam;

public interface AdminService {
    /**
     * 根据id查询admin信息
     * @param id
     * @return
     */
    JsonResult findById(Long id);

    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    JsonResult login(String username, String password);

    /**
     * 分页查询用户信息
     *
     * @param username
     * @param pageNo
     * @param pageSize
     * @return
     */
    JsonResult findPage(String username,Integer pageNo, Integer pageSize);

    //筛选权限分页查询
    JsonResult findPageRole(String username,Integer role,Integer pageNo, Integer pageSize);

    /**
     * 根据id删除管理员信息
     *
     * @param id
     * @return
     */
    JsonResult deleteById(Long id);

    JsonResult save(AdminParam adminParam);

    JsonResult saveAdmin(AdminParam adminParam);

    JsonResult updateLogTime(Long id);

    JsonResult editRole(Long id, int role);
}
