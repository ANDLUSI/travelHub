package com.tjetc.service;

import com.tjetc.common.JsonResult;
import com.tjetc.common.guide.vo.param.GuideParam;

public interface GuideService {
    JsonResult findPage(Integer pageNo, Integer pageSize,Integer isFeatured,
                        Integer status,String title,String sortOrder);
    JsonResult findUserPage(Integer pageNo, Integer pageSize,Long userId,
                        Integer status,String title,String sortOrder);
    JsonResult editStatus(Long id, int status);
    JsonResult editFeatured(Long id, int featured);

    JsonResult findById(Long id);

    JsonResult findPageAll(Integer pageNo, Integer pageSize,Integer day,Integer month,
                           Integer fee,Integer people,String title,Integer sortOrder,String tag);

    JsonResult judge(Long userId,Long guideId);

    JsonResult saveTravel(GuideParam guideParam);

    JsonResult deleteById(Long id);

    JsonResult findHotGuide();

    JsonResult findHotTag();

    JsonResult addView(Long userId,Long guideId);

    JsonResult getGuideInfo();
}
