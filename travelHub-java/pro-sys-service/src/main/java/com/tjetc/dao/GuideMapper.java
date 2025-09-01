package com.tjetc.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tjetc.common.guide.vo.data.GuideView;
import com.tjetc.entity.Guide;
import com.tjetc.entity.GuideLike;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GuideMapper extends BaseMapper<Guide> {
    Page<Guide> findPageAll(Page<Guide> pageGuide, @Param("isFeatured") Integer isFeatured, @Param("status")  Integer status,
                         @Param("title")String title, @Param("sortOrder")  String sortOrder);
    Page<Guide> findUserPageAll(Page<Guide> pageGuide,@Param("userId")  Long userId, @Param("status")  Integer status,
                            @Param("title")String title, @Param("sortOrder")  String sortOrder);
    Page<GuideView> findPageSelect(Page<GuideView> pageGuide, @Param("day") Integer day, @Param("month")  Integer month,
                                   @Param("fee")  Integer fee, @Param("people")  Integer people,
                                   @Param("title")String title, @Param("sortOrder")  Integer sortOrder,
                                   @Param("tag")  String tags);
    GuideLike selectByTwoId(@Param("userId")Long userId, @Param("guideId")Long guideId);

    List<GuideView> selectByHot();

    Integer insertGuide(Guide guide);
    Guide findGuideById(@Param("id") Long id);

    List<GuideView> selectByTag();

    List<Guide> listMaps();
}
