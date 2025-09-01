package com.tjetc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tjetc.entity.Message;
import com.tjetc.entity.Notice;
import org.apache.ibatis.annotations.Param;

public interface NoticeMapper extends BaseMapper<Notice> {
    Page<Notice> selectPage(Page<Notice> pageNotice);
    Page<Notice> selectPage1(Page<Notice> pageMessage, @Param("userId") Long userId );


}
