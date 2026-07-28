package com.gdpt.huantu.core.util;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * SharedPreferences 工具类
 */
@Singleton
public class PreferenceUtil {

    private final SharedPreferences prefs;

    @Inject
    public PreferenceUtil(Context context) {
        this.prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    // ==================== String ====================

    public void putString(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    public String getString(String key, String defaultValue) {
        return prefs.getString(key, defaultValue);
    }

    // ==================== Int ====================

    public void putInt(String key, int value) {
        prefs.edit().putInt(key, value).apply();
    }

    public int getInt(String key, int defaultValue) {
        return prefs.getInt(key, defaultValue);
    }

    // ==================== Long ====================

    public void putLong(String key, long value) {
        prefs.edit().putLong(key, value).apply();
    }

    public long getLong(String key, long defaultValue) {
        return prefs.getLong(key, defaultValue);
    }

    // ==================== Boolean ====================

    public void putBoolean(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }

    // ==================== 清除 ====================

    public void remove(String key) {
        prefs.edit().remove(key).apply();
    }

    public void clearAll() {
        prefs.edit().clear().apply();
    }
}
