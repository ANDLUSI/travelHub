package com.tjetc.controller.message;

import com.tjetc.common.JsonResult;
import com.tjetc.common.admin.vo.param.AdminParam;
import com.tjetc.common.notice.vo.param.NoticeParam;
import com.tjetc.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("notice")
public class NoticeController {
    @Value("${file.basePath}")
    private String fileBasePath;

    @Autowired
    private NoticeService noticeService;

    @RequestMapping("page")
    public JsonResult page(@RequestParam(value="pageNo",required = false,defaultValue = "1")Integer pageNo,
                           @RequestParam(value="pageSize",required = false,defaultValue = "2")Integer pageSize) {
        return noticeService.findPage(pageNo, pageSize);
    }
    @RequestMapping("delete/{id}")
    public JsonResult delete(@PathVariable("id") Long id) {

        return noticeService.deleteById(id);
    }

    @RequestMapping("save")
    public JsonResult save(@RequestBody NoticeParam noticeParam) {
        return noticeService.save(noticeParam);
    }
    @RequestMapping("send/{id}")
    public JsonResult sendM(@PathVariable("id")Integer id) {
        return noticeService.send(id);
    }

}
