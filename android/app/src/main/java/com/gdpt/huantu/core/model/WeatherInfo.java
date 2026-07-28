package com.gdpt.huantu.core.model;

import com.google.gson.annotations.SerializedName;

/**
 * 天气信息模型
 */
public class WeatherInfo {

    @SerializedName("city")
    private String city;

    @SerializedName("date")
    private String date;

    @SerializedName("dayWeather")
    private String dayWeather;

    @SerializedName("nightWeather")
    private String nightWeather;

    @SerializedName("dayTemp")
    private int dayTemp;

    @SerializedName("nightTemp")
    private int nightTemp;

    @SerializedName("dayWind")
    private String dayWind;

    @SerializedName("dayPower")
    private String dayPower;

    public WeatherInfo() {
    }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getDayWeather() { return dayWeather; }
    public void setDayWeather(String dayWeather) { this.dayWeather = dayWeather; }

    public String getNightWeather() { return nightWeather; }
    public void setNightWeather(String nightWeather) { this.nightWeather = nightWeather; }

    public int getDayTemp() { return dayTemp; }
    public void setDayTemp(int dayTemp) { this.dayTemp = dayTemp; }

    public int getNightTemp() { return nightTemp; }
    public void setNightTemp(int nightTemp) { this.nightTemp = nightTemp; }

    public String getDayWind() { return dayWind; }
    public void setDayWind(String dayWind) { this.dayWind = dayWind; }

    public String getDayPower() { return dayPower; }
    public void setDayPower(String dayPower) { this.dayPower = dayPower; }

    /**
     * 获取天气+温度描述文本
     */
    public String getWeatherSummary() {
        return city + " · " + dayWeather + " " + dayTemp + "°";
    }
}
