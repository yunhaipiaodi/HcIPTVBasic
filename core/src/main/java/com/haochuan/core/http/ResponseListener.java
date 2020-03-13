package com.haochuan.core.http;

/**
 * Created by ncx on 2020/3/10
 * 网络请求响应接口
 */
public interface ResponseListener<T> {
    void onSuccess(T response);

    void onFailure(String code, String message);
}