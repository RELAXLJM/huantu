package com.huantu.mapper;

import com.huantu.entity.Scenic;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 景点 Mapper
 */
public interface ScenicMapper {

    Scenic findById(@Param("id") Long id);

    /** 根据城市和类型搜索 */
    List<Scenic> search(@Param("cityCode") String cityCode,
                        @Param("poiType") String poiType,
                        @Param("keyword") String keyword);

    /** 批量查询 */
    List<Scenic> findByIds(@Param("ids") List<Long> ids);

    int insert(Scenic scenic);

    int insertBatch(@Param("list") List<Scenic> list);
}
