package com.haochuan.weilai_video.store;

import android.content.Context;
import android.content.SharedPreferences;

import java.nio.file.FileAlreadyExistsException;


/**
 * Created by yunhaipiaodi on 2017/9/26.
 */
@SuppressWarnings("unused")
public class LocalStore {

    private static LocalStore instance;
    private String shareName = "cntv";


    private String HAS_AD = "hasAd";
    private String KEY_OPEN_AD = "openAdJson";
    private String AD_PLAY_TIME = "adPlayTime";
    private String AD_IMAGE_PATH = "adImagePath";


    public static LocalStore getInstance() {
        if (instance == null) {
            instance = new LocalStore();
        }
        return instance;
    }

    public void putHasAd(Context context,boolean hasAd){
        context.getSharedPreferences(shareName,Context.MODE_PRIVATE)
                .edit()
                .putBoolean(HAS_AD,hasAd)
                .commit();
    }

    public boolean getHasAd(Context context){
        return context.getSharedPreferences(shareName,Context.MODE_PRIVATE)
                .getBoolean(HAS_AD, false);
    }

    public void putOpenAdJson(Context context,String openAdJson) {
        context.getSharedPreferences(shareName,Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_OPEN_AD,openAdJson)
                .commit();
    }

    public String getOpenAdJson(Context context) {
        return context.getSharedPreferences(shareName,Context.MODE_PRIVATE)
                .getString(KEY_OPEN_AD, "");
    }

    public void putAdPlayTime(Context context,int adPlayTime) {
        context.getSharedPreferences(shareName,Context.MODE_PRIVATE)
                .edit()
                .putInt(AD_PLAY_TIME,adPlayTime)
                .commit();
    }

    public int getAdPlayTime(Context context) {
        return context.getSharedPreferences(shareName,Context.MODE_PRIVATE)
                .getInt(AD_PLAY_TIME, 0);
    }

    public void putAdImagePath(Context context,String adImagePath) {
        context.getSharedPreferences(shareName,Context.MODE_PRIVATE)
                .edit()
                .putString(AD_IMAGE_PATH,adImagePath)
                .commit();
    }

    public String getAdImagePath(Context context) {
        return context.getSharedPreferences(shareName,Context.MODE_PRIVATE)
                .getString(AD_IMAGE_PATH, "");
    }


}
