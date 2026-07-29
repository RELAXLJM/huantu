package com.huantu.mapper;

import com.huantu.entity.Route;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 路线 Mapper
 */
public interface RouteMapper {

    int insert(Route route);

    Route findById(@Param("id") Long id);

    List<Route> findByUserId(@Param("userId") Long userId,
                             @Param("status") Integer status);

    int updateById(Route route);

    int deleteById(@Param("id") Long id);

    int countByUserId(@Param("userId") Long userId);

    int countAll();
}
