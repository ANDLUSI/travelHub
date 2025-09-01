package com.tjetc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tjetc.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

//BaseMapper 提供了很多方法让我们使用，例如 单表操作的增删改查
public interface UserMapper extends BaseMapper<User> {
    Page<User> selectPage(Page<User> pageUser);
    Page<User> selectPageLikeUsername(Page<User> pageUser, @Param("username") String username);

    User selectByUsername(String username);
    Page<User> selectPageLikeUsernameAndStatus(Page<User> pageUser, @Param("username") String username,@Param("status")Integer status);

    User selectLogin( @Param("username") String username,
                      @Param("password") String password);

    List<Long> getAllUserIds();

    List<User> listMaps();
}
