package com.tjetc.controller;

import com.tjetc.common.JsonResult;
import com.tjetc.common.admin.vo.param.AdminParam;
import com.tjetc.common.scenic.vo.param.ScenicParam;
import com.tjetc.service.ScenicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("scenic")
public class ScenicController {
    //private static final Logger log = LoggerFactory.getLogger(ScenicController.class);
    @Value("${file.basePath}")
    private String fileBasePath;

    @Autowired
    private ScenicService scenicService;

    @RequestMapping("page")
    public JsonResult page(@RequestParam(value="spotName",required = false,defaultValue = "")String spotName,
                           @RequestParam(value="pageNo",required = false,defaultValue = "1")Integer pageNo,
                           @RequestParam(value="pageSize",required = false,defaultValue = "2")Integer pageSize) {
        return scenicService.findPage(spotName, pageNo, pageSize);
    }

    @RequestMapping("pageTime")
    public JsonResult pageTime(@RequestParam(value="spotName",required = false,defaultValue = "")String spotName,
                           @RequestParam(value="pageNo",required = false,defaultValue = "1")Integer pageNo,
                           @RequestParam(value="pageSize",required = false,defaultValue = "2")Integer pageSize,
                           @RequestParam(value="province",required = false,defaultValue = "")String province,
                           @RequestParam(value="starSelect",required = false,defaultValue = "")String starSelect,
                           @RequestParam(value="isFeatured",required = false,defaultValue = "")String isFeatured,
                           @RequestParam(value="sortOrder",required = false,defaultValue = "")String sortOrder) {
        return scenicService.findPageTime(spotName, pageNo, pageSize, province, starSelect, isFeatured,sortOrder);
    }

    @RequestMapping("pageAll")
    public JsonResult pageAll(@RequestParam(value="spotName",required = false,defaultValue = "")String spotName,
                              @RequestParam(value="pageNo",required = false,defaultValue = "1")Integer pageNo,
                              @RequestParam(value="pageSize",required = false,defaultValue = "2")Integer pageSize,
                              @RequestParam(value="province",required = false,defaultValue = "")String province,
                              @RequestParam(value="isFeatured",required = false,defaultValue = "")String isFeatured,
                              @RequestParam(value="starSelect",required = false,defaultValue = "")String starSelect) {
        return scenicService.findAll(spotName, pageNo, pageSize, province,starSelect,isFeatured);
    }

    @RequestMapping("delete/{id}")
    public JsonResult delete(@PathVariable("id") Long id) {
        return scenicService.deleteById(id);
    }

    @RequestMapping("detail/{id}")
    public JsonResult detail(@PathVariable("id") Long id) {
        return scenicService.findById(id);
    }

    @RequestMapping("saveScenic")
    public JsonResult save(@RequestBody ScenicParam scenicParam) {
        return scenicService.saveScenic(scenicParam);
    }

    @RequestMapping("editFeatured/{id}/{type}")
    public JsonResult editTime(@PathVariable("id") Long id, @PathVariable("type") int type){
        return scenicService.editFeatured(id,type);
    }

}
