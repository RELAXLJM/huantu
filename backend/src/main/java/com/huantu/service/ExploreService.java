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
     * 搜索景点
     */
    public List<ScenicVO> search(String cityCode, String keyword, String poiType) {
        List<Scenic> scenics = scenicMapper.search(cityCode, poiType, keyword);
        return scenics.stream()
                .map(this::toVO)
                .collect(Collectors.toList());
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
