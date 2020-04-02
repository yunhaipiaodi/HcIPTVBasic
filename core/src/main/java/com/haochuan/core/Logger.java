package com.haochuan.core;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.haochuan.core.util.ELS;
import com.haochuan.core.util.ToolsUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Logger {
    private static String TAG = "HcIPTV";
    private static Context context;
    private static WebView webView;

    private static Boolean LOG_NEED_WRITE_TO_FILE = false;// 日志写入文件开关
    private static char LOG_TYPE = 'v';// 输入日志类型，w代表只输出告警信息等，v代表输出所有信息

    private static ELS els;

    public static void init(Context appContext, WebView mWebView) {
        context = appContext;
        webView = mWebView;
        els = ELS.getInstance(context);
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        try {
            //日志根目录文件夹
            String pathName = Environment.getExternalStorageDirectory().getCanonicalPath() + "/"
                    + ToolsUtil.getAppProcessName(context);
            File dir = new File(pathName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            //保证每次使用只生成一个Log文件,利用sp存储文件名,方便后面写入时复用
            String fileName = pathName + "/log_" + getCurrentDate() + ".txt";
            els.saveStringData(ELS.LOG_FILE_NAME, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void e(@NonNull int code, @NonNull String message) {
        message = String.format("错误代码: %s,错误信息：%s", code, message);
        log(message, 'e');
    }

    public static void e(@NonNull String message) {
        message = "错误！ " + message;
        log(message, 'e');
    }

    public static void w(@NonNull String message) {
        message = "警告！ " + message;
        log(message, 'w');
    }

    public static void d(@NonNull String message) {
        message = "调试 " + message;
        log(message, 'd');
    }

    public static void i(@NonNull String message) {
        log(message, 'i');
    }

    public static void v(@NonNull String message) {
        log(message, 'v');
    }

    //根据tag, msg和等级，输出日志
    private static void log(String message, char level) {
        if (BuildConfig.isDebug) {//日志文件总开关
            if ('e' == level && ('e' == LOG_TYPE || 'v' == LOG_TYPE)) {
                Log.e(TAG, message);
            } else if ('w' == level && ('w' == LOG_TYPE || 'v' == LOG_TYPE)) {
                Log.w(TAG, message);
            } else if ('i' == level && ('i' == LOG_TYPE || 'v' == LOG_TYPE)) {
                Log.i(TAG, message);
            } else if ('d' == level && ('d' == LOG_TYPE || 'v' == LOG_TYPE)) {
                Log.d(TAG, message);
            } else {
                Log.v(TAG, message);
            }
            if (LOG_NEED_WRITE_TO_FILE)
                writeLogToFile(String.valueOf(level), TAG, message);
        }
        messageToJs(message);
    }

    private static void messageToJs(@NonNull String message) {
        //将日志传给MainActivity,然后传给js
    }

    public static void evaluateJavascript(String script) {
        if (context == null) {
            Log.e(TAG, "context is null,can`t execute evaluateJavascript");
            return;
        }
        if (webView == null) {
            Log.e(TAG, "webView is null,can`t execute evaluateJavascript");
            return;
        }
        Activity activity = (Activity) context;
        if (webView == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.runOnUiThread(() -> webView.evaluateJavascript(script, value -> {
                //此处为 js 返回的结果
            }));
        } else {
            activity.runOnUiThread(() -> webView.loadUrl(script));
        }
    }

    //打开日志文件并写入日志
    private static void writeLogToFile(String logType, String tag, String text) {
        FileOutputStream fos = null;
        try {
            //拼接需要写入的文本
            String logStr = getCurrentDate() + " /" + logType + " /" + tag + ": " + text + "\n";
            //第二个参数表示接着之前的文本写入,不会覆盖
            fos = new FileOutputStream(els.getStringData(ELS.LOG_FILE_NAME), true);
            fos.write(logStr.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getCurrentDate() {
        //获取当前格式化的日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(System.currentTimeMillis());
    }

    public static void setLogNeedWriteToFile(Boolean logNeedWriteToFile) {
        LOG_NEED_WRITE_TO_FILE = logNeedWriteToFile;
    }

    public static Boolean getLogNeedWriteToFile() {
        return LOG_NEED_WRITE_TO_FILE;
    }
}
