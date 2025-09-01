package com.tjetc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
//配置实体类对应表名称
@TableName("scenic")
public class Scenic {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    @TableField(value = "spot_name")
    private String spotName;
    private String location;
    private String description;
    @TableField(value = "opening_hours")
    private String openingHours;
    @TableField(value = "ticket_price")
    private Double ticketPrice;
    private String province;
    private int stars;
    @TableField(value = "is_featured")
    private int featured;
    private String contact;
    @TableField(value = "image_path")
    private String imagePath;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;

    private String tags;
    private String www;
}