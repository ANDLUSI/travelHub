package com.tjetc.controller;

import com.tjetc.common.JsonResult;
import com.tjetc.dao.PlanMapper;
import com.tjetc.entity.Plan;
import com.tjetc.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("plan")
public class PlanController {
    @Autowired
    private PlanService planService;

    @RequestMapping("find/{userId}")
    public JsonResult find(@PathVariable("userId") Long userId){
        return planService.find(userId);
    }

    @RequestMapping("add")
    public JsonResult find(@RequestBody Plan plan){
        return planService.addPlan(plan);
    }
    @RequestMapping("delete/{id}")
    public JsonResult delete(@PathVariable("id") Long id){
        return planService.delete(id);
    }

}
