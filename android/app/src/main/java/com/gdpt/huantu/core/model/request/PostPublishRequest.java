package com.gdpt.huantu.core.model.request;

import com.google.gson.annotations.SerializedName;

/**
 * 发帖请求
 */
public class PostPublishRequest {

    @SerializedName("content")
    private String content;

    @SerializedName("title")
    private String title;

    @SerializedName("images")
    private String images;

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

    public PostPublishRequest() {
    }

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

    public int getIsLocalAuth() { return isLocalAuth; }
    public void setIsLocalAuth(int isLocalAuth) { this.isLocalAuth = isLocalAuth; }

    public long getScenicId() { return scenicId; }
    public void setScenicId(long scenicId) { this.scenicId = scenicId; }
}
