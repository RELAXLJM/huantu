package com.huantu.mapper;

import com.huantu.entity.User;
import org.apache.ibatis.annotations.Param;

/**
 * 用户 Mapper 接口
 */
public interface UserMapper {

    /** 根据手机号查询用户 */
    User findByPhone(@Param("phone") String phone);

    /** 根据ID查询用户 */
    User findById(@Param("id") Long id);

    /** 插入用户 */
    int insert(User user);

    /** 更新用户信息 */
    int updateById(User user);

    /** 统计用户的路线数 */
    int countRoutes(@Param("userId") Long userId);
}
