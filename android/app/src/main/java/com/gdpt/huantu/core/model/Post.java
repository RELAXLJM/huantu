package com.gdpt.huantu.core.model;

import com.google.gson.annotations.SerializedName;

/**
 * 帖子模型（对应后端 PostVO）
 */
public class Post {

    @SerializedName("id")
    private long id;

    @SerializedName("authorId")
    private long authorId;

    @SerializedName("authorNickname")
    private String authorNickname;

    @SerializedName("authorAvatar")
    private String authorAvatar;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("images")
    private String images;

    @SerializedName("videoUrl")
    private String videoUrl;

    @SerializedName("city")
    private String city;

    @SerializedName("cityCode")
    private String cityCode;

    @SerializedName("locationTag")
    private String locationTag;

    @SerializedName("isLocalAuth")
    private int isLocalAuth;

    @SerializedName("scenicId")
    private long scenicId;

    @SerializedName("scenicName")
    private String scenicName;

    @SerializedName("status")
    private int status;

    @SerializedName("likeCount")
    private int likeCount;

    @SerializedName("collectCount")
    private int collectCount;

    @SerializedName("usefulCount")
    private int usefulCount;

    @SerializedName("liked")
    private boolean liked;

    @SerializedName("collected")
    private boolean collected;

    @SerializedName("createdAt")
    private String createdAt;

    public Post() {
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getAuthorId() { return authorId; }
    public void setAuthorId(long authorId) { this.authorId = authorId; }

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

    public int getIsLocalAuth() { return isLocalAuth; }
    public void setIsLocalAuth(int isLocalAuth) { this.isLocalAuth = isLocalAuth; }

    public boolean isLocalAuth() { return isLocalAuth == 1; }

    public long getScenicId() { return scenicId; }
    public void setScenicId(long scenicId) { this.scenicId = scenicId; }

    public String getScenicName() { return scenicName; }
    public void setScenicName(String scenicName) { this.scenicName = scenicName; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public int getCollectCount() { return collectCount; }
    public void setCollectCount(int collectCount) { this.collectCount = collectCount; }

    public int getUsefulCount() { return usefulCount; }
    public void setUsefulCount(int usefulCount) { this.usefulCount = usefulCount; }

    public boolean isLiked() { return liked; }
    public void setLiked(boolean liked) { this.liked = liked; }

    public boolean isCollected() { return collected; }
    public void setCollected(boolean collected) { this.collected = collected; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    /**
     * 获取首张图片URL
     */
    public String getFirstImage() {
        if (images == null || images.isEmpty() || "[]".equals(images)) {
            return null;
        }
        try {
            String cleaned = images.replaceAll("[\\[\\]\"]", "");
            String[] urls = cleaned.split(",");
            return urls.length > 0 ? urls[0].trim() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取内容预览（最多3行）
     */
    public String getContentPreview() {
        if (content == null) return "";
        return content.length() > 100 ? content.substring(0, 100) + "..." : content;
    }
}
