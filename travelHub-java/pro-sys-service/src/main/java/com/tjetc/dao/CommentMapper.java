package com.tjetc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tjetc.common.comment.vo.data.CommentView;
import com.tjetc.entity.Comment;
import com.tjetc.entity.CommentLike;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.io.Serializable;
import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {
    Page<Comment> pageByTimeAndTitle(Page<Comment> page, @Param("title")String title,@Param("sortOrder")String sortOrder);

    List<CommentView> findChildRow(Long id);

    Comment selectById(Long id);

    List<CommentView> selectByGuideId(Long id);

    CommentLike selectByTwoId(@Param("userId")Long userId, @Param("commentId")Long commentId);

    @Select("SELECT c.*, " +
            "u.username AS replyToUser, " +
            "u2.username AS username " +
            "FROM comment c " +
            "LEFT JOIN t_user u ON u.id = (SELECT user_id FROM comment WHERE id = c.parent_id) " +
            "LEFT JOIN t_user u2 ON u2.id = c.user_id " +
            "WHERE c.root = #{rootId} " +
            "ORDER BY c.created_time ASC")

    List<Comment> selectChildComments(@Param("rootId") Long rootId);

    List<Comment> listMaps();
}
