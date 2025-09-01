package com.tjetc.common.message.vo.param;

import lombok.Data;

@Data
public class MessageParam {
    private Long id;
    private Long userId;
    private Integer noticeId;
    private Integer isRead;
    private Integer notified;
}
