package com.tjetc.controller;

import com.tjetc.common.JsonResult;
import com.tjetc.common.comment.vo.data.CommentView;
import com.tjetc.entity.Comment;
import com.tjetc.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("comment")
public class CommentController {
    @Value("${file.basePath}")
    private String fileBasePath;

    @Autowired
    private CommentService commentService;

    @RequestMapping("page")
    public JsonResult page(@RequestParam(value="title",required = false,defaultValue = "")String title,
                           @RequestParam(value="pageNo",required = false,defaultValue = "1")Integer pageNo,
                           @RequestParam(value="pageSize",required = false,defaultValue = "2")Integer pageSize,
                           @RequestParam(value="sortOrder",required = false,defaultValue = "")String sortOrder) {
        return commentService.pageAll(title, pageNo, pageSize,sortOrder);
    }

    @RequestMapping("page/{id}")
    public List<CommentView> findChildRow(@PathVariable("id") Long id) {
        return commentService.findChildRow(id);
    }

    @RequestMapping("delete/{id}")
    public JsonResult updateById(@PathVariable("id") Long id){
        return commentService.updateById(id);
    }
    @RequestMapping("findComment")
    public JsonResult findComment() {
        return commentService.getCommentInfo();
    }
}
