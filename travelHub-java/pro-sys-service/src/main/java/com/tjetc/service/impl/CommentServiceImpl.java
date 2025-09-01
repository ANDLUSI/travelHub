package com.tjetc.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tjetc.common.JsonResult;
import com.tjetc.common.comment.vo.data.CommentView;
import com.tjetc.common.scenic.vo.data.ScenicView;
import com.tjetc.dao.*;
import com.tjetc.entity.*;
import com.tjetc.service.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
public class CommentServiceImpl implements CommentService {
    @Value("${token.expiration}")
    private int tokenExpiration;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private CommentLikeMapper commentLikeMapper;
    @Autowired
    private GuideMapper guideMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NoticeMapper noticeMapper;
    @Autowired
    private MessageMapper messageMapper;

    @Override
    public JsonResult pageAll(String title, Integer pageNo, Integer pageSize, String sortOrder) {
        //设置页码和每页数量
        Page<Comment> page = Page.of(pageNo, pageSize);

        // 获取顶级评论分页数据
        Page<Comment> topCommentPage = commentMapper.pageByTimeAndTitle(page, title, sortOrder);

        // 创建一个新的分页对象用于存储顶级评论和它们的子评论
        List<CommentView> commentViewList = new ArrayList<>();

        for (Comment topComment : topCommentPage.getRecords()) {
            // 转换顶级评论为CommentView
            CommentView topCommentView = new CommentView();
            BeanUtils.copyProperties(topComment, topCommentView);
            commentViewList.add(topCommentView);

            // 获取当前顶级评论的子评论
            List<Comment> childComments = commentMapper.selectChildComments(topComment.getId());

            // 转换子评论为CommentView并添加到commentViewList
            for (Comment childComment : childComments) {
                CommentView childCommentView = new CommentView();
                BeanUtils.copyProperties(childComment, childCommentView);
                commentViewList.add(childCommentView);
            }
        }

        // 设置返回的分页结果
        IPage<CommentView> commentViewPage = new Page<>(pageNo, pageSize, topCommentPage.getTotal());
        commentViewPage.setRecords(commentViewList);

        return JsonResult.success(commentViewPage);
    }

    @Override
    public List<CommentView> findChildRow(Long id) {
        List<CommentView> comment = commentMapper.findChildRow(id);
        return comment ;
    }

    @Override
    public JsonResult updateById(Long id) {
        Comment comment = commentMapper.selectById(id);
        // 检查用户是否存在
        if (comment == null) {
            return JsonResult.fail("出错了，该评论不存在");
        }
        if(comment.getIsDelete()==0) {
            System.out.println("----------------------");
            Guide guide = guideMapper.selectById(comment.getGuideId());
            guide.setCommentCount(guide.getCommentCount() - 1);
            guideMapper.updateById(guide);
            if(comment.getParentId()!=null) {
                Comment comment1 = commentMapper.selectById(comment.getParentId());
                comment1.setApplyCount(comment1.getApplyCount() - 1);
                commentMapper.updateById(comment1);
            }
            if(comment.getRoot()!=null) {
                Comment comment2 = commentMapper.selectById(comment.getRoot());
                comment2.setApplyCount(comment2.getApplyCount() - 1);
                commentMapper.updateById(comment2);
            }
        }
        // 更新lastLogTime为当前时间
        LocalDateTime now = LocalDateTime.now();
        comment.setUpdateTime(now);
        comment.setContent("该评论已删除！");
        comment.setIsDelete(1);
        // 执行更新操作
        int result = commentMapper.updateById(comment);

        // 检查更新是否成功
        if (result > 0) {
            return JsonResult.success("Log time updated successfully");
        } else {
            return JsonResult.fail("Failed to update log time");
        }
    }

    @Override
    public JsonResult findByGuideId(Long id) {
        List<CommentView> comments = commentMapper.selectByGuideId(id);
        if (comments==null || comments.size()==0) {
            return JsonResult.success("暂无评论");
        }
        return JsonResult.success(comments);
    }

    @Override
    public JsonResult judge(Long userId, Long commentId) {
        CommentLike commentLike = commentMapper.selectByTwoId(userId, commentId);
        if (commentLike == null) {
            CommentLike commentLike1 = new CommentLike();
            commentLike1.setUserId(userId);
            commentLike1.setCommentId(commentId);
            commentLike1.setCreateTime(LocalDateTime.now());
            commentLikeMapper.insert(commentLike1);
            Comment comment = commentMapper.selectById(commentId);
            comment.setLikeCount(comment.getLikeCount() + 1);
            commentMapper.updateById(comment);

            Comment comment1 = commentMapper.selectById(commentId);
            String content = comment1.getContent();
            User user = userMapper.selectById(userId);

            if(userId==comment1.getUserId()){
                return JsonResult.success(commentLike);
            }
            else {
                if (content.length() > 10) {
                    content = content.substring(0, 10) + "...";
                }
                // 新建通知，设置通知
                String username = user.getUsername();
                Notice notice = new Notice();
                notice.setContent(username + "点赞了你的\"" + content + "\"评论");
                notice.setCreateTime(LocalDateTime.now());
                notice.setUpdateTime(LocalDateTime.now());
                notice.setTitle("点赞提醒");
                notice.setTarget(0);
                noticeMapper.insert(notice);

                // 获取评论者的id，并将通告发给用户
                Long id = comment1.getUserId();
                Message message = new Message();
                message.setNotified(0);
                message.setIsRead(0);
                message.setUserId(id);
                message.setNoticeId(Math.toIntExact(notice.getId()));
                messageMapper.insert(message);
            }
        }
        return JsonResult.success(commentLike);
    }


    @Override
    public JsonResult saveComment(Long userId, Long guideId, String content, Long parentId, Long root) {
        if (guideId == null) {
            return JsonResult.fail("guideId 不能为空");
        }
        //新建的评论
        Comment comment = new Comment();
        comment.setLikeCount(0L);
        comment.setUserId(userId);
        comment.setGuideId(guideId);
        comment.setContent(content);
        comment.setParentId(parentId);
        comment.setRoot(root);
        comment.setUpdateTime(LocalDateTime.now());
        comment.setCreatedTime(LocalDateTime.now());
        comment.setApplyCount(0L);
        comment.setIsDelete(0);
        int rows = commentMapper.insert(comment);

        //找到对应的攻略
        Guide guide = guideMapper.selectById(guideId);
        //找到人
        User user = userMapper.selectById(userId);

        //判断加通知，加谁的通知，
        //对攻略设置评论通知：
        if (parentId == null && userId !=guide.getAuthorId()){
            String title = guide.getTitle();
            String username = user.getUsername();
            Notice notice = new Notice();
            notice.setContent(username + "回复了你的\"" + title + "\"攻略");
            notice.setCreateTime(LocalDateTime.now());
            notice.setUpdateTime(LocalDateTime.now());
            notice.setTitle("回复提醒");
            notice.setTarget(0);
            noticeMapper.insert(notice);

            //获取作者的id，并将通告发给作者
            Long id = guide.getAuthorId();
            Message message = new Message();
            message.setNotified(0);
            message.setIsRead(0);
            message.setUserId(id);
            message.setNoticeId(Math.toIntExact(notice.getId()));
            messageMapper.insert(message);
        }else if(parentId!=null){
            //对评论的评论通知，先判断回复的是不是自己的评论
            Comment parentComment = commentMapper.selectById(parentId);
            if (userId != parentComment.getUserId()){
                if (content.length() > 10) {
                    content = content.substring(0, 10) + "...";
                }
                // 新建通知，设置通知
                String username = user.getUsername();
                Notice notice = new Notice();
                notice.setContent(username + "回复了你的\"" + parentComment.getContent() + "\"评论("+guide.getTitle()+"攻略)");
                notice.setCreateTime(LocalDateTime.now());
                notice.setUpdateTime(LocalDateTime.now());
                notice.setTitle("回复提醒");
                notice.setTarget(0);
                noticeMapper.insert(notice);

                // 获取评论者的id，并将通告发给用户
                Message message = new Message();
                message.setNotified(0);
                message.setIsRead(0);
                message.setUserId(parentComment.getUserId());
                message.setNoticeId(Math.toIntExact(notice.getId()));
                messageMapper.insert(message);
            }

        }

        if(rows > 0){
            if(parentId!=null) {
                Comment comment1 = commentMapper.selectById(parentId);
                comment1.setApplyCount(comment1.getApplyCount() + 1);
                commentMapper.updateById(comment1);
            }
            guide.setCommentCount(guide.getCommentCount()+1);
            guideMapper.updateById(guide);
            return JsonResult.success("评论成功",1);
        }else{
            return JsonResult.fail("评论失败");
        }
    }

    @Override
    public JsonResult getCommentInfo() {
        List<Comment> comments = commentMapper.listMaps();
        return JsonResult.success(comments);
    }
}
