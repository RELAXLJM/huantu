package com.gdpt.huantu.core.network;

import android.content.Intent;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * OkHttp 拦截器：自动注入 X-Session-Id，处理 401 未授权
 */
public class AuthInterceptor implements Interceptor {

    private final TokenManager tokenManager;
    private SessionExpiredListener listener;

    public interface SessionExpiredListener {
        void onSessionExpired();
    }

    public AuthInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    public void setSessionExpiredListener(SessionExpiredListener listener) {
        this.listener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        okhttp3.Request original = chain.request();
        okhttp3.Request.Builder builder = original.newBuilder();

        // 注入 X-Session-Id
        String sessionId = tokenManager.getSessionId();
        if (sessionId != null && !sessionId.isEmpty()) {
            builder.header("X-Session-Id", sessionId);
        }

        // 添加公共Header
        builder.header("Content-Type", "application/json")
                .header("Accept", "application/json");

        Response response = chain.proceed(builder.build());

        // 检测 401 → Session 过期
        if (response.code() == 401) {
            tokenManager.clearSession();
            if (listener != null) {
                listener.onSessionExpired();
            }
        }

        return response;
    }
}
