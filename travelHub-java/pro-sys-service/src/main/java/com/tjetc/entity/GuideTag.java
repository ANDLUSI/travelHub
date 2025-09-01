package com.tjetc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//配置实体类对应表名称
@TableName("guide_tag")
public class GuideTag {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    private int people;
    private int fee;
    private int day;
    private int month;
    private Long guideId;
}
