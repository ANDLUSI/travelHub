package com.tjetc.controller;

import com.tjetc.common.JsonResult;
import com.tjetc.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("message")
public class MessageController {
    @Value("${file.basePath}")
    private String fileBasePath;
    @Autowired
    private MessageService messageService;

    @RequestMapping("{id}")
    public JsonResult detail(@PathVariable("id") Long id) {
        return messageService.findById(id);
    }
    @RequestMapping("fade/{id}")
    public JsonResult fade(@PathVariable("id") Long id) {
        return messageService.fadeById(id);
    }

    @RequestMapping("page")
    public JsonResult page(@RequestParam(value="userId",required = false,defaultValue = "")Long userId,
                           @RequestParam(value="pageNo",required = false,defaultValue = "1")Integer pageNo,
                           @RequestParam(value="pageSize",required = false,defaultValue = "2")Integer pageSize) {
        return messageService.page(userId,pageNo,pageSize);
    }
}
