package com.haochuan.core.http.bean;

import com.google.gson.Gson;

/**
 * Created by ncx on 2019/12/25
 * 接口请求通用参数类
 */
public class ResponseBean {
    //code为0表示请求成功
    private int code;
    private String message;

    public static ResponseBean objectFromData(String str) {
        return new Gson().fromJson(str, ResponseBean.class);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ResponseBean{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}