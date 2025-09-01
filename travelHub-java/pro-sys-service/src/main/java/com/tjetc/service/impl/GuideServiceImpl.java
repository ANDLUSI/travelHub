package com.tjetc.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tjetc.common.JsonResult;
import com.tjetc.common.guide.vo.data.GuideView;
import com.tjetc.common.guide.vo.param.GuideParam;
import com.tjetc.dao.*;
import com.tjetc.entity.*;
import com.tjetc.service.GuideService;
import com.tjetc.utils.ChatGLMRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
public class GuideServiceImpl implements GuideService {
    @Value("${token.expiration}")
    private int tokenExpiration;
    @Autowired
    private GuideMapper guideMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private GuideLikeMapper guideLikeMapper;
    @Autowired
    private NoticeMapper noticeMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private GuideTagMapper guideTagMapper;
    @Autowired
    private ViewMapper viewMapper;
    @Autowired
    private ChatGLMRequest chatGLMRequest;

    @Override
    public JsonResult findPage(Integer pageNo, Integer pageSize, Integer isFeatured, Integer status, String title, String sortOrder) {
        //设置页码和每页数量
        Page<Guide> page = Page.of(pageNo,pageSize);
        //把page作为分页参数，传入方法中
        Page<Guide> guidePage = guideMapper.findPageAll(page,isFeatured,status,title,sortOrder);
        //convert方法转换Page中records集合元素类型Admin---->AdminView
        //new Function<Admin,AdminView>(){};表示 创建一个 是Function接口的匿名类 对象
        IPage<GuideView> guideViewPage = guidePage.convert(new Function<Guide, GuideView>() {
            @Override
            public GuideView apply(Guide guide) {
                GuideView guideView = new GuideView();
                BeanUtils.copyProperties(guide,guideView);
                return guideView;
            }

        });
        return JsonResult.success(guideViewPage);
    }

    @Override
    public JsonResult findUserPage(Integer pageNo, Integer pageSize, Long userId ,Integer status, String title, String sortOrder) {
        //设置页码和每页数量
        Page<Guide> page = Page.of(pageNo,pageSize);
        //把page作为分页参数，传入方法中
        Page<Guide> guidePage = guideMapper.findUserPageAll(page,userId,status,title,sortOrder);
        //convert方法转换Page中records集合元素类型Admin---->AdminView
        //new Function<Admin,AdminView>(){};表示 创建一个 是Function接口的匿名类 对象
        IPage<GuideView> guideViewPage = guidePage.convert(new Function<Guide, GuideView>() {
            @Override
            public GuideView apply(Guide guide) {
                GuideView guideView = new GuideView();
                BeanUtils.copyProperties(guide,guideView);
                return guideView;
            }

        });
        return JsonResult.success(guideViewPage);
    }

    @Override
    public JsonResult editStatus(Long id, int status) {
        Guide guide = guideMapper.selectById(id);
        if (guide == null) {
            return JsonResult.fail("攻略不存在");
        }
        guide.setStatus(status);

        if(status == -1){
            Notice notice =new Notice();
            notice.setContent("您的攻略审核没通过！请重新申请！");
            notice.setCreateTime(LocalDateTime.now());
            notice.setUpdateTime(LocalDateTime.now());
            notice.setTitle("审核结果提醒");
            notice.setTarget(0);
            noticeMapper.insert(notice);

            Message message = new Message();
            message.setNotified(0);
            message.setIsRead(0);
            message.setUserId(guide.getAuthorId());
            message.setNoticeId(Math.toIntExact(notice.getId()));
            messageMapper.insert(message);
        }
        else if(status==1){
            Notice notice =new Notice();
            notice.setContent("您的攻略审核已通过！快去看看吧！");
            notice.setCreateTime(LocalDateTime.now());
            notice.setUpdateTime(LocalDateTime.now());
            notice.setTitle("审核结果提醒");
            noticeMapper.insert(notice);

            Message message = new Message();
            message.setNotified(0);
            message.setIsRead(0);
            message.setUserId(guide.getAuthorId());
            message.setNoticeId(Math.toIntExact(notice.getId()));
            messageMapper.insert(message);
        }

        int result = guideMapper.updateById(guide);
        if (result > 0) {
            return JsonResult.success("修改成功",null);
        } else {
            return JsonResult.fail("Failed to update role");
        }
    }

    @Override
    public JsonResult editFeatured(Long id, int featured) {
        Guide guide = guideMapper.selectById(id);
        if (guide == null) {
            return JsonResult.fail("攻略不存在");
        }
        guide.setIsFeatured(featured);

        int result = guideMapper.updateById(guide);
        if (result > 0) {
            return JsonResult.success("修改成功",null);
        } else {
            return JsonResult.fail("Failed to update role");
        }
    }

    @Override
    public JsonResult findById(Long id) {
        // 1. 从 guide 表中查询
        Guide guide = guideMapper.selectById(id);
        // 2. 从 user 表中查询
        User user = userMapper.selectById(guide.getAuthorId());

        // 3. 将数据赋值给 GuideView
        GuideView guideView = new GuideView();
        if (guide != null) {
            BeanUtils.copyProperties(guide, guideView);
        }
        if (user != null) {
            guideView.setAuthorPath(user.getImagePath());
            guideView.setAuthorLocation(user.getLocation());
            guideView.setLastlogTime(user.getLastlogTime());
            guideView.setAuthorName(user.getUsername());
            guideView.setAuthorTitle(user.getTitle());
        }
        return JsonResult.success(guideView);
    }

    @Override
    public JsonResult findPageAll(Integer pageNo, Integer pageSize, Integer day, Integer month, Integer fee, Integer people, String title, Integer sortOrder,String tag) {
        //设置页码和每页数量
        Page<GuideView> page = Page.of(pageNo,pageSize);
        //把page作为分页参数，传入方法中
        Page<GuideView> guideViewPage = guideMapper.findPageSelect(page,day,month,fee,people,title,sortOrder,tag);
        return JsonResult.success(guideViewPage);
    }

    @Override
    public JsonResult judge(Long userId, Long guideId) {
        GuideLike guideLike = guideMapper.selectByTwoId(userId,guideId);
        if (guideLike == null) {
            GuideLike guideLike1 = new GuideLike();
            guideLike1.setUserId(userId);
            guideLike1.setGuideId(guideId);
            guideLike1.setCreateTime(LocalDateTime.now());
            guideLikeMapper.insert(guideLike1);
            Guide guide =guideMapper.selectById(guideId);
            guide.setLikeCount(guide.getLikeCount()+1);
            guideMapper.updateById(guide);


            Guide guide1 = guideMapper.selectById(guideId);
            String title = guide1.getTitle();

            //新建通知，设置通知
            User user = userMapper.selectById(userId);

            if (userId == guide1.getAuthorId()){
                return JsonResult.success(guideLike);
            }
            else {
                String username = user.getUsername();
                Notice notice = new Notice();
                notice.setContent(username + "点赞了你的\"" + title + "\"攻略");
                notice.setCreateTime(LocalDateTime.now());
                notice.setUpdateTime(LocalDateTime.now());
                notice.setTitle("点赞提醒");
                notice.setTarget(0);
                noticeMapper.insert(notice);

                //获取作者的id，并将通告发给作者
                Long id = guide1.getAuthorId();
                Message message = new Message();
                message.setNotified(0);
                message.setIsRead(0);
                message.setUserId(id);
                message.setNoticeId(Math.toIntExact(notice.getId()));
                messageMapper.insert(message);
            }
        }
        else{
            return JsonResult.success(guideLike);
        }
        return JsonResult.success(guideLike);
    }

    @Override
    public JsonResult saveTravel(GuideParam guideParam) {
        Guide guide = new Guide();
        LocalDateTime now = LocalDateTime.now();
        BeanUtils.copyProperties(guideParam,guide);
        guide.setCreateTime(now);
        guide.setUpdateTime(now);
        guide.setStatus(0);
        guide.setIsFeatured(0);
        guide.setCommentCount(0);
        guide.setLikeCount(0);
        guide.setViewCount(0);
        guideMapper.insert(guide);

        GuideTag guideTag = new GuideTag();
        Long id = guide.getId();
        guideTag.setGuideId(guide.getId());
        guideTag.setFee(guideParam.getFee());
        guideTag.setDay(guideParam.getDay());
        guideTag.setPeople(guideParam.getPeople());
        guideTag.setMonth(guideParam.getMonth());
        guideTagMapper.insert(guideTag);

        ////调用大模型接口,让大模型审核
        System.out.println("获得的id"+id);
        Guide guide1 = guideMapper.findGuideById(id);
        Boolean res = chatGLMRequest.LinkAi(guide1);

        if (res){
            editStatus(guide1.getId(),1);
        }else {
            editStatus(guide1.getId(),-1);
        }

        return JsonResult.success("新增成功",null);

    }

    @Transactional // 确保所有操作要么全部成功，要么全部回滚
    public JsonResult deleteById(Long id) {
        int rows = guideMapper.deleteById(id);
        if(rows > 0){
            return JsonResult.success("删除成功");
        }else{
            return JsonResult.fail("删除失败");
        }

    }

    @Override
    public JsonResult findHotGuide() {
        List<GuideView> list = guideMapper.selectByHot();
        if (list.size() > 0) {
            return JsonResult.success(list);
        }else {
            return JsonResult.fail("暂无热门信息");
        }

    }

    @Override
    public JsonResult findHotTag() {
        List<GuideView> list = guideMapper.selectByTag();
        if (list.size() > 0) {
            return JsonResult.success(list);
        }else {
            return JsonResult.fail("暂无热门信息");
        }
    }

    @Override
    public JsonResult addView(Long userId, Long guideId) {
        View view = viewMapper.selectPanChong(userId,guideId);
        LocalDateTime now = LocalDateTime.now();
        if (view != null && view.getLastviewTime() != null) {
            // 计算上次浏览时间和当前时间的差值
            long daysBetween = ChronoUnit.DAYS.between(view.getLastviewTime(), now);

            // 如果上次浏览时间距离当前时间少于1天，返回无需增加 viewCount
            if (daysBetween < 1) {
                return JsonResult.success("无需增加viewCount，距离上次浏览时间不足一天");
            }
        }
        if(view == null){
            view = new View();
            view.setUserId(userId);
            view.setGuideId(guideId);
            view.setLastviewTime(now);
            viewMapper.insert(view);
            Guide guide = guideMapper.selectById(guideId);
            guide.setViewCount(guide.getViewCount()+1);
            guideMapper.updateById(guide);
            return JsonResult.success("viewCount 增加成功");
        }
        else{
            view.setLastviewTime(now);
            viewMapper.updateById(view);
            Guide guide = guideMapper.selectById(guideId);
            guide.setViewCount(guide.getViewCount()+1);
            guideMapper.updateById(guide);
            return JsonResult.success("viewCount 增加成功");
        }

    }

    @Override
    public JsonResult getGuideInfo() {
        List<Guide> guides = guideMapper.listMaps();
        return JsonResult.success(guides);
    }
}
