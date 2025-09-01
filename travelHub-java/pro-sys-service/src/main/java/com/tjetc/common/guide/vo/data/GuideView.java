package com.tjetc.common.guide.vo.data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GuideView {
    private Long id;
    private String title;
    private String content;
    private int viewCount;
    private int likeCount;
    private int commentCount;

    private int isFeatured;
    private int status;
    private String tags;
    private String imagePath;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    private Long authorId;
    private String authorName;
    private String authorPath;
    private String authorTitle;
    private String authorLocation;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastlogTime;
}
