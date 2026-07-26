package com.huantu.service;

import com.huantu.common.ResultCode;
import com.huantu.common.exception.BusinessException;
import com.huantu.dto.request.LoginRequest;
import com.huantu.dto.request.RegisterRequest;
import com.huantu.dto.response.UserVO;
import com.huantu.entity.User;
import com.huantu.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 用户业务逻辑
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private FootprintService footprintService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户注册
     */
    public UserVO register(RegisterRequest req) {
        // 1. 检查手机号是否已注册
        User existUser = userMapper.findByPhone(req.getPhone());
        if (existUser != null) {
            throw new BusinessException(ResultCode.PHONE_ALREADY_REGISTERED);
        }

        // 2. 创建用户
        User user = new User();
        user.setNickname(req.getNickname());
        user.setPhone(req.getPhone());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setStatus(1);

        int rows = userMapper.insert(user);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "注册失败，请稍后重试");
        }

        log.info("新用户注册: phone={}, id={}", maskPhone(req.getPhone()), user.getId());
        return toVO(user);
    }

    /**
     * 用户登录，返回 sessionId
     */
    public String login(LoginRequest req) {
        // 1. 查询用户
        User user = userMapper.findByPhone(req.getPhone());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 2. 校验密码
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        // 3. 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.FORBIDDEN, "账号已被禁用");
        }

        // 4. 生成 sessionId 存入 Redis（7天有效）
        String sessionId = UUID.randomUUID().toString().replace("-", "");
        String sessionKey = "session:" + sessionId;
        redisTemplate.opsForValue().set(sessionKey, user.getId(), 7, TimeUnit.DAYS);

        log.info("用户登录: phone={}, sessionId={}", maskPhone(req.getPhone()), sessionId);
        return sessionId;
    }

    /**
     * 退出登录
     */
    public void logout(String sessionId) {
        if (sessionId != null && !sessionId.isEmpty()) {
            redisTemplate.delete("session:" + sessionId);
            log.info("用户退出登录: sessionId={}", sessionId);
        }
    }

    /**
     * 获取个人信息
     */
    public UserVO getProfile(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        UserVO vo = toVO(user);

        // 统计信息
        vo.setRouteCount(userMapper.countRoutes(userId));
        vo.setFootprintCount(footprintService.countCities(userId));
        vo.setLikeCount(0);

        return vo;
    }

    /**
     * 修改个人信息
     */
    public UserVO updateProfile(Long userId, String nickname, String avatarUrl, String city, String bio) {
        User user = new User();
        user.setId(userId);
        user.setNickname(nickname);
        user.setAvatarUrl(avatarUrl);
        user.setCity(city);
        user.setBio(bio);

        userMapper.updateById(user);
        return getProfile(userId);
    }

    // ==================== 工具方法 ====================

    /** Entity → VO */
    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setPhone(maskPhone(user.getPhone()));
        vo.setCity(user.getCity());
        vo.setBio(user.getBio());
        if (user.getCreatedAt() != null) {
            vo.setCreatedAt(user.getCreatedAt().format(DTF));
        }
        return vo;
    }

    /** 手机号脱敏: 138****1234 */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
