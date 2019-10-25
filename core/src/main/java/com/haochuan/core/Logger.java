package com.haochuan.core;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.NonNull;

public class Logger {
    private static String TAG = "HcIPTV";
    private static Context context;
    private static WebView webView;

    public static void init(Context appContext,WebView mWebView){
        context = appContext;
        webView = mWebView;
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

    public static void e(@NonNull int code, @NonNull String message){
        message = String.format("错误代码: %s,错误信息：%s",code,message);
        if(BuildConfig.isDebug){
            Log.e(TAG,message);
        }
        messageToJs(message);
    }


    private static void messageToJs(@NonNull String message){
        //将日志传给MainActivity,然后传给js

    }


    public static void evaluateJavascript(String script) {
        if(context == null){
            Log.e(TAG,"context is null,can`t execute evaluateJavascript");
            return;
        }
        if(webView == null){
            Log.e(TAG,"webView is null,can`t execute evaluateJavascript");
            return;
        }
        Activity activity = (Activity)context;
        if (webView == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.runOnUiThread(()-> webView.evaluateJavascript(script, value -> {
                //此处为 js 返回的结果
            }));
        } else {
            activity.runOnUiThread(()-> webView.loadUrl(script));
        }
    }

}
