package com.gdpt.huantu.core.model;

import com.google.gson.annotations.SerializedName;

/**
 * 景点模型（对应后端 ScenicVO）
 */
public class Scenic {

    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("poiType")
    private String poiType;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("address")
    private String address;

    @SerializedName("city")
    private String city;

    @SerializedName("rating")
    private double rating;

    @SerializedName("images")
    private String images; // JSON数组字符串

    @SerializedName("openTime")
    private String openTime;

    @SerializedName("priceInfo")
    private String priceInfo;

    @SerializedName("tag")
    private String tag;

    public Scenic() {
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPoiType() { return poiType; }
    public void setPoiType(String poiType) { this.poiType = poiType; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }

    public String getOpenTime() { return openTime; }
    public void setOpenTime(String openTime) { this.openTime = openTime; }

    public String getPriceInfo() { return priceInfo; }
    public void setPriceInfo(String priceInfo) { this.priceInfo = priceInfo; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    /**
     * 获取首张图片URL
     */
    public String getFirstImage() {
        if (images == null || images.isEmpty() || "[]".equals(images)) {
            return null;
        }
        try {
            // images 是 JSON 数组如 ["url1","url2"]
            String cleaned = images.replaceAll("[\\[\\]\"]", "");
            String[] urls = cleaned.split(",");
            return urls.length > 0 ? urls[0].trim() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取POI类型中文名
     */
    public String getPoiTypeName() {
        if (poiType == null) return "其他";
        switch (poiType) {
            case "scenic": return "景点";
            case "food": return "美食";
            case "hotel": return "住宿";
            case "shopping": return "购物";
            default: return poiType;
        }
    }
}
