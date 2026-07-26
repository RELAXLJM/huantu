package com.huantu.mapper;

import com.huantu.entity.RouteScenic;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 路线-景点关联 Mapper
 */
public interface RouteScenicMapper {

    int insert(RouteScenic routeScenic);

    /** 批量插入 */
    int batchInsert(@Param("list") List<RouteScenic> list);

    /** 查询某条路线的所有关联景点，按天和排序 */
    List<RouteScenic> findByRouteId(@Param("routeId") Long routeId);

    int updateById(RouteScenic routeScenic);

    /** 删除某条路线的所有关联 */
    int deleteByRouteId(@Param("routeId") Long routeId);
}
