package com.gdpt.huantu.core.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 单日计划
 */
public class DayPlan {

    @SerializedName("dayNumber")
    private int dayNumber;

    @SerializedName("scenics")
    private List<ScenicItem> scenics;

    public DayPlan() {
    }

    public int getDayNumber() { return dayNumber; }
    public void setDayNumber(int dayNumber) { this.dayNumber = dayNumber; }

    public List<ScenicItem> getScenics() { return scenics; }
    public void setScenics(List<ScenicItem> scenics) { this.scenics = scenics; }

    public String getDayTitle() {
        return "第" + dayNumber + "天";
    }
}
