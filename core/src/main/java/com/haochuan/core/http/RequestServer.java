package com.haochuan.core.http;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.haochuan.core.Logger;
import com.haochuan.core.http.bean.ApkSettingBean;
import com.haochuan.core.http.bean.ResponseBean;
import com.haochuan.core.http.bean.UpdateResponseBean;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.error.NetworkError;
import com.yanzhenjie.nohttp.error.TimeoutError;
import com.yanzhenjie.nohttp.error.URLError;
import com.yanzhenjie.nohttp.error.UnKnownHostError;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.SimpleResponseListener;
import com.yanzhenjie.nohttp.rest.StringRequest;

import java.io.File;

/**
 * Created by ncx on 2020/3/10
 * 接口统一请求单例
 */
public class RequestServer {
    public static final String UNKNOW_ERROR = "0x001";
    public static final String EXCEPTION_NETWORK = "0x002";
    public static final String EXCEPTION_CONNECT_TIMEOUT = "0x003";
    public static final String EXCEPTION_HOST = "0x004";
    public static final String EXCEPTION_URL = "0x005";
    public static final String OTHER_MESSAGE = "其它错误";
    public static final String OTHER_MESSAGE_CODE = "0x999";


    //这个IP需要视具体项目修改
    private static final String HOST = "http://sxsj.reading.sdteleiptv.com:6401/";

    private static final String UPDATE_VERSION = HOST + "apk/up_version";

    private static final String UPLOAD_LOG_FILE = HOST + "apk/save_apk_log";

    private static final String GET_APK_SETTING = HOST + "apk/get_apk_setting";

    private static RequestServer instance;

    public static RequestServer getInstance() {
        if (instance == null)
            synchronized (RequestServer.class) {
                if (instance == null)
                    instance = new RequestServer();
            }
        return instance;
    }

    private RequestQueue queue;
    private Gson gson;

    private RequestServer() {
        queue = NoHttp.newRequestQueue();
        gson = new Gson();
    }

    // 完全退出app时，调用这个方法释放CPU。
    public void stop() {
        if (queue != null) {
            queue.stop();
        }
    }

    //请求失败错误码处理
    private String requestFailedCode(Exception exception) {
        String stringRes = UNKNOW_ERROR;
        if (exception instanceof NetworkError) {
            stringRes = EXCEPTION_NETWORK;
        } else if (exception instanceof TimeoutError) {
            stringRes = EXCEPTION_CONNECT_TIMEOUT;
        } else if (exception instanceof UnKnownHostError) {
            stringRes = EXCEPTION_HOST;
        } else if (exception instanceof URLError) {
            stringRes = EXCEPTION_URL;
        }
        return stringRes;
    }

    //请求失败信息处理
    private String requestFailedMessage(Exception exception) {
        String stringRes = "未知错误";
        if (exception instanceof NetworkError) {
            stringRes = "网络不可用,请检查网络";
        } else if (exception instanceof TimeoutError) {
            stringRes = "链接服务器超时";
        } else if (exception instanceof UnKnownHostError) {
            stringRes = "没有找到Url指定的服务器";
        } else if (exception instanceof URLError) {
            stringRes = "Url格式错误";
        }
        return stringRes;
    }

    //获取版本更新
    public void updateVersion(int versionCode, String userId, ResponseListener<UpdateResponseBean> listener) {
        StringRequest request = new StringRequest(UPDATE_VERSION, RequestMethod.GET);
        //当前版本号
        request.add("versionCode", versionCode);
        //userId用于指定用户升级
        request.add("userId", userId);
        queue.add(1, request, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                super.onSucceed(what, response);
                String responseStr = response.get();
                if (!TextUtils.isEmpty(responseStr)) {
                    UpdateResponseBean bean = UpdateResponseBean.objectFromData(responseStr);
                    if (bean.getCode() == 0) {
                        if (bean.getData() != null) {
                            listener.onSuccess(bean);
                        } else {
                            listener.onFailure(OTHER_MESSAGE_CODE, OTHER_MESSAGE);
                        }
                    } else {
                        listener.onFailure(OTHER_MESSAGE_CODE, OTHER_MESSAGE);
                    }
                } else {
                    listener.onFailure(OTHER_MESSAGE_CODE, OTHER_MESSAGE);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                super.onFailed(what, response);
                if (response != null) {
                    String code = requestFailedCode(response.getException());
                    String message = requestFailedMessage(response.getException());
                    Logger.d("updateVersion,onFailed:" + code + "," + message);
                    listener.onFailure(code, message);
                }
            }
        });
    }

    //查询是否需要上传日志文件
    public void getApkSetting(String uid, ResponseListener<ApkSettingBean> listener) {
        StringRequest request = new StringRequest(GET_APK_SETTING, RequestMethod.GET);
        request.add("uid", uid);
        queue.add(2, request, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                super.onSucceed(what, response);
                String responseStr = response.get();
                if (!TextUtils.isEmpty(responseStr)) {
                    ApkSettingBean bean = ApkSettingBean.objectFromData(responseStr);
                    if (bean.getCode() == 0) {
                        listener.onSuccess(bean);
                    } else {
                        listener.onFailure(OTHER_MESSAGE_CODE, OTHER_MESSAGE);
                    }
                } else {
                    listener.onFailure(OTHER_MESSAGE_CODE, OTHER_MESSAGE);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                super.onFailed(what, response);
                if (response != null) {
                    String code = requestFailedCode(response.getException());
                    String message = requestFailedMessage(response.getException());
                    Logger.d("queryUploadType,onFailed:" + code + "," + message);
                    listener.onFailure(code, message);
                }
            }
        });
    }

    //上传日志文件
    public void uploadLogFile(String fileName, ResponseListener<ResponseBean> listener) {
        try {
            File file = new File(fileName);
            if (!file.exists()) return;
            StringRequest request = new StringRequest(UPLOAD_LOG_FILE, RequestMethod.POST);
            request.add("file", new FileBinary(file));
            queue.add(3, request, new SimpleResponseListener<String>() {
                @Override
                public void onSucceed(int what, Response<String> response) {
                    super.onSucceed(what, response);
                    String responseStr = response.get();
                    if (!TextUtils.isEmpty(responseStr)) {
                        Logger.d("uploadLogFile responseStr:" + responseStr);
                    } else {
                        listener.onFailure(OTHER_MESSAGE_CODE, OTHER_MESSAGE);
                    }
                }

                @Override
                public void onFailed(int what, Response<String> response) {
                    super.onFailed(what, response);
                    if (response != null) {
                        String code = requestFailedCode(response.getException());
                        String message = requestFailedMessage(response.getException());
                        Logger.d("uploadLogFile,onFailed:" + code + "," + message);
                        listener.onFailure(code, message);
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}