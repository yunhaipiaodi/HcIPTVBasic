package com.haochuan.hciptvbasic.Util;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.haochuan.hciptvbasic.BaseWebActivity;
import com.haochuan.hciptvbasic.BuildConfig;
import com.haochuan.hciptvbasic.MainActivity;

public class Logger {
    private static String TAG = "HcIPTV";
    private static Context context;

    public static void init(Context appContext){
        context = appContext;
    }

    public static void d(@NonNull String message){
        message = "调试 " + message;
        if(BuildConfig.isDebug){
            Log.d(TAG,message);
        }
        messageToJs(message);
    }

    public static void w(@NonNull String message){
        message = "警告！ " + message;
        if(BuildConfig.isDebug){
            Log.w(TAG,message);
        }
        messageToJs(message);
    }

    public static void e(@NonNull String message){
        message = "错误！ " + message;
        if(BuildConfig.isDebug){
            Log.e(TAG,message);
        }
        messageToJs(message);
    }


    private static void messageToJs(@NonNull String message){
        //将日志传给MainActivity,然后传给js
        if(context instanceof MainActivity){
            BaseWebActivity baseWebActivity = (BaseWebActivity) context;
            baseWebActivity.loggerToJs(message);
        }
    }

}
