package com.tjetc.common.message.vo.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageView {
    private Long id;
    private Long userId;
    private Integer noticeId;
    private Integer isRead;
    private Integer notified;

    private String content;
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
