package com.huantu.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 腾讯地图 WebService API 封装
 */
@Service
public class AmapService {

    private static final Logger log = LoggerFactory.getLogger(AmapService.class);

    @Value("${tencent.map.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ==================== POI搜索 ====================

    private List<PoiInfo> parsePoiResponse(String url) {
        try {
            String resp = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(resp);

            if (root.get("status").asInt() != 0) {
                log.warn("腾讯POI搜索失败: {}", resp);
                return List.of();
            }

            List<PoiInfo> pois = new ArrayList<>();
            JsonNode dataArray = root.path("data");
            for (JsonNode poi : dataArray) {
                PoiInfo info = new PoiInfo();
                info.setName(poi.path("title").asText());
                info.setAddress(poi.path("address").asText());
                info.setPoiType(poi.path("category").asText());
                info.setLongitude(poi.path("location").path("lng").asDouble());
                info.setLatitude(poi.path("location").path("lat").asDouble());

                JsonNode adInfo = poi.path("ad_info");
                info.setCityName(adInfo.path("city").asText());
                info.setCityCode(String.valueOf(adInfo.path("adcode").asInt()));

                pois.add(info);
            }
            return pois;
        } catch (Exception e) {
            log.error("腾讯POI搜索异常: ", e);
            return List.of();
        }
    }

    /**
     * 全国范围搜索POI（不限城市）
     */
    public List<PoiInfo> searchNationwide(String keyword) {
        String url = String.format(
                "https://apis.map.qq.com/ws/place/v1/search?key=%s&keyword=%s&page_size=20&page_index=1",
                apiKey, keyword);
        return parsePoiResponse(url);
    }

    /**
     * 根据城市和关键词搜索POI
     */
    public List<PoiInfo> searchPoi(String city, String keywords, String poiType) {
        String keyword = keywords != null ? keywords : "景点";
        String url = String.format(
                "https://apis.map.qq.com/ws/place/v1/search?key=%s&keyword=%s&boundary=region(%s,0)&page_size=20&page_index=1",
                apiKey, keyword, city);
        return parsePoiResponse(url);
    }

    /**
     * 周边搜索（基于经纬度）
     */
    public List<PoiInfo> searchAround(double longitude, double latitude, String poiType) {
        String types = poiType != null ? poiType : "景点";
        String url = String.format(
                "https://apis.map.qq.com/ws/place/v1/search?key=%s&boundary=nearby(%f,%f,5000)&keyword=%s&page_size=20&page_index=1",
                apiKey, latitude, longitude, types);
        List<PoiInfo> pois = parsePoiResponse(url);
        // 填充距离信息
        for (PoiInfo info : pois) {
            info.setDistance(calculateDistance(longitude, latitude, info.getLongitude(), info.getLatitude()));
        }
        return pois;
    }

    /**
     * 简易距离计算（单位：米）
     */
    private int calculateDistance(double lng1, double lat1, double lng2, double lat2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (int) (6371000 * c);
    }

    // ==================== 天气查询 ====================

    /**
     * 查询城市天气
     */
    public WeatherInfo getWeather(String cityCode) {
        String url = String.format(
                "https://apis.map.qq.com/ws/weather/v1/?key=%s&adcode=%s",
                apiKey, cityCode);

        try {
            String resp = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(resp);

            if (root.get("status").asInt() != 0) {
                log.warn("天气查询失败: {}", resp);
                return null;
            }

            JsonNode realtime = root.path("result").path("realtime").get(0);
            WeatherInfo info = new WeatherInfo();
            info.setCity(realtime.path("province").asText() + realtime.path("city").asText());
            info.setDate(realtime.path("update_time").asText().substring(0, 10));
            info.setDayWeather(realtime.path("infos").path("weather").asText());
            info.setDayTemp(Integer.parseInt(realtime.path("infos").path("temperature").asText()));
            info.setDayWind(realtime.path("infos").path("wind_direction").asText());
            info.setDayPower(realtime.path("infos").path("wind_power").asText());
            return info;
        } catch (Exception e) {
            log.error("天气查询异常: ", e);
            return null;
        }
    }

    // ==================== 逆地理编码 ====================

    /**
     * 经纬度 → 城市名
     */
    public String reverseGeocode(double longitude, double latitude) {
        String url = String.format(
                "https://apis.map.qq.com/ws/geocoder/v1/?key=%s&location=%f,%f",
                apiKey, latitude, longitude);

        try {
            String resp = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(resp);
            if (root.get("status").asInt() == 0) {
                return root.path("result").path("address_component").path("city").asText();
            }
        } catch (Exception e) {
            log.error("逆地理编码异常: ", e);
        }
        return null;
    }

    // ==================== 内部数据类 ====================

    public static class PoiInfo {
        private String name;
        private String address;
        private String poiType;
        private Double longitude;
        private Double latitude;
        private String cityName;
        private String cityCode;
        private Double rating;
        private String images;
        private Integer distance;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getPoiType() { return poiType; }
        public void setPoiType(String poiType) { this.poiType = poiType; }
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        public String getCityName() { return cityName; }
        public void setCityName(String cityName) { this.cityName = cityName; }
        public String getCityCode() { return cityCode; }
        public void setCityCode(String cityCode) { this.cityCode = cityCode; }
        public Double getRating() { return rating; }
        public void setRating(Double rating) { this.rating = rating; }
        public String getImages() { return images; }
        public void setImages(String images) { this.images = images; }
        public Integer getDistance() { return distance; }
        public void setDistance(Integer distance) { this.distance = distance; }
    }

    public static class WeatherInfo {
        private String city;
        private String date;
        private String dayWeather;
        private String nightWeather;
        private Integer dayTemp;
        private Integer nightTemp;
        private String dayWind;
        private String dayPower;

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getDayWeather() { return dayWeather; }
        public void setDayWeather(String dayWeather) { this.dayWeather = dayWeather; }
        public String getNightWeather() { return nightWeather; }
        public void setNightWeather(String nightWeather) { this.nightWeather = nightWeather; }
        public Integer getDayTemp() { return dayTemp; }
        public void setDayTemp(Integer dayTemp) { this.dayTemp = dayTemp; }
        public Integer getNightTemp() { return nightTemp; }
        public void setNightTemp(Integer nightTemp) { this.nightTemp = nightTemp; }
        public String getDayWind() { return dayWind; }
        public void setDayWind(String dayWind) { this.dayWind = dayWind; }
        public String getDayPower() { return dayPower; }
        public void setDayPower(String dayPower) { this.dayPower = dayPower; }
    }
}
