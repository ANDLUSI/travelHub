package com.tjetc.service;

import com.tjetc.common.JsonResult;
import com.tjetc.entity.Plan;

public interface PlanService {
    JsonResult find(Long userId);

    JsonResult addPlan(Plan plan);

    JsonResult delete(Long id);
}
