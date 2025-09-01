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
@TableName("comment")
public class Comment {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    private String content;

    @TableField(value = "guide_id")
    private Long guideId;
    @TableField(value = "user_id")
    private Long userId;
    @TableField(value = "like_count")
    private Long likeCount;
    @TableField(value = "parent_id")
    private Long parentId;
    @TableField(value = "apply_count")
    private Long applyCount;
    @TableField(value = "is_delete")
    private int isDelete;
    private Long root;

    private LocalDateTime updateTime;
    private LocalDateTime createdTime;

    private String guideTitle;
    private String username;
    private String replyToUser;

    private String useLocation;
    private String UIPath;
}
