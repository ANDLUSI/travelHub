package com.tjetc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
//配置实体类对应表名称
@TableName("notice")
public class Notice {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
    private int target;
}
