package com.tjetc.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tjetc.common.JsonResult;
import com.tjetc.common.guide.vo.data.GuideView;
import com.tjetc.common.scenic.vo.data.ScenicView;
import com.tjetc.common.scenic.vo.param.ScenicParam;
import com.tjetc.dao.ScenicMapper;
import com.tjetc.entity.Admin;
import com.tjetc.entity.Scenic;
import com.tjetc.service.ScenicService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Service
public class ScenicServiceImpl implements ScenicService {
    @Value("${token.expiration}")
    private int tokenExpiration;
    @Autowired
    private ScenicMapper scenicMapper;
    @Override
    public JsonResult findPage(String spotName, Integer pageNo, Integer pageSize) {
        //设置页码和每页数量
        Page<Scenic> page = Page.of(pageNo,pageSize);
        //把page作为分页参数，传入方法中
        Page<Scenic> scenicPage = scenicMapper.selectPageLikeSpotName(page,spotName);
        //convert方法转换Page中records集合元素类型Admin---->AdminView
        //new Function<Admin,AdminView>(){};表示 创建一个 是Function接口的匿名类 对象
        IPage<ScenicView> scenicViewPage = scenicPage.convert(new Function<Scenic, ScenicView>() {
            @Override
            public ScenicView apply(Scenic scenic) {
                ScenicView scenicView = new ScenicView();
                BeanUtils.copyProperties(scenic,scenicView);
                return scenicView;
            }

        });
        return JsonResult.success(scenicViewPage);
    }

    @Override
    public JsonResult findPageTime(String spotName, Integer pageNo, Integer pageSize,String province, String starSelect, String isFeatured, String sortOrder) {
        //设置页码和每页数量
        Page<Scenic> page = Page.of(pageNo,pageSize);
        //把page作为分页参数，传入方法中
        Page<Scenic> scenicPage = scenicMapper.selectPageLikeSpotNameAndTime(page,spotName,province,starSelect,isFeatured,sortOrder);
        //convert方法转换Page中records集合元素类型Admin---->AdminView
        //new Function<Admin,AdminView>(){};表示 创建一个 是Function接口的匿名类 对象
        IPage<ScenicView> scenicViewPage = scenicPage.convert(new Function<Scenic, ScenicView>() {
            @Override
            public ScenicView apply(Scenic scenic) {
                ScenicView scenicView = new ScenicView();
                BeanUtils.copyProperties(scenic,scenicView);
                return scenicView;
            }

        });
        return JsonResult.success(scenicViewPage);
    }

    @Override
    public JsonResult findAll(String spotName, Integer pageNo, Integer pageSize, String province, String starSelect, String isFeatured) {
        //设置页码和每页数量
        Page<Scenic> page = Page.of(pageNo,pageSize);
        //把page作为分页参数，传入方法中
        Page<Scenic> scenicPage = scenicMapper.selectPageAll(page,spotName,province,starSelect,isFeatured);
        //convert方法转换Page中records集合元素类型Admin---->AdminView
        //new Function<Admin,AdminView>(){};表示 创建一个 是Function接口的匿名类 对象
        IPage<ScenicView> scenicViewPage = scenicPage.convert(new Function<Scenic, ScenicView>() {
            @Override
            public ScenicView apply(Scenic scenic) {
                ScenicView scenicView = new ScenicView();
                BeanUtils.copyProperties(scenic,scenicView);
                return scenicView;
            }

        });
        return JsonResult.success(scenicViewPage);
    }

    @Override
    public JsonResult deleteById(Long id) {
        int rows = scenicMapper.deleteById(id);
        if(rows > 0){
            return JsonResult.success("删除成功");
        }else{
            return JsonResult.fail("删除失败");
        }
    }

    @Override
    public JsonResult findById(Long id) {
        //selectById是mybatisplus提供的单表查询操作，生成sql语句都是mybatisplus内置了
        Scenic scenic = scenicMapper.selectById(id);

        ScenicView scenicView = new ScenicView();
        BeanUtils.copyProperties(scenic, scenicView);
        return JsonResult.success(scenicView);
    }

    @Transactional
    @Override
    public JsonResult saveScenic(ScenicParam scenicParam) {
        //通过判断id值有无判断是新增更新，id值无是新增，id值有就是更新
        Scenic scenic = null;
        LocalDateTime now = LocalDateTime.now();
        if(scenicParam.getId() == null || scenicParam.getId() <= 0){
            //新增
            Scenic existsScenic = scenicMapper.selectBySpotName(scenicParam.getSpotName());
            if(existsScenic != null){
                return JsonResult.fail("景点已经存在");
            }

            scenic = new Scenic();
            BeanUtils.copyProperties(scenicParam,scenic);
            scenic.setCreateTime(now);
            scenic.setUpdateTime(now);
            scenicMapper.insert(scenic);
            return JsonResult.success("新增成功",null);
        }else{
            //更新
            //根据id查询Admin信息
            scenic = scenicMapper.selectById(scenicParam.getId());
            if(scenic == null){

                return JsonResult.fail("用户不存在，更新失败");
            }
            //要更新的字段
            scenic.setImagePath(scenicParam.getImagePath());
            scenic.setLocation(scenicParam.getLocation());
            scenic.setContact(scenicParam.getContact());
            scenic.setDescription(scenicParam.getDescription());
            scenic.setProvince(scenicParam.getProvince());
            scenic.setFeatured(scenicParam.getFeatured());
            scenic.setTags(scenicParam.getTags());
            scenic.setStars(scenicParam.getStars());
            scenic.setTicketPrice(scenicParam.getTicketPrice());
            scenic.setOpeningHours(scenicParam.getOpeningHours());
            scenic.setUpdateTime(now);
            int rows = scenicMapper.updateById(scenic);
            //要判断更新的影响行数
            if(rows > 0){
                return JsonResult.success("更新成功",null);
            }else{
                return JsonResult.fail("景点不存在，更新失败");
            }
        }
    }

    @Override
    public JsonResult editFeatured(Long id, int isFeatured) {
        Scenic scenic = scenicMapper.selectById(id);
        if (scenic == null) {
            return JsonResult.fail("用户不存在");
        }
        scenic.setFeatured(isFeatured);
        int result = scenicMapper.updateById(scenic);
        if (result > 0) {
            return JsonResult.success("修改成功",null);
        } else {
            return JsonResult.fail("Failed to update role");
        }
    }

    @Override
    public JsonResult findName(String spotName) {
        List<ScenicView> scenic = scenicMapper.selectByName(spotName);
        return JsonResult.success(scenic);
    }

    @Override
    public JsonResult findHot() {
        List<ScenicView> list = scenicMapper.selectByHot();
        if (list.size() > 0) {
            return JsonResult.success(list);
        }else {
            return JsonResult.success("暂无热门信息");
        }
    }

    @Override
    public JsonResult findNear(String location) {
        List<ScenicView> list = scenicMapper.selectByNear(location);
        if (list.size() > 0) {
            return JsonResult.success(list);
        }else {
            return JsonResult.success("暂无热门信息");
        }
    }

    @Override
    public JsonResult findByTag(String tags) {
        List<ScenicView> lists = scenicMapper.findByTag(tags);
        if (lists.size() > 0) {
            return JsonResult.success(lists);
        }
        else{
            return JsonResult.success("暂无景点数据");
        }
    }
}
