package com.huantu.dto.response;

/**
 * 帖子返回对象
 */
public class PostVO {

    private Long id;
    private Long authorId;
    private String authorNickname;
    private String authorAvatar;
    private String title;
    private String content;
    private String images;
    private String videoUrl;
    private String city;
    private String cityCode;
    private String locationTag;
    private Integer isLocalAuth;
    private Long scenicId;
    private String scenicName;
    private Integer status;
    private Integer likeCount;
    private Integer collectCount;
    private Integer usefulCount;
    private String createdAt;

    // 当前用户是否已点赞/收藏
    private Boolean liked;
    private Boolean collected;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public String getAuthorNickname() { return authorNickname; }
    public void setAuthorNickname(String authorNickname) { this.authorNickname = authorNickname; }

    public String getAuthorAvatar() { return authorAvatar; }
    public void setAuthorAvatar(String authorAvatar) { this.authorAvatar = authorAvatar; }

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

    public String getScenicName() { return scenicName; }
    public void setScenicName(String scenicName) { this.scenicName = scenicName; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }

    public Integer getCollectCount() { return collectCount; }
    public void setCollectCount(Integer collectCount) { this.collectCount = collectCount; }

    public Integer getUsefulCount() { return usefulCount; }
    public void setUsefulCount(Integer usefulCount) { this.usefulCount = usefulCount; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public Boolean getLiked() { return liked; }
    public void setLiked(Boolean liked) { this.liked = liked; }

    public Boolean getCollected() { return collected; }
    public void setCollected(Boolean collected) { this.collected = collected; }
}
