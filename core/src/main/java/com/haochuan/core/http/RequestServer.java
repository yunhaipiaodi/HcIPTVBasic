package com.haochuan.core.http;

import com.google.gson.Gson;
import com.haochuan.core.Logger;
import com.haochuan.core.http.bean.UpdateResponseBean;
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

/**
 * Created by ncx on 2020/3/10
 * 接口统一请求类
 */
public class RequestServer {
    public static final String UNKNOW_ERROR = "0x001";
    public static final String EXCEPTION_NETWORK = "0x002";
    public static final String EXCEPTION_CONNECT_TIMEOUT = "0x003";
    public static final String EXCEPTION_HOST = "0x004";
    public static final String EXCEPTION_URL = "0x005";
    public static final String OTHER_MESSAGE = "其它错误";
    public static final String OTHER_MESSAGE_CODE = "0x999";


    private static final String HOST = "http://150.138.11.180:6401/";

    private static final String UPDATE_VERSION = HOST + "apk/up_version";

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
    public void updateVersion(int versionCode, ResponseListener<UpdateResponseBean> listener) {
        StringRequest request = new StringRequest(UPDATE_VERSION, RequestMethod.GET);
        queue.add(1, request, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                super.onSucceed(what, response);
                String responseStr = response.get();
                if (responseStr != null) {
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
                    Logger.d("onFailed:" + code + "," + message);
                    listener.onFailure(code, message);
                }
            }
        });
    }

}