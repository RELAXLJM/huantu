package com.gdpt.huantu.core.util;

import com.gdpt.huantu.BuildConfig;

/**
 * 全局常量
 */
public class Constants {

    // ==================== API ====================
    public static final String API_BASE_URL = BuildConfig.API_BASE_URL;

    // ==================== 腾讯地图 ====================
    public static final String TENCENT_MAP_KEY = BuildConfig.TENCENT_MAP_KEY;

    // ==================== SharedPreferences ====================
    public static final String PREF_NAME = "huantu_prefs";
    public static final String KEY_SESSION_ID = "session_id";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_NICKNAME = "user_nickname";
    public static final String KEY_USER_AVATAR = "user_avatar";
    public static final String KEY_CURRENT_CITY = "current_city";
    public static final String KEY_CURRENT_CITY_CODE = "current_city_code";

    // ==================== 帖子互动类型 ====================
    public static final int INTERACT_TYPE_LIKE = 1;
    public static final int INTERACT_TYPE_COLLECT = 2;
    public static final int INTERACT_TYPE_USEFUL = 3;

    // ==================== 收藏目标类型 ====================
    public static final int FAVORITE_TYPE_ROUTE = 1;
    public static final int FAVORITE_TYPE_POST = 2;
    public static final int FAVORITE_TYPE_SCENIC = 3;

    // ==================== 路线状态 ====================
    public static final int ROUTE_STATUS_DRAFT = 0;
    public static final int ROUTE_STATUS_SAVED = 1;
    public static final int ROUTE_STATUS_FINISHED = 2;

    // ==================== 同行人类型 ====================
    public static final String COMPANION_SOLO = "solo";
    public static final String COMPANION_COUPLE = "couple";
    public static final String COMPANION_FAMILY = "family";
    public static final String COMPANION_ELDERLY = "elderly";
    public static final String COMPANION_PET = "pet";
}
