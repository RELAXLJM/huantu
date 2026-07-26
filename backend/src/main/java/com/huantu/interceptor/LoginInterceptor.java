package com.huantu.interceptor;

import com.huantu.common.Result;
import com.huantu.common.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

/**
 * 登录拦截器：校验 Session 是否有效
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoginInterceptor.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // GET 请求的公开接口无需登录
        String path = request.getRequestURI();
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            if (path.startsWith("/api/post") || path.startsWith("/api/explore")) {
                return true;
            }
        }

        // 从 Header 或 Cookie 中获取 sessionId
        String sessionId = request.getHeader("X-Session-Id");
        if (sessionId == null || sessionId.isEmpty()) {
            // 尝试从 Cookie 获取
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

        // 未提供 sessionId
        if (sessionId == null || sessionId.isEmpty()) {
            writeUnauthorized(response, "未登录");
            return false;
        }

        // 校验 Redis 中的 Session
        String sessionKey = "session:" + sessionId;
        Object userId = redisTemplate.opsForValue().get(sessionKey);
        if (userId == null) {
            writeUnauthorized(response, "登录已过期，请重新登录");
            return false;
        }

        // 续期 Session（7天）
        redisTemplate.expire(sessionKey, 7, TimeUnit.DAYS);

        // 将 userId 存入 request 属性，方便后续使用
        request.setAttribute("userId", userId);
        return true;
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> result = Result.error(ResultCode.UNAUTHORIZED, message);
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(result));
    }
}
