package com.haochuan.core.http.bean;

import com.google.gson.Gson;

/**
 * Created by ncx on 2020/3/11
 * 更新接口返回数据实体类
 */
public class UpdateResponseBean {

    //code为0表示请求成功
    private int code;
    private DataBean data;
    private String msg;

    public static UpdateResponseBean objectFromData(String str) {
        return new Gson().fromJson(str, UpdateResponseBean.class);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean {
        private int id;
        private String name;
        //APK包大小,用于对比机器剩余大小
        private double apk_size;
        //APK下载链接
        private String apk_url;
        //此参数为1才更新
        private int upload_type;
        //APK版本号
        private int version_code;
        //APK版本名
        private String version_name;
        //md5校验值,未来电视用
        private String apk_md5;
        //版本描述
        private Object version_content;
        //下面三个参数是对应操作时间
        private String created_at;
        private String updated_at;
        private Object deleted_at;

        public static DataBean objectFromData(String str) {

            return new Gson().fromJson(str, DataBean.class);
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getApk_size() {
            return apk_size;
        }

        public void setApk_size(double apk_size) {
            this.apk_size = apk_size;
        }

        public String getApk_url() {
            return apk_url;
        }

        public void setApk_url(String apk_url) {
            this.apk_url = apk_url;
        }

        public int getUpload_type() {
            return upload_type;
        }

        public void setUpload_type(int upload_type) {
            this.upload_type = upload_type;
        }

        public int getVersion_code() {
            return version_code;
        }

        public void setVersion_code(int version_code) {
            this.version_code = version_code;
        }

        public String getVersion_name() {
            return version_name;
        }

        public void setVersion_name(String version_name) {
            this.version_name = version_name;
        }

        public String getApk_md5() {
            return apk_md5;
        }

        public void setApk_md5(String apk_md5) {
            this.apk_md5 = apk_md5;
        }

        public Object getVersion_content() {
            return version_content;
        }

        public void setVersion_content(Object version_content) {
            this.version_content = version_content;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
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
                    ", name='" + name + '\'' +
                    ", apk_size=" + apk_size +
                    ", apk_url='" + apk_url + '\'' +
                    ", upload_type=" + upload_type +
                    ", version_code=" + version_code +
                    ", version_name='" + version_name + '\'' +
                    ", apk_md5='" + apk_md5 + '\'' +
                    ", version_content=" + version_content +
                    ", created_at='" + created_at + '\'' +
                    ", updated_at='" + updated_at + '\'' +
                    ", deleted_at=" + deleted_at +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "UpdateResponseBean{" +
                "code=" + code +
                ", data=" + data +
                ", msg='" + msg + '\'' +
                '}';
    }
}