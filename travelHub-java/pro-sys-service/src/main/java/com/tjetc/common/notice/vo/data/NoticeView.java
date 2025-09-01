package com.tjetc.common.notice.vo.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeView {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
    private int target;
}
