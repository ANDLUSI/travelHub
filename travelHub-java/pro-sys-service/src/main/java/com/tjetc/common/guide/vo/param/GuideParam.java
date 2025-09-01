package com.tjetc.common.guide.vo.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GuideParam {
    private Long id;
    private String title;
    private String content;
    private Long authorId;

    private int viewCount;
    private int likeCount;
    private int commentCount;

    private int isFeatured;
    private int status;
    private String tags;
    private String imagePath;

    private int people;
    private int fee;
    private int day;
    private int month;
}
