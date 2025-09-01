package com.tjetc.common.comment.vo.param;

import lombok.Data;

@Data
public class CommentParam {
    private Long id;
    private String content;

    private Long guideId;
    private Long userId;
    private Long likeCount;
    private Long parentId;
    private Long applyCount;

    private int isDelete;
    private Long root;

    private String guideTitle;
    private String username;
    private String replyToUser;
}
