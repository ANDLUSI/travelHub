package com.tjetc.common.comment.vo.data;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentView {
    private Long id;
    private String content;

    private Long guideId;
    private Long userId;
    private Long likeCount;
    private Long parentId;
    private Long applyCount;

    private int isDelete;
    private Long root;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private String guideTitle;
    private String username;
    private String replyToUser;

    private String useLocation;
    private String UIPath;
}
