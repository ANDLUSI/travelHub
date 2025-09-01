package com.tjetc.controller;

import com.tjetc.common.JsonResult;
import com.tjetc.common.guide.vo.param.GuideParam;
import com.tjetc.common.user.vo.param.UserParam;
import com.tjetc.entity.Guide;
import com.tjetc.service.GuideService;
import com.tjetc.utils.ChatGLMRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("guide")
public class GuideController {
    @Value("${file.basePath}")
    private String fileBasePath;

    @Autowired
    private ChatGLMRequest chatGLMRequest;

    @Autowired
    private GuideService guideService;
    @RequestMapping("UGdetail/{id}")
    public JsonResult detail(@PathVariable("id") Long id){

        return guideService.findById(id);
    }
    //sortOrder: 0-时间  1-浏览量（默认）   2-点赞量
    @RequestMapping("pageSelect")
    public JsonResult pageSelect(
            @RequestParam(value="sortOrder",required = false,defaultValue = "")Integer sortOrder,
            @RequestParam(value="title",required = false,defaultValue = "")String title,
            @RequestParam(value="day",required = false,defaultValue = "")Integer day,
            @RequestParam(value="month",required = false,defaultValue = "")Integer month,
            @RequestParam(value="fee",required = false,defaultValue = "")Integer fee,
            @RequestParam(value="people",required = false,defaultValue = "")Integer people,
            @RequestParam(value="pageNo",required = false,defaultValue = "1")Integer pageNo,
            @RequestParam(value="pageSize",required = false,defaultValue = "2")Integer pageSize,
            @RequestParam(value="tag",required = false,defaultValue = "")String tag) {
        return guideService.findPageAll(pageNo, pageSize,day,month,fee,people,title,sortOrder,tag);
    }
    @RequestMapping("like/{userId}/{guideId}")
    public JsonResult panchong(@PathVariable("userId") Long userId, @PathVariable("guideId") Long guideId) {
        return guideService.judge(userId,guideId);
    }

    @RequestMapping("saveTravel")
    public JsonResult save(@RequestBody GuideParam guideParam) {
        System.out.println("保存方法controller被调用了");
        return guideService.saveTravel(guideParam);
    }

    @RequestMapping("page")
    public JsonResult page(
            @RequestParam(value="userId",required = false,defaultValue = "")Long userId,
            @RequestParam(value="sortOrder",required = false,defaultValue = "")String sortOrder,
            @RequestParam(value="title",required = false,defaultValue = "")String title,
            @RequestParam(value="status",required = false,defaultValue = "")Integer status,
            @RequestParam(value="pageNo",required = false,defaultValue = "1")Integer pageNo,
            @RequestParam(value="pageSize",required = false,defaultValue = "2")Integer pageSize) {
        return guideService.findUserPage(pageNo, pageSize,userId,status,title,sortOrder);

    }

    @RequestMapping("detail/{id}")
    public JsonResult userDetail(@PathVariable("id") Long id) {
        return guideService.findById(id);
    }

    @RequestMapping("delete/{id}")
    public JsonResult delete(@PathVariable("id") Long id) {
        return guideService.deleteById(id);
    }

    @RequestMapping("hot")
    public JsonResult selectHot() {
        return guideService.findHotGuide();
    }

    @RequestMapping("view/{userId}/{guideId}")
    public JsonResult addView(@PathVariable("userId") Long userId, @PathVariable("guideId") Long guideId) {
        return guideService.addView(userId,guideId);
    }
}
