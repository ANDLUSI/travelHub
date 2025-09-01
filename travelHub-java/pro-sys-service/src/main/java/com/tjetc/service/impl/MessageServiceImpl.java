package com.tjetc.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tjetc.common.JsonResult;
import com.tjetc.common.admin.vo.data.AdminView;
import com.tjetc.common.message.vo.data.MessageView;
import com.tjetc.common.notice.vo.data.NoticeView;
import com.tjetc.dao.CommentMapper;
import com.tjetc.dao.MessageMapper;
import com.tjetc.dao.NoticeMapper;
import com.tjetc.entity.Admin;
import com.tjetc.entity.Message;
import com.tjetc.entity.Notice;
import com.tjetc.service.MessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
public class MessageServiceImpl implements MessageService {
    @Value("${token.expiration}")
    private int tokenExpiration;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private NoticeMapper noticeMapper;
    @Override
    public JsonResult findById(Long id) {
        List<Message> messages = messageMapper.findNum(id);
        return JsonResult.success(messages.size());
    }

    @Override
    public JsonResult fadeById(Long id) {
        List<Message> messages = messageMapper.findNum(id);
        for (Message message : messages) {
            message.setNotified(1);
            message.setIsRead(1);
            messageMapper.updateById(message);
        }
        return JsonResult.success(messages.size());
    }

    @Override
    public JsonResult page(Long userId, Integer pageNo, Integer pageSize) {
        Page<Notice> page = Page.of(pageNo,pageSize);
        Page<Notice> noticePage = noticeMapper.selectPage1(page,userId);
        IPage<NoticeView> noticeViewPage = noticePage.convert(new Function<Notice, NoticeView>() {
            @Override
            public NoticeView apply(Notice notice) {
                NoticeView noticeView = new NoticeView();
                BeanUtils.copyProperties(notice,noticeView);
                return noticeView;
            }

        });
        return JsonResult.success(noticeViewPage);
    }
}
