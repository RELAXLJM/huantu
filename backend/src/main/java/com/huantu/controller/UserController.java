package com.huantu.controller;

import com.huantu.common.Result;
import com.huantu.dto.request.LoginRequest;
import com.huantu.dto.request.RegisterRequest;
import com.huantu.dto.response.UserVO;
import com.huantu.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterRequest req) {
        UserVO userVO = userService.register(req);
        return Result.success("注册成功", userVO);
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<Map<String, String>> login(@Valid @RequestBody LoginRequest req) {
        String sessionId = userService.login(req);
        Map<String, String> data = new HashMap<>();
        data.put("sessionId", sessionId);
        return Result.success("登录成功", data);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String sessionId = getSessionId(request);
        userService.logout(sessionId);
        return Result.success();
    }

    /**
     * 获取个人信息
     */
    @GetMapping("/profile")
    public Result<UserVO> getProfile(HttpServletRequest request) {
        Long userId = getUserId(request);
        UserVO userVO = userService.getProfile(userId);
        return Result.success(userVO);
    }

    /**
     * 修改个人信息
     */
    @PutMapping("/profile")
    public Result<UserVO> updateProfile(
            HttpServletRequest request,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String avatarUrl,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String bio) {
        Long userId = getUserId(request);
        UserVO userVO = userService.updateProfile(userId, nickname, avatarUrl, city, bio);
        return Result.success("修改成功", userVO);
    }

    // ==================== 工具方法 ====================

    /** 从请求中获取当前登录用户ID（由LoginInterceptor注入） */
    private Long getUserId(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return (Long) userId;
    }

    /** 从请求中获取 sessionId */
    private String getSessionId(HttpServletRequest request) {
        String sessionId = request.getHeader("X-Session-Id");
        if (sessionId == null) {
            jakarta.servlet.http.Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (jakarta.servlet.http.Cookie cookie : cookies) {
                    if ("sessionId".equals(cookie.getName())) {
                        sessionId = cookie.getValue();
                        break;
                    }
                }
            }
        }
        return sessionId;
    }
}
