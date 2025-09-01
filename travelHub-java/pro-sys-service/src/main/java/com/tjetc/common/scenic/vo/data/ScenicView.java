package com.tjetc.common.scenic.vo.data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ScenicView {
    private Long id;
    private String spotName;
    private String location;
    private String description;
    private String openingHours;
    private Double ticketPrice;
    private String province;
    private int stars;
    private int featured;
    private String contact;
    private String imagePath;
    private String www;

    private String tags;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
