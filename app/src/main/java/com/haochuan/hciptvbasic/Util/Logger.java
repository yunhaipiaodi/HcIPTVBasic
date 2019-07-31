package com.haochuan.hciptvbasic.Util;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.haochuan.hciptvbasic.BuildConfig;
import com.haochuan.hciptvbasic.MainActivity;

public class Logger {
    static String  TAG = "HcIPTV";


    public static void show(@NonNull Context context, @NonNull String message){
        if(BuildConfig.isDebug){
            Log.d(TAG,message);
        }
        //将日志传给MainActivity,然后传给js
        if(context instanceof MainActivity){
            MainActivity mainActivity = (MainActivity)context;
            mainActivity.loggerToJs(message);
        }
    }


}
