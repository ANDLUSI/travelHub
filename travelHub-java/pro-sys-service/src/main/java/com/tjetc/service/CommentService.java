package com.tjetc.service;

import com.tjetc.common.JsonResult;
import com.tjetc.common.comment.vo.data.CommentView;
import com.tjetc.entity.Comment;

import java.util.List;

public interface CommentService {
    JsonResult pageAll(String title,Integer pageNo,Integer pageSize,String sortOrder);
    List<CommentView> findChildRow(Long id);

    JsonResult updateById(Long id);

    JsonResult findByGuideId(Long id);

    JsonResult judge(Long userId,Long commentId);

    JsonResult saveComment(Long userId,Long guideId,String content,Long parentId,Long root);

    JsonResult getCommentInfo();
}
