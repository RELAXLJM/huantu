package com.huantu.mapper;

import com.huantu.entity.PostInteract;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 帖子互动 Mapper
 */
public interface PostInteractMapper {

    int insert(PostInteract interact);

    /** 检查是否已互动过 */
    PostInteract findByUserAndPost(@Param("userId") Long userId,
                                   @Param("postId") Long postId,
                                   @Param("type") Integer type);

    int deleteById(@Param("id") Long id);

    /** 批量查询用户对一组帖子的互动状态 */
    List<PostInteract> findByUserAndPosts(@Param("userId") Long userId,
                                          @Param("postIds") List<Long> postIds,
                                          @Param("type") Integer type);
}
