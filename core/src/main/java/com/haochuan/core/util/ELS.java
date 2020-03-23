package com.haochuan.core.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashSet;
import java.util.Set;

//sharedPreference封装类
public class ELS {
    private static final String ELS = "EL_SharePreference";
    private static ELS mPref = null;
    private SharedPreferences mSharePrefer = null;
    private Editor mEditor = null;

    //日志文件名称
    public static final String LOG_FILE_NAME = "log_file_name";
    //上一次使用应用的日志文件上传开关
    public static final String LAST_LOG_SWITCH = "last_log_switch";
    //上一次日志文件的名称

    public static ELS getInstance(Context mContext) {
        if (mPref == null)
            synchronized (ELS.class) {
                if (mPref == null)
                    mPref = new ELS(mContext);
            }
        return mPref;
    }

    private ELS(Context context) {
        mSharePrefer = context.getSharedPreferences(ELS, Context.MODE_PRIVATE);
        mEditor = mSharePrefer.edit();
    }

    public void saveLogInfo() {
        mEditor.putBoolean(LAST_LOG_SWITCH, true);
        mEditor.apply();
    }

    public void saveLongDate(String key, long value) {
        mEditor.putLong(key, value);
        mEditor.apply();
    }

    public long getLongDate(String key) {
        return mSharePrefer.getLong(key, 0);
    }

    public void saveBoolData(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.apply();
    }

    public boolean getBoolData(String key) {
        return mSharePrefer.getBoolean(key, false);
    }

    public void saveCookieSet(String key, HashSet<String> set) {
        mEditor.putStringSet(key, set);
        mEditor.apply();
    }

    public Set<String> getCookieSet(String key) {
        return mSharePrefer.getStringSet(key, new HashSet<String>());
    }

    public void saveStringData(String key, String value) {
        mEditor.putString(key, value);
        mEditor.apply();
    }

    public String getStringData(String key) {
        return mSharePrefer.getString(key, "");
    }

    public void saveIntData(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.apply();
    }

    public int getIntData(String key, int defaultValue) {
        return mSharePrefer.getInt(key, defaultValue);
    }

    public int getIntData(String key) {
        return mSharePrefer.getInt(key, 0);
    }

    public void saveFloatData(String key, float value) {
        mEditor.putFloat(key, value);
        mEditor.apply();
    }

    public float getFloatData(String key) {
        return mSharePrefer.getFloat(key, 0);
    }

    /**
     * 清空 SharedPreferences
     */
    public void clear() {
        mEditor.clear();
        mEditor.apply();
    }

}