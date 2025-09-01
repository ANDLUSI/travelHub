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
@TableName("view")
public class View {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("guide_id")
    private Long guideId;
    private LocalDateTime lastviewTime;
}
