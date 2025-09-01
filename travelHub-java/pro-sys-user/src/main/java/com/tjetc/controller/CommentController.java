package com.tjetc.controller;

import com.tjetc.common.JsonResult;
import com.tjetc.entity.Comment;
import com.tjetc.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("comment")
public class CommentController {
    @Value("${file.basePath}")
    private String fileBasePath;

    @Autowired
    private CommentService commentService;

    @RequestMapping("guideC/{id}")
    public JsonResult detail(@PathVariable("id") Long id) {
        return commentService.findByGuideId(id);
    }
    @RequestMapping("like/{userId}/{commentId}")
    public JsonResult panchong(@PathVariable("userId") Long userId, @PathVariable("commentId") Long commentId) {
        return commentService.judge(userId,commentId);
    }
    @RequestMapping("save")
    public JsonResult save(@RequestParam(value="userId",required = false,defaultValue = "")Long userId,
                           @RequestParam(value="guideId",required = false,defaultValue = "")Long guideId,
                           @RequestParam(value="content",required = false,defaultValue = "")String content,
                           @RequestParam(value="parentId",required = false,defaultValue = "")Long parentId,
                           @RequestParam(value="root",required = false,defaultValue = "")Long root) {
        return commentService.saveComment(userId,guideId,content,parentId,root);
    }
    @RequestMapping("delete/{id}")
    public JsonResult delete(@PathVariable("id") Long id) {
        return commentService.updateById(id);
    }
}
