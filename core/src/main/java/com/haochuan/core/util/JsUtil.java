package com.haochuan.core.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.webkit.WebView;

import com.haochuan.core.Logger;

public class JsUtil {
    /**
     * 调用js事件
     */
    public static void evaluateJavascript(Context context, WebView webView, String script) {
        Logger.d(String.format("JsUtil,evaluateJavascript('%s')", script));
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
