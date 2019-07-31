package com.haochuan.hciptvbasic.webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.annotation.StringDef;

import com.haochuan.hciptvbasic.BuildConfig;
import com.haochuan.hciptvbasic.Util.Logger;
import com.haochuan.hciptvbasic.Util.MacUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.haochuan.hciptvbasic.webview.ToolToJS.JsEvent.JS_EVENT_BACK;


public class ToolToJS {
    private Context context;                        //MainActivity 句柄
    private WebView webView;

    public ToolToJS(Context context, WebView webView){
        this.context = context;
        this.webView = webView;
    }

    /**
     * JS调用类型
     */
    @StringDef({JS_EVENT_BACK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface JsEvent{

        /**
         * 将遥控返回按键传递给js
         */
        String JS_EVENT_BACK = "javascript:onBackEvent()";

    }

    /*------------------------------------功能性函数-----------------------------------------*/
    /*---------------------------------------------------------------------------------------*/

    /**
     * 调用js事件
     */
    private void evaluateJavascript(WebView webView, @ToolToJS.JsEvent String script) {
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

    /*
     * webView对象获取"返回"按键事件
     * */
    public void onBackPressed(){
        evaluateJavascript(webView, JS_EVENT_BACK);
    }

    /*---------------------------------获取本地参数--------------------------*/
    /**
     * 当前app版本号
     */
    @JavascriptInterface
    public int getVersionCode() {
        return BuildConfig.VERSION_CODE;
    }

    /**
     * 当前app版本名
     */
    @JavascriptInterface
    public String getVersionName() {
        return BuildConfig.VERSION_NAME;
    }


    /**
     * 获取Mac地址
     */
    @JavascriptInterface
    public String getMac() {
        return MacUtil.getMac(context);
    }

    /*-----------------------------操作APK-------------------------------------*/

    /*
    * 查看目标包名app是否安装
    * */
    @JavascriptInterface
    public void checkInstall(String pkgName) {
        //((Activity) context).runOnUiThread(() -> installApk(apkPath));
    }

    /**
     * 安装app
     * @param apkPath 本地安装包路径
     */
    @JavascriptInterface
    public void install(String apkPath) {
        ((Activity) context).runOnUiThread(() -> installApk(apkPath));
    }

    private void installApk(String filePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//4.0以上系统弹出安装成功打开界面
        context.startActivity(intent);
    }

    /**
     * 卸载app
     */
    @JavascriptInterface
    public void uninstall() {
        ((Activity) context).runOnUiThread(() -> {
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            Intent intent = new Intent(Intent.ACTION_DELETE, uri);
            context.startActivity(intent);
        });
    }
}
