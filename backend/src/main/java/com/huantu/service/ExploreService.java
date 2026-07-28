package com.huantu.service;

import com.huantu.dto.response.ScenicVO;
import com.huantu.entity.Scenic;
import com.huantu.mapper.ScenicMapper;
import com.huantu.service.AmapService.WeatherInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 主页/探索业务逻辑
 */
@Service
public class ExploreService {

    private static final Logger log = LoggerFactory.getLogger(ExploreService.class);

    @Autowired
    private ScenicMapper scenicMapper;

    @Autowired
    private AmapService amapService;

    /**
     * 周边热门景点（根据城市）
     */
    public List<ScenicVO> getNearby(String city, Integer limit) {
        if (limit == null || limit <= 0) limit = 10;
        List<Scenic> scenics = scenicMapper.search(null, null, city);
        return scenics.stream()
                .sorted((a, b) -> {
                    Double ra = a.getRating() != null ? a.getRating() : 0;
                    Double rb = b.getRating() != null ? b.getRating() : 0;
                    return rb.compareTo(ra);
                })
                .limit(limit)
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    /**
     * 天气查询
     */
    public WeatherInfo getWeather(String cityCode) {
        return amapService.getWeather(cityCode);
    }

    /**
     * 城市必玩榜单（按评分排序）
     */
    public List<ScenicVO> getRankings(String city, String tag, Integer limit) {
        if (limit == null || limit <= 0) limit = 5;

        List<Scenic> scenics = scenicMapper.search(null, null, city);
        return scenics.stream()
                .filter(s -> {
                    if (tag == null || tag.isEmpty()) return true;
                    return s.getTag() != null && s.getTag().contains(tag);
                })
                .sorted((a, b) -> {
                    Double ra = a.getRating() != null ? a.getRating() : 0;
                    Double rb = b.getRating() != null ? b.getRating() : 0;
                    return rb.compareTo(ra);
                })
                .limit(limit)
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    /**
     * 搜索景点（本地DB → 腾讯地图API全国搜索回退）
     */
    public List<ScenicVO> search(String cityCode, String keyword, String poiType) {
        List<Scenic> scenics = scenicMapper.search(cityCode, poiType, keyword);
        if (!scenics.isEmpty()) {
            return scenics.stream().map(this::toVO).collect(Collectors.toList());
        }
        // 本地无结果 → 调用腾讯地图全国搜索
        List<AmapService.PoiInfo> pois = amapService.searchNationwide(keyword);
        return pois.stream().map(this::poiToVO).collect(Collectors.toList());
    }

    /**
     * 基于GPS的周边热门景点
     */
    public List<ScenicVO> getNearbyByGps(double lng, double lat, Integer limit) {
        if (limit == null || limit <= 0) limit = 10;
        List<AmapService.PoiInfo> pois = amapService.searchAround(lng, lat, "旅游景点");
        return pois.stream()
                .limit(limit)
                .map(this::poiToVO)
                .collect(Collectors.toList());
    }

    private ScenicVO poiToVO(AmapService.PoiInfo poi) {
        ScenicVO vo = new ScenicVO();
        vo.setName(poi.getName());
        vo.setPoiType(poi.getPoiType());
        vo.setLongitude(poi.getLongitude());
        vo.setLatitude(poi.getLatitude());
        vo.setAddress(poi.getAddress());
        vo.setCity(poi.getCityName());
        vo.setRating(poi.getRating() != null ? poi.getRating() : 0.0);
        vo.setImages(poi.getImages());
        return vo;
    }

    private ScenicVO toVO(Scenic s) {
        ScenicVO vo = new ScenicVO();
        vo.setId(s.getId());
        vo.setName(s.getName());
        vo.setPoiType(s.getPoiType());
        vo.setLongitude(s.getLongitude());
        vo.setLatitude(s.getLatitude());
        vo.setAddress(s.getAddress());
        vo.setCity(s.getCity());
        vo.setRating(s.getRating());
        vo.setImages(s.getImages());
        vo.setOpenTime(s.getOpenTime());
        vo.setPriceInfo(s.getPriceInfo());
        vo.setTag(s.getTag());
        return vo;
    }
}
