package com.huantu.entity;

import java.time.LocalDateTime;

/**
 * 帖子互动实体（点赞/收藏/有用）
 */
public class PostInteract {

    private Long id;
    private Long postId;
    private Long userId;
    private Integer type;   // 1点赞 2收藏 3有用
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
