package com.huantu.service;

import com.huantu.entity.Scenic;
import com.huantu.entity.UserFootprint;
import com.huantu.mapper.ScenicMapper;
import com.huantu.mapper.UserFootprintMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 足迹业务逻辑
 */
@Service
public class FootprintService {

    @Autowired
    private UserFootprintMapper footprintMapper;

    @Autowired
    private ScenicMapper scenicMapper;

    /**
     * 添加足迹（打卡）
     */
    public void addFootprint(Long userId, Long scenicId) {
        Scenic scenic = scenicMapper.findById(scenicId);

        UserFootprint fp = new UserFootprint();
        fp.setUserId(userId);
        fp.setScenicId(scenicId);
        if (scenic != null) {
            fp.setCity(scenic.getCity());
            fp.setCityCode(scenic.getCityCode());
            fp.setLongitude(scenic.getLongitude());
            fp.setLatitude(scenic.getLatitude());
        }
        footprintMapper.insert(fp);
    }

    /**
     * 足迹列表
     */
    public List<Map<String, Object>> getFootprints(Long userId) {
        List<UserFootprint> footprints = footprintMapper.findByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();

        // 批量查询景点名
        List<Long> scenicIds = footprints.stream()
                .map(UserFootprint::getScenicId).filter(id -> id > 0)
                .distinct().collect(Collectors.toList());
        Map<Long, String> scenicNames = Map.of();
        if (!scenicIds.isEmpty()) {
            scenicNames = scenicMapper.findByIds(scenicIds).stream()
                    .collect(Collectors.toMap(Scenic::getId, Scenic::getName));
        }

        for (UserFootprint fp : footprints) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", fp.getId());
            item.put("scenicId", fp.getScenicId());
            item.put("scenicName", scenicNames.getOrDefault(fp.getScenicId(), "未知"));
            item.put("city", fp.getCity());
            item.put("cityCode", fp.getCityCode());
            item.put("longitude", fp.getLongitude());
            item.put("latitude", fp.getLatitude());
            item.put("createdAt", fp.getCreatedAt() != null ? fp.getCreatedAt().toString() : null);
            result.add(item);
        }
        return result;
    }

    /**
     * 足迹地图数据（城市级别）
     */
    public List<Map<String, Object>> getFootprintMap(Long userId) {
        return footprintMapper.getFootprintCities(userId);
    }

    /**
     * 足迹城市数
     */
    public int countCities(Long userId) {
        return footprintMapper.countCities(userId);
    }
}
