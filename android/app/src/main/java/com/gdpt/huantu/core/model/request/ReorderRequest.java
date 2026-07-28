package com.gdpt.huantu.core.model.request;

import com.google.gson.annotations.SerializedName;

/**
 * 路线景点排序请求项
 */
public class ReorderRequest {

    @SerializedName("id")
    private long id;

    @SerializedName("dayNumber")
    private int dayNumber;

    @SerializedName("sortOrder")
    private int sortOrder;

    public ReorderRequest(long id, int dayNumber, int sortOrder) {
        this.id = id;
        this.dayNumber = dayNumber;
        this.sortOrder = sortOrder;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public int getDayNumber() { return dayNumber; }
    public void setDayNumber(int dayNumber) { this.dayNumber = dayNumber; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
