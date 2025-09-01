package com.tjetc.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tjetc.common.JsonResult;
import com.tjetc.common.JwtTokenUtil;
import com.tjetc.common.user.vo.data.UserView;
import com.tjetc.common.user.vo.param.UserParam;
import com.tjetc.dao.UserMapper;
import com.tjetc.entity.Admin;
import com.tjetc.entity.User;
import com.tjetc.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class UserServiceImpl implements UserService {
    @Value("${token.expiration}")
    private int tokenExpiration;
    @Autowired
    private UserMapper userMapper;

    @Override
    public JsonResult login(String username, String password) {
        if (StringUtils.isAnyBlank(username, password)) {
            return JsonResult.fail("用户名或者密码不能为空");
        }
        User user = userMapper.selectLogin(username, password);
        if (user == null) {
            return JsonResult.fail("用户名或密码错误");
        } else {
            if (user.getStatus() == 0) {
                return JsonResult.fail("该账号被封禁中");
            }

            // 检查用户创建时间并设置头衔
            LocalDateTime accountCreationDate = user.getCreateTime();
            LocalDateTime now = LocalDateTime.now();
            long daysSinceCreation = ChronoUnit.DAYS.between(accountCreationDate, now);

            String title = "小萌新"; // 默认头衔
            if (daysSinceCreation > 10) {
                title = "大萌新";
            } else if (daysSinceCreation > 5) {
                title = "中萌新";
            }

            // 更新用户的头衔
            user.setTitle(title);
            userMapper.updateById(user);

            // 生成token
            Map<String, Object> claims = new HashMap<>();
            claims.put("adminId", user.getId());
            String token = JwtTokenUtil.generateToken(claims, "admin", tokenExpiration);

            return JsonResult.success(token, user);
        }
    }


    @Override
    public JsonResult findById(Long id){
        //selectById是mybatisplus提供的单表查询操作，生成sql语句都是mybatisplus内置了
        User user = userMapper.selectById(id);

        UserView userView = new UserView();
        BeanUtils.copyProperties(user, userView);
        return JsonResult.success(userView);
    }

    @Override
    public JsonResult findPage(String username, Integer pageNo, Integer pageSize) {
        //设置页码和每页数量
        Page<User> page = Page.of(pageNo,pageSize);
        //把page作为分页参数，传入方法中
        Page<User> userPage = userMapper.selectPageLikeUsername(page,username);
        IPage<UserView> userViewPage = userPage.convert(new Function<User, UserView>() {
            @Override
            public UserView apply(User user) {
                UserView userView = new UserView();
                BeanUtils.copyProperties(user,userView);
                return userView;
            }

        });
        return JsonResult.success(userViewPage);
    }

    @Override
    public JsonResult editStatus(Long id, int status) {
        User user = userMapper.selectById(id);

        // 检查用户是否存在
        if (user == null) {
            return JsonResult.fail("用户不存在");
        }
        // 更新用户的角色
        user.setStatus(status);

        // 保存更新后的用户信息到数据库
        int result = userMapper.updateById(user);

        // 检查更新是否成功
        if (result > 0) {
            return JsonResult.success("修改成功",null);
        } else {
            return JsonResult.fail("Failed to update role");
        }
    }

    @Override
    public JsonResult deleteById(Long id) {
        int rows = userMapper.deleteById(id);
        if(rows > 0){
            return JsonResult.success("删除成功");
        }else{
            return JsonResult.fail("删除失败");
        }
    }

    @Override
    public JsonResult saveUser(UserParam userParam) {
        //通过判断id值有无判断是新增更新，id值无是新增，id值有就是更新
        User user = null;
        LocalDateTime now = LocalDateTime.now();
        if(userParam.getId() == null || userParam.getId() <= 0){
            //新增
            //判断用户名或密码不能为空
            if(StringUtils.isAnyBlank(userParam.getUsername(),userParam.getPassword())) {
                return JsonResult.fail("新增用户，用户名和密码不能为空");
            }
            //判断用户名不能重复
            User existsUser = userMapper.selectByUsername(userParam.getUsername());
            if(existsUser != null){
                return JsonResult.fail("用户名已经存在");
            }

            user = new User();
            BeanUtils.copyProperties(userParam,user);
            user.setCreateTime(now);
            user.setUpdateTime(now);
            userMapper.insert(user);
            return JsonResult.success("新增成功",null);
        }else{
            //更新
            //根据id查询Admin信息
            user = userMapper.selectById(userParam.getId());
            if(user == null){

                return JsonResult.fail("用户不存在，更新失败");
            }
            System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
            System.out.println(userParam);
            System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
            //要更新的字段
            user.setImagePath(userParam.getImagePath());
            user.setStatus(userParam.getStatus());
            user.setEmail(userParam.getEmail());
            user.setPassword(userParam.getPassword());
            user.setPhone(userParam.getPhone());
            user.setLocation(userParam.getLocation());
            user.setTitle(userParam.getTitle());
            user.setStatus(userParam.getStatus());
            user.setUpdateTime(now);
            int rows = userMapper.updateById(user);
            //要判断更新的影响行数
            if(rows > 0){
                return JsonResult.success("更新成功",null);
            }else{
                return JsonResult.fail("用户不存在，更新失败");
            }
        }
    }

    @Override
    public JsonResult save(UserParam userParam) {
        User user = userMapper.selectByUsername(userParam.getUsername());
        if(user == null){
            User user1 = new User();
            LocalDateTime now = LocalDateTime.now();
            user1.setPassword(userParam.getPassword());
            user1.setUsername(userParam.getUsername());
            user1.setLocation(userParam.getLocation());

            user1.setCreateTime(now);
            user1.setUpdateTime(now);
            user1.setStatus(1);
            user1.setTitle("小萌新");
            userMapper.insert(user1);
            return JsonResult.success("新增成功", null);
        }
        else{
            return JsonResult.fail("该用户已存在");
        }
    }

    @Override
    public JsonResult findPageStatus(String username, Integer pageNo, Integer pageSize, Integer status) {

        Page<User> page = Page.of(pageNo,pageSize);
        //把page作为分页参数，传入方法中
        Page<User> userPage = userMapper.selectPageLikeUsernameAndStatus(page,username,status);
        IPage<UserView> userViewPage = userPage.convert(new Function<User, UserView>() {
            @Override
            public UserView apply(User user) {
                UserView userView = new UserView();
                BeanUtils.copyProperties(user,userView);
                return userView;
            }
        });
        return JsonResult.success(userViewPage);
    }

    @Transactional
    @Override
    public JsonResult updateLogTime(Long id){
        User user = userMapper.selectById(id);
        // 检查用户是否存在
        if (user == null) {
            return JsonResult.fail("出错了，该管理员不存在");
        }
        // 更新lastLogTime为当前时间
        LocalDateTime now = LocalDateTime.now();
        user.setLastlogTime(now);

        // 执行更新操作
        int result = userMapper.updateById(user);

        // 检查更新是否成功
        if (result > 0) {
            return JsonResult.success("Log time updated successfully");
        } else {
            return JsonResult.fail("Failed to update log time");
        }

    }

    @Override
    public JsonResult getNewUserStats() {
        List<User> userStats = userMapper.listMaps();
        System.out.println(userStats);
        return JsonResult.success(userStats);
    }

    @Override
    public JsonResult getUserInfo() {
        List<User> users = userMapper.listMaps();
        return JsonResult.success(users);
    }
}
