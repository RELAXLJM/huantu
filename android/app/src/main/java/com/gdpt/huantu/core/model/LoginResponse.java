package com.gdpt.huantu.core.model;

import com.google.gson.annotations.SerializedName;

/**
 * 登录响应（{ "sessionId": "xxx" }）
 */
public class LoginResponse {

    @SerializedName("sessionId")
    private String sessionId;

    public LoginResponse() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
