package com.haochuan.hciptvbasic.webview;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.haochuan.core.Logger;
import com.haochuan.core.util.JsUtil;

public class PayToJS {
    private Context context;                        //MainActivity 句柄
    private WebView webView;

    /**
     * 将SDK初始化结果传递给js
     * 参数：0，成功；-1，失败
     */
    private String JS_EVENT_SDK_INIT_RESULT = "javascript:onSDKInitResult(%s)";

    /**
     * 将SDK鉴权结果传递给js
     * 参数：json字符串
     */
    private String JS_EVENT_AUTH_RESULT = "javascript:onAuthResult('%s')";


    /*
     * 将计费结果传递给js
     * 参数：json字符串
     * */
    private String JS_EVENT_PAY_RESULT = "javascript:onPayResult('%s')";


    public PayToJS(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
    }


    /*---------------------------功能性事件--------------------------------*/

    /*
    * SDK初始化
    * 已去除
    * */
    @JavascriptInterface
    public void sdkInit(String paramsJson){
        //在这里添加SDK初始化逻辑
        Logger.d("sdkInit(),paramsJson:" + paramsJson);
    }

    /*
    * 鉴权
    * */
    @JavascriptInterface
    public void auth(String paramsJson){
        //在这里添加鉴权逻辑
        Logger.d("auth(),paramsJson:" + paramsJson);
    }


    /*
    * 计费
    * */
    @JavascriptInterface
    public void pay(String paramsJson){
        //在这里添加支付逻辑
        Logger.d("pay(),paramsJson:" + paramsJson);
    }

    /*
    * 获取用户ID
    * */
    @JavascriptInterface
    public String getUserId(){
        //这这里添加获取用户ID逻辑
        Logger.d("getUserId()");
        return "";
    }

    /*---------------------------事件函数--------------------------------*/

    /**
     * 将SDK初始化结果传递给js
     * 参数：0，成功；-1，失败
     */
    public void onSDKInitResult(int code){
        Logger.d(String.format("onSDKInitResult(%s)",code));
        JsUtil.evaluateJavascript(context,webView, String.format(JS_EVENT_SDK_INIT_RESULT,code));
    }

    /**
     * 将SDK鉴权结果传递给js
     * 参数：json字符串
     */
    public void onAuthResult(String paramJson){
        Logger.d("onAuthResult(),paramJson:" + paramJson);
        JsUtil.evaluateJavascript(context,webView, String.format(JS_EVENT_SDK_INIT_RESULT,paramJson));
    }

    /*
     * 将计费结果传递给js
     * 参数：json字符串
     * */
    public void onPayResult(String paramJson){
        Logger.d("onPayResult(),paramJson:" + paramJson);
        JsUtil.evaluateJavascript(context,webView, String.format(JS_EVENT_SDK_INIT_RESULT,paramJson));
    }
}
