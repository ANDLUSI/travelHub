package com.tjetc.common.notice.vo.param;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeParam {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
    private int target;
}
