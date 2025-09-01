package com.tjetc.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tjetc.common.JsonResult;
import com.tjetc.common.JwtTokenUtil;
import com.tjetc.common.admin.vo.data.AdminView;
import com.tjetc.common.admin.vo.param.AdminParam;
import com.tjetc.dao.AdminMapper;
import com.tjetc.entity.Admin;
import com.tjetc.service.AdminService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class AdminServiceImpl implements AdminService {
    @Value("${token.expiration}")
    private int tokenExpiration;
    @Autowired
    private AdminMapper adminMapper;

    @Override
    public JsonResult findById(Long id){
        //selectById是mybatisplus提供的单表查询操作，生成sql语句都是mybatisplus内置了
        Admin admin = adminMapper.selectById(id);

        AdminView adminView = new AdminView();
        BeanUtils.copyProperties(admin, adminView);
        return JsonResult.success(adminView);
    }

    @Override
    public JsonResult login(String username, String password){
        //StringUtils.isAnyBlank 实参中任意一个为空，就返回true
        if(StringUtils.isAnyBlank(username,password)){
            return JsonResult.fail("用户名或者密码不能为空");
        }
        //根据用户名和密码查询Admin
        Admin admin = adminMapper.selectByUsernameAndPassword(username, password);
        if(admin == null){
            return JsonResult.fail("用户名或着密码错误");
        }else{
            //用户名或者密码正确，生成token
            Map<String,Object> claims = new HashMap<>();
            claims.put("adminId",admin.getId());
            String token = JwtTokenUtil.generateToken(claims, "admin", tokenExpiration);
            //token和admin对象全部给前端
            return JsonResult.success(token,admin);
        }
    }

    @Override
    public JsonResult findPage(String username, Integer pageNo, Integer pageSize) {
        //设置页码和每页数量
        Page<Admin> page = Page.of(pageNo,pageSize);
        //把page作为分页参数，传入方法中
        Page<Admin> adminPage = adminMapper.selectPageLikeUsername(page,username);
        //convert方法转换Page中records集合元素类型Admin---->AdminView
        //new Function<Admin,AdminView>(){};表示 创建一个 是Function接口的匿名类 对象
        IPage<AdminView> adminViewPage = adminPage.convert(new Function<Admin, AdminView>() {
            @Override
            public AdminView apply(Admin admin) {
                AdminView adminView = new AdminView();
                //spring的BeanUtils工具类进行属性copy,2个属性名要保持一致
                BeanUtils.copyProperties(admin,adminView);
                //AdminView adminView = new AdminView();
                //adminView.setId(admin.getId());
                //adminView.setUsername(admin.getUsername());
                //adminView.setImagePath(admin.getImagePath());
                //adminView.setCreateTime(admin.getCreateTime());
                //adminView.setUpdateTime(admin.getUpdateTime());
                return adminView;
            }

        });
        return JsonResult.success(adminViewPage);
    }

    @Override
    public JsonResult findPageRole(String username, Integer role, Integer pageNo, Integer pageSize) {
        //设置页码和每页数量
        Page<Admin> page = Page.of(pageNo,pageSize);
        //把page作为分页参数，传入方法中
        Page<Admin> adminPage = adminMapper.selectPageLikeUsernameAndRole(username,role,page);
        //convert方法转换Page中records集合元素类型Admin---->AdminView
        //new Function<Admin,AdminView>(){};表示 创建一个 是Function接口的匿名类 对象
        IPage<AdminView> adminViewPage = adminPage.convert(new Function<Admin, AdminView>() {
            @Override
            public AdminView apply(Admin admin) {
                AdminView adminView = new AdminView();
                BeanUtils.copyProperties(admin,adminView);
                return adminView;
            }
        });
        return JsonResult.success(adminViewPage);
    }

    @Override
    public JsonResult deleteById(Long id) {
        int rows = adminMapper.deleteById(id);
        if(rows > 0){
            return JsonResult.success("删除成功");
        }else{
            return JsonResult.fail("删除失败");
        }
    }

    //@Transactional用在service方法上，保证事务，
    //注意:默认情况@Transactional 只对运行时异常起作用(RuntimeException)
    @Transactional
    @Override
    public JsonResult save(AdminParam adminParam) {
        //通过判断id值有无判断是新增更新，id值无是新增，id值有就是更新
        Admin admin = null;
        LocalDateTime now = LocalDateTime.now();
        if(adminParam.getId() == null || adminParam.getId() <= 0){
            //新增
            //判断用户名或密码不能为空
            if(StringUtils.isAnyBlank(adminParam.getUsername(),adminParam.getPassword())) {
                return JsonResult.fail("新增用户，用户名和密码不能为空");
            }
            //判断用户名不能重复
            Admin existsAdmin = adminMapper.selectByUsername(adminParam.getUsername());
            if(existsAdmin != null){
                return JsonResult.fail("用户名已经存在");
            }

            admin = new Admin();
            BeanUtils.copyProperties(adminParam,admin);
            admin.setCreateTime(now);
            admin.setUpdateTime(now);
            adminMapper.insert(admin);
            return JsonResult.success("新增成功",null);
        }else{
            //更新
            //根据id查询Admin信息
            admin = adminMapper.selectById(adminParam.getId());
            if(admin == null){
                return JsonResult.fail("用户不存在，更新失败");
            }
            //要更新的字段
            admin.setImagePath(adminParam.getImagePath());
            admin.setUpdateTime(now);
            admin.setUsername(adminParam.getUsername());
            int rows = adminMapper.updateById(admin);
            //要判断更新的影响行数
            if(rows > 0){
                return JsonResult.success("更新成功",null);
            }else{
                return JsonResult.fail("用户不存在，更新失败");
            }
        }
    }


    @Transactional
    @Override
    public JsonResult updateLogTime(Long id){
        Admin admin = adminMapper.selectById(id);
        // 检查用户是否存在
        if (admin == null) {
            return JsonResult.fail("出错了，该管理员不存在");
        }
        // 更新lastLogTime为当前时间
        LocalDateTime now = LocalDateTime.now();
        admin.setLastlogTime(now);

        // 执行更新操作
        int result = adminMapper.updateById(admin);

        // 检查更新是否成功
        if (result > 0) {
            return JsonResult.success("Log time updated successfully");
        } else {
            return JsonResult.fail("Failed to update log time");
        }

    }

    @Override
    public JsonResult editRole(Long id, int role) {
        Admin admin = adminMapper.selectById(id);

        // 检查用户是否存在
        if (admin == null) {
            return JsonResult.fail("用户不存在");
        }
        // 更新用户的角色
        admin.setRole(role);

        // 保存更新后的用户信息到数据库
        int result = adminMapper.updateById(admin);

        // 检查更新是否成功
        if (result > 0) {
            return JsonResult.success("修改成功",null);
        } else {
            return JsonResult.fail("Failed to update role");
        }
    }

    @Transactional
    @Override
    public JsonResult saveAdmin(AdminParam adminParam) {
        //通过判断id值有无判断是新增更新，id值无是新增，id值有就是更新
        Admin admin = null;
        LocalDateTime now = LocalDateTime.now();
        if(adminParam.getId() == null || adminParam.getId() <= 0){
            //新增
            //判断用户名或密码不能为空
            if(StringUtils.isAnyBlank(adminParam.getUsername(),adminParam.getPassword())) {
                return JsonResult.fail("新增用户，用户名和密码不能为空");
            }
            //判断用户名不能重复
            Admin existsAdmin = adminMapper.selectByUsername(adminParam.getUsername());
            if(existsAdmin != null){
                return JsonResult.fail("用户名已经存在");
            }

            admin = new Admin();
            BeanUtils.copyProperties(adminParam,admin);
            admin.setCreateTime(now);
            admin.setUpdateTime(now);
            adminMapper.insert(admin);
            return JsonResult.success("新增成功",null);
        }else{
            //更新
            //根据id查询Admin信息
            admin = adminMapper.selectById(adminParam.getId());
            if(admin == null){

                return JsonResult.fail("用户不存在，更新失败");
            }
            System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
            System.out.println(adminParam);
            System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
            //要更新的字段
            admin.setUsername(adminParam.getUsername());
            admin.setImagePath(adminParam.getImagePath());
            admin.setRole(adminParam.getRole());
            admin.setEmail(adminParam.getEmail());
            admin.setPassword(adminParam.getPassword());
            admin.setPhone(adminParam.getPhone());
            admin.setUpdateTime(now);
            int rows = adminMapper.updateById(admin);
            //要判断更新的影响行数
            if(rows > 0){
                return JsonResult.success("更新成功",null);
            }else{
                return JsonResult.fail("用户不存在，更新失败");
            }
        }
    }

}
