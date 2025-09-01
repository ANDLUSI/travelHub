package com.tjetc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tjetc.entity.GuideLike;
import com.tjetc.entity.View;
import org.apache.ibatis.annotations.Param;

public interface ViewMapper extends BaseMapper<View> {

    View selectPanChong(@Param("userId")Long userId, @Param("guideId")Long guideId);
}
