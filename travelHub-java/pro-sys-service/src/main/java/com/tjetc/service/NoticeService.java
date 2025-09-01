package com.tjetc.service;

import com.tjetc.common.JsonResult;
import com.tjetc.common.notice.vo.param.NoticeParam;
import com.tjetc.entity.Notice;

public interface NoticeService {
    public JsonResult findPage(Integer pageNo, Integer pageSize);
    public JsonResult save(NoticeParam noticeParam);
    public JsonResult deleteById(Long id);

    public JsonResult send(Integer id);
}
