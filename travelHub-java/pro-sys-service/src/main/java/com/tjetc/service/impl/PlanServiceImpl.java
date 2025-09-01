package com.tjetc.service.impl;

import com.tjetc.common.JsonResult;
import com.tjetc.dao.PlanMapper;
import com.tjetc.entity.Plan;
import com.tjetc.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlanServiceImpl implements PlanService {
    @Autowired
    private PlanMapper planMapper;

    @Override
    public JsonResult find(Long userId) {
        List<Plan> plans = planMapper.selectByUserId(userId);
        return JsonResult.success(plans);
    }

    @Override
    public JsonResult addPlan(Plan plan) {
        LocalDateTime now = LocalDateTime.now();
        Plan plan1 = new Plan();
        plan1.setUserId(plan.getUserId());
        plan1.setCreateTime(now);
        plan1.setDescription(plan.getDescription());
        plan1.setUpdateTime(now);
        plan1.setDestination(plan.getDestination());
        plan1.setEndDate(plan.getEndDate());
        plan1.setStartDate(plan.getStartDate());
        plan1.setVehicle(plan.getVehicle());
        planMapper.insert(plan1);
        return JsonResult.success(plan1);
    }

    @Override
    public JsonResult delete(Long id) {
        Plan plan = planMapper.selectById(id);
        if (plan != null) {
            planMapper.deleteById(id);
        }
        return JsonResult.success(plan);
    }

}
