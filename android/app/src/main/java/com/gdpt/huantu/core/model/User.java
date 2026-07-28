package com.gdpt.huantu.core.model;

import com.google.gson.annotations.SerializedName;

/**
 * 用户模型（对应后端 UserVO）
 */
public class User {

    @SerializedName("id")
    private long id;

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("avatarUrl")
    private String avatarUrl;

    @SerializedName("phone")
    private String phone;

    @SerializedName("city")
    private String city;

    @SerializedName("bio")
    private String bio;

    @SerializedName("footprintCount")
    private int footprintCount;

    @SerializedName("routeCount")
    private int routeCount;

    @SerializedName("likeCount")
    private int likeCount;

    @SerializedName("createdAt")
    private String createdAt;

    public User() {
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public int getFootprintCount() { return footprintCount; }
    public void setFootprintCount(int footprintCount) { this.footprintCount = footprintCount; }

    public int getRouteCount() { return routeCount; }
    public void setRouteCount(int routeCount) { this.routeCount = routeCount; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
