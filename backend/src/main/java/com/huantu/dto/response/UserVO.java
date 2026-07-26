package com.huantu.dto.response;

/**
 * 用户信息返回对象（脱敏，不包含密码）
 */
public class UserVO {

    private Long id;
    private String nickname;
    private String avatarUrl;
    private String phone;       // 脱敏显示：138****1234
    private String city;
    private String bio;
    private String createdAt;

    // 统计信息
    private int footprintCount;  // 足迹城市数
    private int routeCount;      // 路线数
    private int likeCount;       // 获赞数

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getFootprintCount() {
        return footprintCount;
    }

    public void setFootprintCount(int footprintCount) {
        this.footprintCount = footprintCount;
    }

    public int getRouteCount() {
        return routeCount;
    }

    public void setRouteCount(int routeCount) {
        this.routeCount = routeCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
