package com.haochuan.core.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;

import com.haochuan.core.Logger;
import com.haochuan.core.http.MyRequest;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.StringRequest;

import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ToolsUtil {

    public boolean checkSubAppInstalled(Context context, String pkgName) {
        if (context == null) {
            Logger.e("checkSubAppInstalled() context is null,不能执行");
            return false;
        }
        if (pkgName == null || pkgName.isEmpty()) {
            return false;
        }
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    public static void installApk(Context context, String filePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//4.0以上系统弹出安装成功打开界面
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public void uninstall(Context context, String pkgName) {
        Uri uri = Uri.fromParts("package", pkgName, null);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        context.startActivity(intent);
    }

    /*
     * 获得intent参数集
     * */
    public String getIntentJson(Context context) {
        try {
            Activity activity = (Activity) context;
            Intent intent = activity.getIntent();
            if (intent != null) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Set<String> keySet = bundle.keySet();
                    JSONObject intentJson = new JSONObject();
                    for (String key : keySet) {
                        Object bundleValue = bundle.get(key);
                        intentJson.put(key, bundleValue);
                    }
                    String getIntentJson = intentJson.toString();
                    Logger.d("getIntentJson:" + getIntentJson);
                    return getIntentJson;
                } else {
                    Logger.d("getIntentJson, bundle is null");
                    return "{}";
                }
            } else {
                Logger.d("getIntentJson, intent is null");
                return "{}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    /*
     *js 通过apk客户端访问网络接口
     *@param paramJson 请求参数集,格式为json字符串
     *@param headJson 请求头部集，格式为json字符串
     *@param method 请求方法，1,get;2,post
     *@param ignoreResult 是否忽略结果,true,忽略;false,不忽略.
     *@param tag 透传参数，将在结果回调中一并返回，主要区别多个并发请求
     * */
    public void clientWebRequest(String url, int method, String paramJson,
                                 String headJson, boolean ignoreResult,
                                 String tag, IResponseListener listener) {
        if (url == null || paramJson == null || headJson == null || tag == null) {
            Logger.w(String.format("参数不能为null,url:%s;paramJson:%s;headJson:%s;" +
                    "method:%s;ignoreResult:%s;tag:%s", url, paramJson, headJson, method, ignoreResult ? "忽略结果" : "不忽略结果", tag));
            return;
        }
        try {
            RequestMethod requestMethod;
            switch (method) {
                case 1:
                    requestMethod = RequestMethod.GET;
                    break;
                case 2:
                    requestMethod = RequestMethod.POST;
                    break;
                default:
                    requestMethod = RequestMethod.GET;
                    break;
            }
            if (url.isEmpty()) {
                Logger.w("clientWebRequest，url 不能为空");
                return;
            }
            final StringRequest request = new StringRequest(url, requestMethod);
            if (!paramJson.isEmpty()) {
                JSONObject jsonParams = new JSONObject(paramJson);
                Iterator<String> paramIterators = jsonParams.keys();
                while (paramIterators.hasNext()) {
                    String paramKey = paramIterators.next();
                    String paramValue = jsonParams.getString(paramKey);
                    request.add(paramKey, paramValue);
                }
            }
            if (!headJson.isEmpty()) {
                JSONObject headParams = new JSONObject(headJson);
                Iterator<String> headIterators = headParams.keys();
                while (headIterators.hasNext()) {
                    String paramKey = headIterators.next();
                    String paramValue = headParams.getString(paramKey);
                    request.addHeader(paramKey, paramValue);
                }
            }

            NoHttp.newRequestQueue().add(100, request, new OnResponseListener<String>() {
                @Override
                public void onStart(int what) {
                    Logger.d("clientWebRequest，开始请求");
                }

                @Override
                public void onSucceed(int what, com.yanzhenjie.nohttp.rest.Response<String> response) {
                    String data = response.get();
                    Logger.d("clientWebRequest，请求认证成功!");
                    String base64Response = Base64.encodeToString(data.getBytes(), Base64.NO_WRAP);
                    base64Response = base64Response.replace("\n", "");
                    if (ignoreResult) {
                        listener.OnResponse(0, "{}", tag);
                    } else {
                        listener.OnResponse(0, base64Response, tag);
                    }
                }

                @Override
                public void onFailed(int what, com.yanzhenjie.nohttp.rest.Response<String> response) {
                    Logger.w("clientWebRequest，请求认证失败：" + what);
                    listener.OnResponse(-1, "{}", tag);
                }

                @Override
                public void onFinish(int what) {
                    Logger.d("clientWebRequest，请求认证结束");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void clientWebRequest(String url, int method,
                                 String contentType, String headJson,
                                 String paramsBody, boolean ignoreResult,
                                 String tag, IResponseListener listener) {
        try {
            Logger.d(String.format("webRequest,url:%s,method:%s,contentType:%s," +
                            "headJson:%s,paramsBody:%s,tag:%s,ignoreResult%s,",
                    url, method, contentType, headJson, paramsBody, tag, ignoreResult ? "忽略" : "不忽略"));
            RequestMethod requestMethod;
            switch (method) {
                case 1:
                    requestMethod = RequestMethod.GET;
                    break;
                case 2:
                    requestMethod = RequestMethod.POST;
                    break;
                default:
                    requestMethod = RequestMethod.GET;
                    break;
            }
            Request<String> request = new MyRequest(url, requestMethod);

            //添加头部
            if (!TextUtils.isEmpty(headJson)) {
                JSONObject headParams = new JSONObject(headJson);
                Iterator<String> headIterators = headParams.keys();
                while (headIterators.hasNext()) {
                    String paramKey = headIterators.next();
                    String paramValue = headParams.getString(paramKey);
                    request.addHeader(paramKey, paramValue);
                }
            }

            if (TextUtils.equals(contentType, "application/x-www-form-urlencoded")) {
                JSONObject jsonObject = new JSONObject(paramsBody);
                Iterator iterator = jsonObject.keys();
                StringBuilder sb = new StringBuilder();
                int i = 0;
                while (iterator.hasNext()) {
                    if (i > 0) {
                        sb.append("&");
                    }
                    String key = (String) iterator.next();
                    String value = jsonObject.getString(key);
                    value = URLEncoder.encode(value, "UTF-8");
                    sb.append(String.format("%s=%s", key, value));
                    i++;
                }
                paramsBody = sb.toString();
            }

            if (method == 2 && !TextUtils.isEmpty(paramsBody)) {
                request.setDefineRequestBody(paramsBody, contentType);
            }
            NoHttp.newRequestQueue().add(100, request, new OnResponseListener<String>() {
                @Override
                public void onStart(int what) {
                    Logger.d("clientWebRequest，开始请求");
                }

                @Override
                public void onSucceed(int what, com.yanzhenjie.nohttp.rest.Response<String> response) {
                    String data = response.get();
                    String result = ignoreResult ? "{}" : data.replace("\"", "\\\"");
                    result = result.replace("\n", "");
                    String base64Response = Base64.encodeToString(data.getBytes(), Base64.NO_WRAP);
                    base64Response = base64Response.replace("\n", "");
                    if (ignoreResult) {
                        listener.OnResponse(0, "{}", tag);
                    } else {
                        listener.OnResponse(0, base64Response, tag);
                    }
                }

                @Override
                public void onFailed(int what, com.yanzhenjie.nohttp.rest.Response<String> response) {
                    Logger.w("clientWebRequest，请求认证失败：" + what);
                }

                @Override
                public void onFinish(int what) {
                    Logger.d("clientWebRequest，请求认证结束");
                }
            });
        } catch (Exception e) {
            Logger.e("JS传递的请求发生未知异常");
            e.printStackTrace();
        }
    }

    public static int getVersionCode(Context mContext) {
        int versionName = 0;
        PackageManager manager = mContext.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            versionName = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    //获取剩余可用内存
    public static long getAvailableMemory(Context mContext) {
        ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        mActivityManager.getMemoryInfo(mi);
        //返回的内存单位为字节
        return mi.availMem;
    }

    //获取当前应用程序包名
    public static String getAppProcessName(Context context) {
        //因为派生出去的项目都会修改对应的ApplicationId,所以用BuildConfig获取包名是不准的
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//得到当前应用
                return info.processName;//返回包名
        }
        return "";
    }


    /*
     * clientWebRequest 结果response接口
     * */
    public interface IResponseListener {
        public void OnResponse(int code, String response, String tag);
    }
}