package com.huantu.entity;

import java.time.LocalDateTime;

/**
 * 路线-景点关联实体
 */
public class RouteScenic {

    private Long id;
    private Long routeId;
    private Long scenicId;
    private Integer dayNumber;      // 第几天
    private Integer sortOrder;      // 当天排序
    private Integer stayDuration;   // 停留时长（分钟）
    private String transportTip;    // 交通建议
    private String memo;            // 用户备注
    private LocalDateTime createdAt;

    // ==================== Getter / Setter ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getScenicId() {
        return scenicId;
    }

    public void setScenicId(Long scenicId) {
        this.scenicId = scenicId;
    }

    public Integer getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(Integer dayNumber) {
        this.dayNumber = dayNumber;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getStayDuration() {
        return stayDuration;
    }

    public void setStayDuration(Integer stayDuration) {
        this.stayDuration = stayDuration;
    }

    public String getTransportTip() {
        return transportTip;
    }

    public void setTransportTip(String transportTip) {
        this.transportTip = transportTip;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
