package com.tjetc.controller;

import com.tjetc.common.JsonResult;
import com.tjetc.service.ScenicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("scenic")
public class ScenicController {
    @Value("${file.basePath}")
    private String fileBasePath;

    @Autowired
    private ScenicService scenicService;

    @RequestMapping("search")
    public JsonResult search(@RequestParam(value="spotName",required = false,defaultValue = "")String spotName) {
        return scenicService.findName(spotName);
    }

    @RequestMapping("hot")
    public JsonResult selectHot() {
        return scenicService.findHot();
    }
    @RequestMapping("near")
    public JsonResult selectNear(@RequestParam("location") String location) {
        return scenicService.findNear(location);
    }

    @RequestMapping("detail/{id}")
    public JsonResult selectNear(@PathVariable("id") Long id) {
        return scenicService.findById(id);
    }

    @RequestMapping("select")
    public JsonResult select(@RequestParam("tag") String tags) {
        return scenicService.findByTag(tags);
    }
}
