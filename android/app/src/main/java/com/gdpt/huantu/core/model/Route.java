package com.gdpt.huantu.core.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 路线模型（对应后端 RouteVO）
 */
public class Route {

    @SerializedName("id")
    private long id;

    @SerializedName("title")
    private String title;

    @SerializedName("coverUrl")
    private String coverUrl;

    @SerializedName("destination")
    private String destination;

    @SerializedName("cityCode")
    private String cityCode;

    @SerializedName("days")
    private int days;

    @SerializedName("totalDuration")
    private int totalDuration;

    @SerializedName("companionType")
    private String companionType;

    @SerializedName("budgetMin")
    private int budgetMin;

    @SerializedName("budgetMax")
    private int budgetMax;

    @SerializedName("preference")
    private String preference;

    @SerializedName("status")
    private int status;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("dayPlans")
    private List<DayPlan> dayPlans;

    public Route() {
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getCityCode() { return cityCode; }
    public void setCityCode(String cityCode) { this.cityCode = cityCode; }

    public int getDays() { return days; }
    public void setDays(int days) { this.days = days; }

    public int getTotalDuration() { return totalDuration; }
    public void setTotalDuration(int totalDuration) { this.totalDuration = totalDuration; }

    public String getCompanionType() { return companionType; }
    public void setCompanionType(String companionType) { this.companionType = companionType; }

    public int getBudgetMin() { return budgetMin; }
    public void setBudgetMin(int budgetMin) { this.budgetMin = budgetMin; }

    public int getBudgetMax() { return budgetMax; }
    public void setBudgetMax(int budgetMax) { this.budgetMax = budgetMax; }

    public String getPreference() { return preference; }
    public void setPreference(String preference) { this.preference = preference; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public List<DayPlan> getDayPlans() { return dayPlans; }
    public void setDayPlans(List<DayPlan> dayPlans) { this.dayPlans = dayPlans; }

    /**
     * 获取状态文本
     */
    public String getStatusText() {
        switch (status) {
            case 0: return "草稿";
            case 1: return "已保存";
            case 2: return "已结束";
            default: return "未知";
        }
    }

    /**
     * 获取同行人类型文本
     */
    public String getCompanionTypeText() {
        if (companionType == null) return "独自";
        switch (companionType) {
            case "solo": return "独自";
            case "couple": return "情侣";
            case "family": return "家庭";
            case "elderly": return "带长辈";
            case "pet": return "带宠物";
            default: return companionType;
        }
    }

    /**
     * 获取总景点数
     */
    public int getScenicCount() {
        if (dayPlans == null) return 0;
        int count = 0;
        for (DayPlan dp : dayPlans) {
            if (dp.getScenics() != null) {
                count += dp.getScenics().size();
            }
        }
        return count;
    }

    /**
     * 获取时长文本
     */
    public String getDurationText() {
        int hours = totalDuration / 60;
        int mins = totalDuration % 60;
        if (hours > 0) {
            return hours + "小时" + (mins > 0 ? mins + "分钟" : "");
        }
        return mins + "分钟";
    }
}
