package com.gdpt.huantu.core.model;

import com.google.gson.annotations.SerializedName;

/**
 * 路线中的景点项
 */
public class ScenicItem {

    @SerializedName("scenicId")
    private long scenicId;

    @SerializedName("name")
    private String name;

    @SerializedName("poiType")
    private String poiType;

    @SerializedName("address")
    private String address;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("rating")
    private double rating;

    @SerializedName("images")
    private String images;

    @SerializedName("priceInfo")
    private String priceInfo;

    @SerializedName("stayDuration")
    private int stayDuration;

    @SerializedName("transportTip")
    private String transportTip;

    @SerializedName("memo")
    private String memo;

    public ScenicItem() {
    }

    public long getScenicId() { return scenicId; }
    public void setScenicId(long scenicId) { this.scenicId = scenicId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPoiType() { return poiType; }
    public void setPoiType(String poiType) { this.poiType = poiType; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }

    public String getPriceInfo() { return priceInfo; }
    public void setPriceInfo(String priceInfo) { this.priceInfo = priceInfo; }

    public int getStayDuration() { return stayDuration; }
    public void setStayDuration(int stayDuration) { this.stayDuration = stayDuration; }

    public String getTransportTip() { return transportTip; }
    public void setTransportTip(String transportTip) { this.transportTip = transportTip; }

    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }

    /**
     * 获取停留时长文本
     */
    public String getStayDurationText() {
        if (stayDuration <= 0) return "";
        int hours = stayDuration / 60;
        int mins = stayDuration % 60;
        if (hours > 0) {
            return "建议游玩 " + hours + "小时" + (mins > 0 ? mins + "分钟" : "");
        }
        return "建议游玩 " + mins + "分钟";
    }
}
