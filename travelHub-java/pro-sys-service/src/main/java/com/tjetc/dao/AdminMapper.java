package com.tjetc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tjetc.entity.Admin;
import org.apache.ibatis.annotations.Param;

import java.util.List;

//BaseMapper 提供了很多方法让我们使用，例如 单表操作的增删改查
public interface AdminMapper extends BaseMapper<Admin> {
    Admin selectByUsernameAndPassword( @Param("username") String username,
                                        @Param("password") String password);

    /**
     * 根据username进行模糊匹配
     *
     * @param username
     * @return
     */
    Page<Admin> selectPageLikeUsername(Page<Admin> pageAdmin, @Param("username") String username);

    Admin selectByUsername(String username);

    Page<Admin> selectPageLikeUsernameAndRole( @Param("username") String username, @Param("role") Integer role,Page<Admin> pageAdmin);
}
