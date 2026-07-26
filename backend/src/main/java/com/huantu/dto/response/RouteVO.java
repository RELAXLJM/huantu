package com.huantu.dto.response;

import java.util.List;

/**
 * 路线返回对象
 */
public class RouteVO {

    private Long id;
    private String title;
    private String coverUrl;
    private String destination;
    private String cityCode;
    private Integer days;
    private Integer totalDuration;    // 总预估时长（分钟）
    private String companionType;
    private Integer budgetMin;
    private Integer budgetMax;
    private String preference;
    private Integer status;           // 0草稿 1已保存 2已结束
    private String createdAt;

    // 时间轴：每天一组景点
    private List<DayPlan> dayPlans;

    /**
     * 单日计划
     */
    public static class DayPlan {
        private Integer dayNumber;
        private List<ScenicItem> scenics;

        public Integer getDayNumber() {
            return dayNumber;
        }

        public void setDayNumber(Integer dayNumber) {
            this.dayNumber = dayNumber;
        }

        public List<ScenicItem> getScenics() {
            return scenics;
        }

        public void setScenics(List<ScenicItem> scenics) {
            this.scenics = scenics;
        }
    }

    /**
     * 时间轴中的景点项
     */
    public static class ScenicItem {
        private Long scenicId;
        private String name;
        private String poiType;
        private String address;
        private Double longitude;
        private Double latitude;
        private Double rating;
        private String images;
        private String priceInfo;
        private Integer stayDuration;    // 分钟
        private String transportTip;    // 交通建议
        private String memo;            // 用户备注

        public Long getScenicId() {
            return scenicId;
        }

        public void setScenicId(Long scenicId) {
            this.scenicId = scenicId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPoiType() {
            return poiType;
        }

        public void setPoiType(String poiType) {
            this.poiType = poiType;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getRating() {
            return rating;
        }

        public void setRating(Double rating) {
            this.rating = rating;
        }

        public String getImages() {
            return images;
        }

        public void setImages(String images) {
            this.images = images;
        }

        public String getPriceInfo() {
            return priceInfo;
        }

        public void setPriceInfo(String priceInfo) {
            this.priceInfo = priceInfo;
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
    }

    // ==================== Getter / Setter ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Integer getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(Integer totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String getCompanionType() {
        return companionType;
    }

    public void setCompanionType(String companionType) {
        this.companionType = companionType;
    }

    public Integer getBudgetMin() {
        return budgetMin;
    }

    public void setBudgetMin(Integer budgetMin) {
        this.budgetMin = budgetMin;
    }

    public Integer getBudgetMax() {
        return budgetMax;
    }

    public void setBudgetMax(Integer budgetMax) {
        this.budgetMax = budgetMax;
    }

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<DayPlan> getDayPlans() {
        return dayPlans;
    }

    public void setDayPlans(List<DayPlan> dayPlans) {
        this.dayPlans = dayPlans;
    }
}
