package com.tjetc.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tjetc.common.JsonResult;
import com.tjetc.common.notice.vo.data.NoticeView;
import com.tjetc.common.notice.vo.param.NoticeParam;
import com.tjetc.common.scenic.vo.data.ScenicView;
import com.tjetc.dao.MessageMapper;
import com.tjetc.dao.NoticeMapper;
import com.tjetc.dao.UserMapper;
import com.tjetc.entity.Message;
import com.tjetc.entity.Notice;
import com.tjetc.service.NoticeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Service
public class NoticeServiceImpl implements NoticeService {
    @Value("${token.expiration}")
    private int tokenExpiration;
    @Autowired
    private NoticeMapper noticeMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MessageMapper messageMapper;

    @Override
    public JsonResult findPage(Integer pageNo, Integer pageSize) {
        //设置页码和每页数量
        Page<Notice> page = Page.of(pageNo, pageSize);
        //把page作为分页参数，传入方法中
        Page<Notice> noticePage = noticeMapper.selectPage(page);
        IPage<NoticeView> noticeViewPage = noticePage.convert(new Function<Notice, NoticeView>() {
            @Override
            public NoticeView apply(Notice notice) {
                NoticeView noticeView = new NoticeView();
                BeanUtils.copyProperties(notice, noticeView);
                return noticeView;
            }

        });
        return JsonResult.success(noticeViewPage);
    }

    @Override
    public JsonResult save(NoticeParam noticeParam) {
        Notice notice = null;
        LocalDateTime now = LocalDateTime.now();
        if(noticeParam.getId() == null || noticeParam.getId() <= 0){
            notice = new Notice();
            BeanUtils.copyProperties(noticeParam, notice);
            notice.setCreateTime(now);
            notice.setUpdateTime(now);
            notice.setTarget(1);
            noticeMapper.insert(notice);
            return JsonResult.success("新增成功",null);
        }else{
            //更新
            //根据id查询Notice信息
            notice = noticeMapper.selectById(noticeParam.getId());
            if(notice == null){
                return JsonResult.fail("公告不存在，更新失败");
            }
            //要更新的字段
            notice.setContent(noticeParam.getContent());
            notice.setTitle(noticeParam.getTitle());
            notice.setUpdateTime(now);
            notice.setTarget(1);
            int rows = noticeMapper.updateById(notice);
            //要判断更新的影响行数
            if(rows > 0){
                return JsonResult.success("更新成功",null);
            }else{
                return JsonResult.fail("公告不存在，更新失败");
            }
        }
    }

    @Override
    public JsonResult deleteById(Long id) {
        int relatedRows = messageMapper.deleteByNoticeId(id);
        int rows = noticeMapper.deleteById(id);
        if(rows > 0){
            return JsonResult.success("删除成功");
        }else{
            return JsonResult.fail("删除失败");
        }
    }

    @Override
    public JsonResult send(Integer noticeId) {
        // 1. 获取所有用户的 user_id
        Notice notice = noticeMapper.selectById(noticeId);
        notice.setUpdateTime(LocalDateTime.now());
        noticeMapper.updateById(notice);
        List<Long> userIds = userMapper.getAllUserIds(); // 需要有一个方法可以获取所有用户的ID
        // 2. 遍历用户列表并插入发布公告记录
        for (Long userId : userIds) {
            // 创建一个新的发布公告记录
            Message record = new Message();
            record.setUserId(userId);
            record.setNoticeId(noticeId);
            record.setIsRead(0); // 设置未读状态
            record.setNotified(0); // 设置未通知状态

            // 插入记录到数据库中
            messageMapper.insert(record);
        }
        // 3. 返回操作结果
        return JsonResult.success("公告已成功发布给所有用户！");
    }

}
