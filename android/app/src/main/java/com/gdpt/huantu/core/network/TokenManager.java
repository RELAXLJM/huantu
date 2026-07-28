package com.gdpt.huantu.core.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.gdpt.huantu.core.util.Constants;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * Session Token 管理器（单例）
 * 负责读写 SharedPreferences 中的 sessionId
 */
@Singleton
public class TokenManager {

    private final SharedPreferences prefs;

    @Inject
    public TokenManager(@ApplicationContext Context context) {
        this.prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 保存 SessionId
     */
    public void saveSessionId(String sessionId) {
        prefs.edit().putString(Constants.KEY_SESSION_ID, sessionId).apply();
    }

    /**
     * 获取 SessionId
     */
    public String getSessionId() {
        return prefs.getString(Constants.KEY_SESSION_ID, null);
    }

    /**
     * 清除 Session（退出登录）
     */
    public void clearSession() {
        prefs.edit().remove(Constants.KEY_SESSION_ID).apply();
    }

    /**
     * 是否已登录
     */
    public boolean isLoggedIn() {
        return getSessionId() != null && !getSessionId().isEmpty();
    }

    /**
     * 保存当前用户信息
     */
    public void saveUserInfo(long userId, String nickname, String avatarUrl) {
        prefs.edit()
                .putLong(Constants.KEY_USER_ID, userId)
                .putString(Constants.KEY_USER_NICKNAME, nickname)
                .putString(Constants.KEY_USER_AVATAR, avatarUrl)
                .apply();
    }

    public long getUserId() {
        return prefs.getLong(Constants.KEY_USER_ID, 0);
    }

    public String getUserNickname() {
        return prefs.getString(Constants.KEY_USER_NICKNAME, "");
    }

    public String getUserAvatar() {
        return prefs.getString(Constants.KEY_USER_AVATAR, "");
    }

    /**
     * 保存城市信息
     */
    public void saveCityInfo(String city, String cityCode) {
        prefs.edit()
                .putString(Constants.KEY_CURRENT_CITY, city)
                .putString(Constants.KEY_CURRENT_CITY_CODE, cityCode)
                .apply();
    }

    public String getCurrentCity() {
        return prefs.getString(Constants.KEY_CURRENT_CITY, "定位中...");
    }

    public String getCurrentCityCode() {
        return prefs.getString(Constants.KEY_CURRENT_CITY_CODE, "");
    }
}
