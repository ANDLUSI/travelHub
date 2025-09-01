package com.tjetc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tjetc.entity.Admin;
import com.tjetc.entity.Message;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageMapper extends BaseMapper<Message> {
    List<Message> findNum(Long id);

    @Delete("DELETE FROM message_read WHERE notice_id = #{id}")
    int deleteByNoticeId(Long noticeId);

}
