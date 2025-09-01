package com.tjetc.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tjetc.common.JsonResult;
import com.tjetc.entity.User;
import com.tjetc.service.GuideService;
import com.tjetc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("echarts")
public class EchartsController {
    @Value("${file.basePath}")
    private String fileBasePath;

    @Autowired
    private UserService userService;

    @Autowired
    private GuideService guideService;

    @RequestMapping("user")
    public JsonResult findByNewUser() {
        return userService.getNewUserStats();
    }

    @RequestMapping("guide")
    public JsonResult findByHotGuide() {
        return guideService.findHotGuide();
    }

    @RequestMapping("tag")
    public JsonResult findByHotTag() {
        return guideService.findHotTag();
    }


}
