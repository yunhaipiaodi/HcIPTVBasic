package com.haochuan.hciptvbasic;

import android.app.Application;

import com.haochuan.core.util.DownloadServer;


public class BaseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DownloadServer.getInstance().initHttp(this);
    }


}
