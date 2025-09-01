package com.tjetc.service;

import com.tjetc.common.JsonResult;
import com.tjetc.common.admin.vo.param.AdminParam;
import com.tjetc.common.scenic.vo.param.ScenicParam;

public interface ScenicService {
    JsonResult findPage(String spotName, Integer pageNo, Integer pageSize);

    JsonResult findPageTime(String spotName, Integer pageNo, Integer pageSize,String province, String starSelect, String isFeatured, String sortOrder);

    JsonResult findAll(String spotName, Integer pageNo, Integer pageSize,String province,String starSelect,String isFeatured);

    JsonResult deleteById(Long id);

    JsonResult findById(Long id);

    JsonResult saveScenic(ScenicParam scenicParam);

    JsonResult editFeatured(Long id, int isFeatured);

    JsonResult findName(String spotName);

    JsonResult findHot();

    JsonResult findNear(String location);

    JsonResult findByTag(String tags);
}
