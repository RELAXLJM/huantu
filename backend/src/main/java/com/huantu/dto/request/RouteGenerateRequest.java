package com.huantu.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * AI路线生成请求
 */
public class RouteGenerateRequest {

    @NotBlank(message = "目的地不能为空")
    private String destination;

    @NotNull(message = "游玩天数不能为空")
    @Min(value = 1, message = "至少1天")
    @Max(value = 5, message = "最多5天")
    private Integer days;

    private String companionType;    // solo/couple/family/elderly/pet

    @Min(value = 0, message = "预算不能为负")
    private Integer budgetMin;

    @Min(value = 0, message = "预算不能为负")
    private Integer budgetMax;

    private String preference;       // 偏好标签，逗号分隔

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
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
}
