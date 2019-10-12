package com.haochuan.hciptvbasic.webview;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.haochuan.hciptvbasic.BuildConfig;
import com.haochuan.hciptvbasic.Util.DownloadUtils;
import com.haochuan.hciptvbasic.Util.JsUtil;
import com.haochuan.hciptvbasic.Util.Logger;
import com.haochuan.hciptvbasic.Util.MacUtil;
import com.haochuan.hciptvbasic.Util.MathUtil;
import com.haochuan.hciptvbasic.Util.Md5Util;
import com.haochuan.hciptvbasic.Util.ToolsUtil;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;

import static com.haochuan.hciptvbasic.Util.MessageCode.EXCEPTION_ERROR;
import static com.haochuan.hciptvbasic.Util.MessageCode.PARAM_ERROR;
import static com.haochuan.hciptvbasic.Util.MessageCode.SUCCESS;


public class UtilToJS {
    private Context context;                        //MainActivity 句柄
    private WebView webView;
    private ToolsUtil toolsUtil;

    //将遥控返回按键事件传递给前端
    private String JS_EVENT_BACK = "javascript:onBackEvent()";

    //将日志传递给js
    private String JS_EVENT_LOG = "javascript:onLog('%s')";

    //将response传递给js
    private String JS_EVENT_RESPONSE ="javascript:onWebRequestResponse('%s','%s')";


    //下载事件
    private String JS_EVENT_DOWNLOAD_EVENT = "javascript:onDownloadEvent(%s,'%s')";


    public UtilToJS(Context context, WebView webView){
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

    @JavascriptInterface
    public String getLocalParamsByJson(){
        try{
            JSONObject localParamsJson = new JSONObject();
            localParamsJson.put("version_code",BuildConfig.VERSION_CODE);
            localParamsJson.put("version_name",BuildConfig.VERSION_NAME);
            localParamsJson.put("mac",MacUtil.getMac(context) != null ? MacUtil.getMac(context) : "");
            return localParamsJson.toString();
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    /*-----------------------------操作APK-------------------------------------*/

    /**
     * 判定是否安装第三方应用
     * packageName,包名
     * 返回 0,安装；-1，未安装
     * **/
    @JavascriptInterface
    public int checkAppInstalled(String paramsJson){
        try{
            JSONObject jsonObject = new JSONObject(paramsJson);
            String packageName = jsonObject.has("package_name")?jsonObject.get("package_name").toString():"";
            return toolsUtil.checkSubAppInstalled(context,packageName)?0:-1;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    /*
    * 下载
    * */
    @JavascriptInterface
    public int download(String paramsJson){
        try{
            JSONObject jsonObject = new JSONObject(paramsJson);
            String downloadUrl = jsonObject.has("download_url")?jsonObject.get("download_url").toString():"";
            if(TextUtils.isEmpty(downloadUrl)){
                Logger.e(PARAM_ERROR,"download_url is empty,download stopped");
                return PARAM_ERROR;
            }
            DownloadUtils.download(downloadUrl, context.getPackageName() + getVersionCode(), "apk", new DownloadUtils.DownloadProgressListener() {
                @Override
                public void onDownloadStart(String fileName) {
                    exeDownloadEvent(1,"");
                }

                @Override
                public void onDownloadProgress(int progress) {
                    exeDownloadEvent(2,String.valueOf(progress));
                }

                @Override
                public void onDownloadSuccessful(String filePath) {
                    exeDownloadEvent(3,"");
                }

                @Override
                public void onDownloadFail(String message){
                    exeDownloadEvent(4,message);
                }
            });
            return SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return EXCEPTION_ERROR;
        }
    }

    /*
     * 下载并且安装
     * */
    @JavascriptInterface
    public int downLoadAndInstall(String paramsJson){
        try{
            JSONObject jsonObject = new JSONObject(paramsJson);
            String downloadUrl = jsonObject.has("download_url")?jsonObject.get("download_url").toString():"";
            if(TextUtils.isEmpty(downloadUrl)){
                Logger.e(PARAM_ERROR,"download_url is empty,download stopped");
                return PARAM_ERROR;
            }
            DownloadUtils.download(downloadUrl, context.getPackageName() + getVersionCode(), "apk", new DownloadUtils.DownloadProgressListener() {
                @Override
                public void onDownloadStart(String fileName) {
                    exeDownloadEvent(1,"");
                }

                @Override
                public void onDownloadProgress(int progress) {
                    exeDownloadEvent(2,String.valueOf(progress));
                }

                @Override
                public void onDownloadSuccessful(String filePath) {
                    exeDownloadEvent(3,"");
                    install(String.format("{\"file_path\": \"%s\"}",filePath));
                }

                @Override
                public void onDownloadFail(String message){
                    exeDownloadEvent(4,message);
                }
            });
            return SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return EXCEPTION_ERROR;
        }
    }

    private void exeDownloadEvent(int type,String message){
        JsUtil.evaluateJavascript(context,webView,
                String.format(JS_EVENT_DOWNLOAD_EVENT,type,message));
    }

    /*
    * 获得下载文件MD5值
    * */
    @JavascriptInterface
    public String getMD5(String paramsJson){
        try{
            JSONObject jsonObject = new JSONObject(paramsJson);
            String filePath = jsonObject.has("file_path")?jsonObject.get("file_path").toString():"";
            if(TextUtils.isEmpty(filePath)){
                Logger.e(PARAM_ERROR,"file_path is empty,getMD5 function stopped");
                return "";
            }
            return Md5Util.getFileMD5(new File(filePath));
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 安装app
     */
    @JavascriptInterface
    public int install(String paramsJson) {
        try{
            JSONObject jsonObject = new JSONObject(paramsJson);
            String filePath = jsonObject.has("file_path")?jsonObject.get("file_path").toString():"";
            if(TextUtils.isEmpty(filePath)){
                Logger.e(PARAM_ERROR,"file_path is empty,install function stopped");
                return PARAM_ERROR;
            }
            ((Activity) context).runOnUiThread(() -> toolsUtil.installApk(context,filePath));
            return SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return EXCEPTION_ERROR;
        }
    }

    /**
     * 卸载app
     */
    @JavascriptInterface
    public int uninstall(String paramsJson) {
        try{
            JSONObject jsonObject = new JSONObject(paramsJson);
            String packageName = jsonObject.has("package_name")?jsonObject.get("package_name").toString():"";
            ((Activity) context).runOnUiThread(() -> toolsUtil.uninstall(context,packageName));
            return SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return EXCEPTION_ERROR;
        }
    }

    /*
     * 退出app
     * */
    @JavascriptInterface
    public int appExit(){
        try{
            ((Activity) context).runOnUiThread(() -> {
                android.os.Process.killProcess(android.os.Process.myPid());   //获取PID
                System.exit(0);
            });
            return SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return EXCEPTION_ERROR;
        }
    }

    /*---------------------------通过客户端请求接口------------------------*/



    /*
     *js 通过apk客户端访问网络接口
     *@param contentType 网络类型,详情参考网络请求的content-type
     *@param paramJson 请求参数集,格式为json字符串
     *@param headJson 请求头部集，格式为json字符串
     *@param method 请求方法，1,get;2,post
     *@param ignoreResult 是否忽略结果,true,忽略;false,不忽略.
     *@param tag 透传参数，将在结果回调中一并返回，主要区别多个并发请求
     * */
    @JavascriptInterface
    public int clientWebRequest(String paramsJson){
        try{
            JSONObject requestParams = new JSONObject(paramsJson);
            String url = requestParams.has("url")?requestParams.get("url").toString():"";
            String methodStr = requestParams.has("method")?requestParams.get("method").toString():"1";
            int method = 0;
            if(MathUtil.isDigitsOnly(methodStr)){
                method = Integer.parseInt(methodStr);
                if( method <0  || method > 1 ){
                    Logger.w("clientWebRequest 请求参数method必须为0或者1，目前重置为0");
                    method = 0;
                }
            }else{
                Logger.w("clientWebRequest 请求参数method必须为数字，目前重置为0");
            }
            String contentType = requestParams.has("content_type")?requestParams.get("content_type").toString():"application/json";
            String headJson = requestParams.has("head_json")?requestParams.get("head_json").toString():"{}";
            String paramJson = requestParams.has("param_json")?requestParams.get("param_json").toString():"{}";
            String ignore = requestParams.has("ignore_result")?requestParams.get("ignore_result").toString():"0";
            boolean ignoreResult = TextUtils.equals(ignore,"1");
            String tag = requestParams.has("tag")?requestParams.get("tag").toString():"";
            toolsUtil.clientWebRequest(url, method, contentType, headJson,paramJson, ignoreResult, tag,
                    (int what,String response,String tag1)->{
                        Logger.d(String.format("response:%s;tag:%s",response,tag1));
                        JsUtil.evaluateJavascript(context,webView,
                                String.format(JS_EVENT_RESPONSE,response,tag1));
                    });
            return SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return EXCEPTION_ERROR;
        }
    }

}
