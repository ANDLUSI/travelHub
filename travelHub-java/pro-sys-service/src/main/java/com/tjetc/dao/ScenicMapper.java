package com.tjetc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tjetc.common.guide.vo.data.GuideView;
import com.tjetc.common.scenic.vo.data.ScenicView;
import com.tjetc.entity.Scenic;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ScenicMapper extends BaseMapper<Scenic> {
    Page<Scenic> selectPageLikeSpotName(Page<Scenic> pageScenic, @Param("spotName") String spotName);

    Page<Scenic> selectPageLikeSpotNameAndTime(Page<Scenic> pageScenic, @Param("spotName") String spotName,@Param("province") String province,
                                               @Param("starSelect")String starSelect,@Param("isFeatured") String isFeatured,@Param("sortOrder") String sortOrder);
    Page<Scenic> selectPageAll(Page<Scenic> pageScenic, @Param("spotName") String spotName,@Param("province") String province,
                               @Param("starSelect")String starSelect,@Param("isFeatured") String isFeatured);
    Scenic selectBySpotName(String spotName);

    List<ScenicView> selectByName(String spotName);

    List<ScenicView> selectByHot();

    List<ScenicView> selectByNear(String location);

    List<ScenicView> findByTag(String tags);
}
