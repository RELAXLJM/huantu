package com.huantu.mapper;

import com.huantu.entity.Post;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 帖子 Mapper
 */
public interface PostMapper {

    int insert(Post post);

    Post findById(@Param("id") Long id);

    List<Post> findList(@Param("cityCode") String cityCode,
                        @Param("offset") int offset,
                        @Param("limit") int limit);

    int updateById(Post post);

    /** 更新互动计数 */
    int updateCount(@Param("id") Long id,
                    @Param("field") String field,
                    @Param("delta") int delta);

    int deleteById(@Param("id") Long id);

    int countAll();
}
