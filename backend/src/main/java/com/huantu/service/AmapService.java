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
 * 高德地图 Web API 封装
 */
@Service
public class AmapService {

    private static final Logger log = LoggerFactory.getLogger(AmapService.class);

    @Value("${amap.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ==================== POI搜索 ====================

    /**
     * 根据城市和关键词搜索POI
     */
    public List<PoiInfo> searchPoi(String city, String keywords, String poiType) {
        String url = String.format(
                "https://restapi.amap.com/v3/place/text?key=%s&city=%s&keywords=%s&types=%s&offset=20&page=1&extensions=all",
                apiKey, city, keywords, poiType != null ? poiType : "");

        try {
            String resp = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(resp);

            if (root.get("status").asInt() != 1) {
                log.warn("高德POI搜索失败: {}", resp);
                return List.of();
            }

            List<PoiInfo> pois = new ArrayList<>();
            JsonNode poisNode = root.path("pois");
            for (JsonNode poi : poisNode) {
                PoiInfo info = new PoiInfo();
                info.setName(poi.path("name").asText());
                info.setAddress(poi.path("address").asText());
                info.setPoiType(poi.path("type").asText());
                String[] location = poi.path("location").asText().split(",");
                if (location.length == 2) {
                    info.setLongitude(Double.parseDouble(location[0]));
                    info.setLatitude(Double.parseDouble(location[1]));
                }
                info.setCityName(poi.path("cityname").asText());
                info.setCityCode(poi.path("citycode").asText());
                // 评分
                JsonNode ratingNode = poi.path("biz_ext").path("rating");
                if (!ratingNode.isMissingNode()) {
                    info.setRating(Double.parseDouble(ratingNode.asText()));
                }
                // 图片
                JsonNode photos = poi.path("photos");
                if (photos.isArray() && photos.size() > 0) {
                    List<String> imgList = new ArrayList<>();
                    for (JsonNode photo : photos) {
                        imgList.add(photo.path("url").asText());
                    }
                    info.setImages(String.join(",", imgList));
                }
                pois.add(info);
            }
            return pois;
        } catch (Exception e) {
            log.error("高德POI搜索异常: ", e);
            return List.of();
        }
    }

    /**
     * 周边搜索（基于经纬度）
     */
    public List<PoiInfo> searchAround(double longitude, double latitude, String poiType) {
        String url = String.format(
                "https://restapi.amap.com/v3/place/around?key=%s&location=%f,%f&radius=5000&types=%s&offset=20&page=1&extensions=all",
                apiKey, longitude, latitude, poiType != null ? poiType : "景点");

        try {
            String resp = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(resp);

            if (root.get("status").asInt() != 1) {
                log.warn("高德周边搜索失败: {}", resp);
                return List.of();
            }

            List<PoiInfo> pois = new ArrayList<>();
            JsonNode poisNode = root.path("pois");
            for (JsonNode poi : poisNode) {
                PoiInfo info = new PoiInfo();
                info.setName(poi.path("name").asText());
                info.setAddress(poi.path("address").asText());
                info.setPoiType(poi.path("type").asText());
                String[] location = poi.path("location").asText().split(",");
                if (location.length == 2) {
                    info.setLongitude(Double.parseDouble(location[0]));
                    info.setLatitude(Double.parseDouble(location[1]));
                }
                info.setCityName(poi.path("cityname").asText());
                info.setCityCode(poi.path("citycode").asText());

                JsonNode ratingNode = poi.path("biz_ext").path("rating");
                if (!ratingNode.isMissingNode()) {
                    info.setRating(Double.parseDouble(ratingNode.asText()));
                }
                // 距离
                info.setDistance(poi.path("distance").asInt());

                JsonNode photos = poi.path("photos");
                if (photos.isArray() && photos.size() > 0) {
                    List<String> imgList = new ArrayList<>();
                    for (JsonNode photo : photos) {
                        imgList.add(photo.path("url").asText());
                    }
                    info.setImages(String.join(",", imgList));
                }
                pois.add(info);
            }
            return pois;
        } catch (Exception e) {
            log.error("高德周边搜索异常: ", e);
            return List.of();
        }
    }

    // ==================== 天气查询 ====================

    /**
     * 查询城市天气
     */
    public WeatherInfo getWeather(String cityCode) {
        String url = String.format(
                "https://restapi.amap.com/v3/weather/weatherInfo?key=%s&city=%s&extensions=all",
                apiKey, cityCode);

        try {
            String resp = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(resp);

            if (root.get("status").asInt() != 1) {
                log.warn("天气查询失败: {}", resp);
                return null;
            }

            JsonNode forecast = root.path("forecasts").get(0);
            JsonNode today = forecast.path("casts").get(0);

            WeatherInfo info = new WeatherInfo();
            info.setCity(forecast.path("city").asText());
            info.setDate(today.path("date").asText());
            info.setDayWeather(today.path("dayweather").asText());
            info.setNightWeather(today.path("nightweather").asText());
            info.setDayTemp(Integer.parseInt(today.path("daytemp").asText()));
            info.setNightTemp(Integer.parseInt(today.path("nighttemp").asText()));
            info.setDayWind(today.path("daywind").asText());
            info.setDayPower(today.path("daypower").asText());
            return info;
        } catch (Exception e) {
            log.error("天气查询异常: ", e);
            return null;
        }
    }

    // ==================== 逆地理编码 ====================

    /**
     * 经纬度 → 城市信息
     */
    public String reverseGeocode(double longitude, double latitude) {
        String url = String.format(
                "https://restapi.amap.com/v3/geocode/regeo?key=%s&location=%f,%f",
                apiKey, longitude, latitude);

        try {
            String resp = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(resp);
            if (root.get("status").asInt() == 1) {
                return root.path("regeocode").path("addressComponent").path("city").asText();
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
        private Integer distance;  // 距离（米）

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
