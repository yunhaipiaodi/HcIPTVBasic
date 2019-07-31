package com.haochuan.hciptvbasic.webview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.TypedValue;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;

import com.haochuan.hciptvbasic.BuildConfig;
import com.haochuan.hciptvbasic.Util.DownloadUtils;
import com.haochuan.hciptvbasic.Util.JsUtil;
import com.haochuan.hciptvbasic.Util.Logger;
import com.haochuan.hciptvbasic.Util.MacUtil;
import com.haochuan.hciptvbasic.Util.Md5Util;
import com.haochuan.hciptvbasic.Util.ToolsUtil;

import java.io.File;


public class ToolToJS {
    private Context context;                        //MainActivity 句柄
    private WebView webView;
    private ToolsUtil toolsUtil;

    //将遥控返回按键事件传递给前端
    String JS_EVENT_BACK = "javascript:onBackEvent()";

    //将日志传递给js
    String JS_EVENT_LOG = "javascript:onLog('%s')";

    //将response传递给js
    String JS_EVENT_RESPONSE ="javascript:onWebRequestResponse('%s','%s')";

    //开始下载事件
    String JS_EVENT_DOWNLOAD_START = "javascript:onDownloadStart()";

    //下载进度通知,参数progress,下载进度
    String JS_EVENT_DOWNLOAD_PROGRESS = "javascript:onDownloadProgress(%s)";

    //下载成功事件，参数filePath,下载路径
    String JS_EVENT_DOWNLOAD_SUCCESS = "javascript:onDownloadSuccess('%s')";

    //下载失败事件，参数errorMessage,错误信息
    String JS_EVENT_DOWNLOAD_FAIL = "javascript:onDownloadFail('%s')";


    public ToolToJS(Context context, WebView webView){
        this.context = context;
        this.webView = webView;
        toolsUtil = new ToolsUtil();
    }

    /*------------------------------------功能性函数-----------------------------------------*/
    /*---------------------------------------------------------------------------------------*/

    /*
     * 将log传递给前端
     * */
    public void logToJs(String log){
        JsUtil.evaluateJavascript(context,webView,
                String.format(JS_EVENT_LOG,log));
    }

    /*
     * webView对象获取"返回"按键事件
     * */
    public void onBackPressed(){
        JsUtil.evaluateJavascript(context,webView, JS_EVENT_BACK);
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

    /*
    * 获取intent启动参数
    * */
    @JavascriptInterface
    public String getIntentJson(){
        return toolsUtil.getIntentJson(context);
    }

    /*-----------------------------操作APK-------------------------------------*/

    /**
     * 判定是否安装第三方应用
     * packageName,包名
     * 返回 0,安装；-1，未安装
     * **/
    @JavascriptInterface
    public int checkAppInstalled(String packageName){
        return toolsUtil.checkSubAppInstalled(context,packageName)?1:-1;
    }


    /*
    * 下载
    * */
    @JavascriptInterface
    public void download(String downloadUrl){
        DownloadUtils.download(downloadUrl, context.getPackageName() + getVersionCode(), "apk", new DownloadUtils.DownloadProgressListener() {
            @Override
            public void onDownloadStart(String fileName) {
                JsUtil.evaluateJavascript(context,webView,JS_EVENT_DOWNLOAD_START);
            }

            @Override
            public void onDownloadProgress(int progress) {
                JsUtil.evaluateJavascript(context,webView,
                        String.format(JS_EVENT_DOWNLOAD_START,progress));
            }

            @Override
            public void onDownloadSuccessful(String filePath) {
                JsUtil.evaluateJavascript(context,webView,
                        String.format(JS_EVENT_DOWNLOAD_SUCCESS,filePath));
            }

            @Override
            public void onDownloadFail(String message){
                JsUtil.evaluateJavascript(context,webView,
                        String.format(JS_EVENT_DOWNLOAD_FAIL,message));
            }
        });
    }

    /*
    * 获得下载文件MD5值
    * */
    @JavascriptInterface
    public String getMD5(String filePath){
        try{
            return Md5Util.getFileMD5(new File(filePath));
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 安装app
     * @param apkPath 本地安装包路径
     */
    @JavascriptInterface
    public void install(String apkPath) {
        ((Activity) context).runOnUiThread(() -> toolsUtil.installApk(context,apkPath));
    }



    /**
     * 卸载app
     */
    @JavascriptInterface
    public void uninstall(String pkgName) {
        ((Activity) context).runOnUiThread(() -> toolsUtil.uninstall(context,pkgName));
    }

    /*---------------------------通过客户端请求接口------------------------*/

    /*
     *js 通过apk客户端访问网络接口
     *@param paramJson 请求参数集,格式为json字符串
     *@param headJson 请求头部集，格式为json字符串
     *@param method 请求方法，1,get;2,post
     *@param ignoreResult 是否忽略结果,true,忽略;false,不忽略.
     *@param tag 透传参数，将在结果回调中一并返回，主要区别多个并发请求
     * */
    @JavascriptInterface
    public void clientWebRequest(String url,String paramJson,String headJson,int method,boolean ignoreResult,String tag){
        toolsUtil.clientWebRequest(context, url, paramJson, headJson, method, ignoreResult, tag,
                (int what,String response,String tag1)->{
                        if(what == 0){
                            Logger.d(String.format("response:%s;tag:%s",response,tag1));
                            JsUtil.evaluateJavascript(context,webView,
                                    String.format(JS_EVENT_RESPONSE,response,tag1));
                        }
                });
    }

}
