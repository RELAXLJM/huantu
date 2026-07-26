package com.huantu.mapper;

import com.huantu.entity.UserFootprint;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户足迹 Mapper
 */
public interface UserFootprintMapper {

    int insert(UserFootprint footprint);

    /** 用户足迹列表 */
    List<UserFootprint> findByUserId(@Param("userId") Long userId);

    /** 用户去过的城市数 */
    int countCities(@Param("userId") Long userId);

    /** 用户足迹城市列表（用于地图展示） */
    List<Map<String, Object>> getFootprintCities(@Param("userId") Long userId);
}
