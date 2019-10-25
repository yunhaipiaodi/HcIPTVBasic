package com.haochuan.weilai_video.util;

import com.haochuan.weilai_video.BuildConfig;

import tv.icntv.logsdk.logSDK;

public class ReportCNTVLog {


    public void reportHomeLog(String  extVersionType,String extVersionCode) {
        reportLog(88, String.format("0,%s", BuildConfig.VERSION_NAME));//上报进入 APK 日志
        reportLog(0, "0");//进入首页
        reportLog(10, String.format("0,%s,%s", extVersionType, extVersionCode));//认证成功
    }

    public void reportExitLog() {
        reportLog(88, "1");//上报退出 APK 日志
    }

    /**
     * 上传收藏/取消记录
     *
     * @param isCollected 是否收藏操作
     * @param id          视频id
     */
    public void reportCollectedOrNotLog(boolean isCollected, String id) {
        reportLog(5, String.format("%s, %s", isCollected ? 0 : 1, id));
    }

    /**
     * 上传添加历史记录
     *
     * @param id 视频id
     */
    public void reportAddHistoryLog(String id) {
        reportLog(15, String.format("0, %s", id));
    }

    /**
     * 上传清空历史记录
     */
    public void reportClearAllHistoryLog() {
        reportLog(15, "2, 0");
    }

    /**
     * 上传搜索日志
     *
     * @param keyword 关键字
     */
    public void reportSearchLog(String keyword) {
        reportLog(2, keyword);
    }



    /**
     * 上传日志
     * @param type 日志类型，详情请查看未来sdk提供的文档
     * @param event 日志事件，详情请查看未来sdk提供的文档
     */
    public void reportLog(int type, String event) {
        try {
            logSDK.getInstance().logUpload(type, event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
