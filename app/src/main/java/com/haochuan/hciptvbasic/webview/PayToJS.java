package com.haochuan.hciptvbasic.webview;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.annotation.StringDef;

import com.haochuan.hciptvbasic.Util.Logger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.haochuan.hciptvbasic.webview.PayToJS.JsEvent.JS_EVENT_AUTH_RESULT;
import static com.haochuan.hciptvbasic.webview.PayToJS.JsEvent.JS_EVENT_PAY_RESULT;
import static com.haochuan.hciptvbasic.webview.PayToJS.JsEvent.JS_EVENT_SDK_INIT_RESULT;

public class PayToJS {
    private Context context;                        //MainActivity 句柄
    private WebView webView;

    public PayToJS(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
    }

    /**
     * JS调用类型
     */
    @StringDef({JS_EVENT_SDK_INIT_RESULT,JS_EVENT_AUTH_RESULT,JS_EVENT_PAY_RESULT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface JsEvent{

        /**
         * 将SDK初始化结果传递给js
         * 参数：0，成功；-1，失败
         */
        String JS_EVENT_SDK_INIT_RESULT = "javascript:onSDKInitResult(%s)";

        /**
         * 将SDK鉴权结果传递给js
         * 参数：json字符串
         */
        String JS_EVENT_AUTH_RESULT = "javascript:onAuthResult('%s')";


        /*
        * 将计费结果传递给js
        * 参数：json字符串
        * */
        String JS_EVENT_PAY_RESULT = "javascript:onPayResult('%s')";


    }

    /*--------------------功能性函数-----------------------------*/
    /**
     * 调用js事件
     */
    private void evaluateJavascript(WebView webView, @PayToJS.JsEvent String script) {
        Logger.show(context,"ToolToJS 执行脚本：" + script);
        if (webView == null) {
            Logger.show(context,"webView对象为空，JS事件调用无法执行");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(script, value -> {
                //此处为 js 返回的结果
                Logger.show(context,value);
            });
        } else {
            webView.loadUrl(script);
        }
    }

    /*---------------------------功能性事件--------------------------------*/

    /*
    * SDK初始化
    * */
    @JavascriptInterface
    public void sdkInit(String paramsJson){
        //在这里添加SDK初始化逻辑
    }

    /*
    * 鉴权
    * */
    @JavascriptInterface
    public void auth(String paramsJson){
        //在这里添加鉴权逻辑
    }


    /*
    * 计费
    * */
    @JavascriptInterface
    public void pay(String paramsJson){
        //在这里添加支付逻辑

    }

    /*
    * 获取用户ID
    * */
    @JavascriptInterface
    public String getUserId(){
        //这这里添加获取用户ID逻辑

        return "";
    }

    /*---------------------------事件函数--------------------------------*/

    /**
     * 将SDK初始化结果传递给js
     * 参数：0，成功；-1，失败
     */
    public void onSDKInitResult(int code){
        evaluateJavascript(webView, String.format(JS_EVENT_SDK_INIT_RESULT,code));
    }

    /**
     * 将SDK鉴权结果传递给js
     * 参数：json字符串
     */
    public void onAuthResult(String paramJson){
        evaluateJavascript(webView, String.format(JS_EVENT_SDK_INIT_RESULT,paramJson));
    }

    /*
     * 将计费结果传递给js
     * 参数：json字符串
     * */
    public void onPayResult(String paramJson){
        evaluateJavascript(webView, String.format(JS_EVENT_SDK_INIT_RESULT,paramJson));
    }
}
