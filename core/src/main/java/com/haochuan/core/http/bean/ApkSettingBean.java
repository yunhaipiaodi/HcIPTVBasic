package com.haochuan.core.http.bean;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by ncx on 2020/3/17
 * 获取APK配置状态实体类
 */
public class ApkSettingBean {
    //code为0表示请求成功
    private int code;
    private String message;
    private List<DataBean> data;

    public static ApkSettingBean objectFromData(String str) {
        return new Gson().fromJson(str, ApkSettingBean.class);
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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        //自增id,不需要处理
        private int id;
        //配置标识名称
        private String setting_name;
        //配置描述，解释
        private Object setting_describe;
        //配置值,如果是1代表该项配置已打开
        private String setting_value;
        //配置类型【1：apk】
        private int setting_type;
        private Object created_at;
        private Object updated_at;
        private Object deleted_at;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSetting_name() {
            return setting_name;
        }

        public void setSetting_name(String setting_name) {
            this.setting_name = setting_name;
        }

        public Object getSetting_describe() {
            return setting_describe;
        }

        public void setSetting_describe(Object setting_describe) {
            this.setting_describe = setting_describe;
        }

        public String getSetting_value() {
            return setting_value;
        }

        public void setSetting_value(String setting_value) {
            this.setting_value = setting_value;
        }

        public int getSetting_type() {
            return setting_type;
        }

        public void setSetting_type(int setting_type) {
            this.setting_type = setting_type;
        }

        public Object getCreated_at() {
            return created_at;
        }

        public void setCreated_at(Object created_at) {
            this.created_at = created_at;
        }

        public Object getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(Object updated_at) {
            this.updated_at = updated_at;
        }

        public Object getDeleted_at() {
            return deleted_at;
        }

        public void setDeleted_at(Object deleted_at) {
            this.deleted_at = deleted_at;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "id=" + id +
                    ", setting_name='" + setting_name + '\'' +
                    ", setting_describe=" + setting_describe +
                    ", setting_value='" + setting_value + '\'' +
                    ", setting_type=" + setting_type +
                    ", created_at=" + created_at +
                    ", updated_at=" + updated_at +
                    ", deleted_at=" + deleted_at +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ApkSettingBean{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}