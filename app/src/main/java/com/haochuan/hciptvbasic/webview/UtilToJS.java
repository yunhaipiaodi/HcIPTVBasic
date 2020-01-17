package com.haochuan.hciptvbasic.webview;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.haochuan.core.Logger;
import com.haochuan.core.util.HandlerUtil;
import com.haochuan.hciptvbasic.BaseWebActivity;
import com.haochuan.hciptvbasic.BuildConfig;
import com.haochuan.core.util.JSONUtil;
import com.haochuan.core.util.JsUtil;
import com.haochuan.core.util.MacUtil;
import com.haochuan.core.util.MathUtil;
import com.haochuan.core.util.ToolsUtil;

import org.json.JSONObject;

import static com.haochuan.core.util.MessageCode.EXCEPTION_ERROR;
import static com.haochuan.core.util.MessageCode.SUCCESS;


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



    public UtilToJS(Context context, WebView webView){
        this.context = context;
        this.webView = webView;
        toolsUtil = new ToolsUtil();
    }

    /*------------------------------------功能性函数-----------------------------------------*/

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

    /*
     * 退出app
     * */
    @JavascriptInterface
    public int appExit(){
        try{
            HandlerUtil.runOnUiThread(() -> {
               Activity activity =(Activity)context;
               if(activity instanceof BaseWebActivity){
                   BaseWebActivity baseWebActivity = (BaseWebActivity)activity;
                   baseWebActivity.AppExit();
               }else{
                   android.os.Process.killProcess(android.os.Process.myPid());   //获取PID
                   System.exit(0);
               }
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
        Logger.d("clientWebRequest(),paramsJson" + paramsJson);
        try{
            JSONObject requestParams = new JSONObject(paramsJson);
            String url = JSONUtil.getString(requestParams,"url","");
            String methodStr = JSONUtil.getString(requestParams,"method","1");
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
            String contentType = JSONUtil.getString(requestParams,"content_type","application/json");
            String headJson = JSONUtil.getString(requestParams,"head_json","{}");
            String paramJson = JSONUtil.getString(requestParams,"param_json","{}");
            String ignore = JSONUtil.getString(requestParams,"ignore_result","0");
            boolean ignoreResult = TextUtils.equals(ignore,"1");
            String tag = JSONUtil.getString(requestParams,"tag","");
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
