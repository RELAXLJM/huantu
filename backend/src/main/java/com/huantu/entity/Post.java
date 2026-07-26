package com.huantu.entity;

import java.time.LocalDateTime;

/**
 * 社区帖子实体
 */
public class Post {

    private Long id;
    private Long authorId;
    private String title;
    private String content;
    private String images;          // JSON数组
    private String videoUrl;
    private String city;
    private String cityCode;
    private String locationTag;
    private Integer isLocalAuth;    // 0否 1是
    private Long scenicId;
    private Integer status;         // 0正常 1审核中 2下架
    private Integer likeCount;
    private Integer collectCount;
    private Integer usefulCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCityCode() { return cityCode; }
    public void setCityCode(String cityCode) { this.cityCode = cityCode; }

    public String getLocationTag() { return locationTag; }
    public void setLocationTag(String locationTag) { this.locationTag = locationTag; }

    public Integer getIsLocalAuth() { return isLocalAuth; }
    public void setIsLocalAuth(Integer isLocalAuth) { this.isLocalAuth = isLocalAuth; }

    public Long getScenicId() { return scenicId; }
    public void setScenicId(Long scenicId) { this.scenicId = scenicId; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }

    public Integer getCollectCount() { return collectCount; }
    public void setCollectCount(Integer collectCount) { this.collectCount = collectCount; }

    public Integer getUsefulCount() { return usefulCount; }
    public void setUsefulCount(Integer usefulCount) { this.usefulCount = usefulCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
