package com.gdpt.huantu.core.model.request;

import com.google.gson.annotations.SerializedName;

/**
 * AI路线生成请求
 */
public class RouteGenerateRequest {

    @SerializedName("destination")
    private String destination;

    @SerializedName("days")
    private int days;

    @SerializedName("companionType")
    private String companionType;

    @SerializedName("budgetMin")
    private int budgetMin;

    @SerializedName("budgetMax")
    private int budgetMax;

    @SerializedName("preference")
    private String preference;

    public RouteGenerateRequest() {
    }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public int getDays() { return days; }
    public void setDays(int days) { this.days = days; }

    public String getCompanionType() { return companionType; }
    public void setCompanionType(String companionType) { this.companionType = companionType; }

    public int getBudgetMin() { return budgetMin; }
    public void setBudgetMin(int budgetMin) { this.budgetMin = budgetMin; }

    public int getBudgetMax() { return budgetMax; }
    public void setBudgetMax(int budgetMax) { this.budgetMax = budgetMax; }

    public String getPreference() { return preference; }
    public void setPreference(String preference) { this.preference = preference; }
}
