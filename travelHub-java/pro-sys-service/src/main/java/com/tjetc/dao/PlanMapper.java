package com.tjetc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tjetc.entity.Plan;

import java.util.List;

public interface PlanMapper extends BaseMapper<Plan> {
    List<Plan> selectByUserId(Long userId);

    Plan selectByIdAndUserId(Long userId,Long id);
}
