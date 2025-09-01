package com.tjetc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("message_read")
public class Message {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Integer noticeId;
    private Integer isRead;
    private Integer notified;

    private String content;
    private String title;
    private LocalDateTime createTime;
}
