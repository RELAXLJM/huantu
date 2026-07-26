package com.huantu.mapper;

import com.huantu.entity.Favorite;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收藏 Mapper
 */
public interface FavoriteMapper {

    int insert(Favorite favorite);

    Favorite findByUserAndTarget(@Param("userId") Long userId,
                                  @Param("targetId") Long targetId,
                                  @Param("targetType") Integer targetType);

    int deleteById(@Param("id") Long id);

    List<Favorite> findByUser(@Param("userId") Long userId,
                              @Param("targetType") Integer targetType);

    int countByUser(@Param("userId") Long userId);
}
