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
@TableName("guide")
public class Guide {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    @TableField(value = "author_id")
    private Long authorId;


    @TableField(value = "view_count")
    private int viewCount;
    @TableField(value = "like_count")
    private int likeCount;
    @TableField(value = "comment_count")
    private int commentCount;

    @TableField(value = "is_featured")
    private int isFeatured;
    private int status;
    private String tags;
    @TableField(value = "image_path")
    private String imagePath;

    //private Integer day;
    //private Integer month;
    //private Integer fee;
    //private Integer people;
    //
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
