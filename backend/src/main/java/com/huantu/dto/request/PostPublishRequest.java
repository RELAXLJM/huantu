package com.huantu.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 发帖请求
 */
public class PostPublishRequest {

    @NotBlank(message = "内容不能为空")
    @Size(max = 2000, message = "内容最长2000字")
    private String content;

    private String title;

    private String images;          // 图片URL列表，逗号分隔

    private String city;

    private String cityCode;

    private String locationTag;     // 位置标签

    private Integer isLocalAuth;    // 是否本地人认证

    private Long scenicId;          // 关联景点ID

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }

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
}
