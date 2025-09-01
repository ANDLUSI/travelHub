package com.tjetc.controller;

import com.tjetc.common.JsonResult;
import com.tjetc.service.GuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("guide")
public class GuideController {
    @Value("${file.basePath}")
    private String fileBasePath;

    @Autowired
    private GuideService guideService;

    @RequestMapping("pageAll")
    public JsonResult page(
            @RequestParam(value="sortOrder",required = false,defaultValue = "")String sortOrder,
            @RequestParam(value="title",required = false,defaultValue = "")String title,
            @RequestParam(value="status",required = false,defaultValue = "")Integer status,
            @RequestParam(value="isFeatured",required = false,defaultValue = "")Integer isFeatured,
            @RequestParam(value="pageNo",required = false,defaultValue = "1")Integer pageNo,
            @RequestParam(value="pageSize",required = false,defaultValue = "2")Integer pageSize) {
        return guideService.findPage(pageNo, pageSize,isFeatured,status,title,sortOrder);
    }
    @RequestMapping("editStatus/{id}/{type}")
    public JsonResult editStatus(@PathVariable("id") Long id, @PathVariable("type") int type){
        return guideService.editStatus(id,type);
    }
    @RequestMapping("editFeatured/{id}/{type}")
    public JsonResult editFeatured(@PathVariable("id") Long id, @PathVariable("type") int type){
        return guideService.editFeatured(id,type);
    }

    @RequestMapping("detail/{id}")
    public JsonResult detail(@PathVariable("id") Long id) {
        return guideService.findById(id);
    }
    @RequestMapping("findGuide")
    public JsonResult findGuide() {
        return guideService.getGuideInfo();
    }

}
