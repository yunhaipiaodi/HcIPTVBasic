package com.haochuan.hciptvbasic;

import android.app.Application;
import android.content.Context;

import com.yanzhenjie.nohttp.InitializationConfig;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.URLConnectionNetworkExecutor;
import com.yanzhenjie.nohttp.cache.DBCacheStore;
import com.yanzhenjie.nohttp.cookie.DBCookieStore;


public class BaseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initHttp(this);
    }

    //初始化网络请求库配置
    public void initHttp(Context context) {
        InitializationConfig config = InitializationConfig.newBuilder(context)
                // 全局连接服务器超时时间，单位毫秒，默认10s。
                .connectionTimeout(60 * 1000)
                // 全局等待服务器响应超时时间，单位毫秒，默认10s。
                .readTimeout(60 * 1000)
                // 配置缓存，默认保存数据库DBCacheStore，保存到SD卡使用DiskCacheStore。
                .cacheStore(
                        // 如果不使用缓存，setEnable(false)禁用。
                        new DBCacheStore(context).setEnable(true)
                )
                // 配置Cookie，默认保存数据库DBCookieStore，开发者可以自己实现CookieStore接口。
                .cookieStore(
                        // 如果不维护cookie，setEnable(false)禁用。
                        new DBCookieStore(context).setEnable(true)
                )
                // 配置网络层，默认URLConnectionNetworkExecutor，如果想用OkHttp：OkHttpNetworkExecutor。
                .networkExecutor(new URLConnectionNetworkExecutor())
                // 全局重试次数，配置后每个请求失败都会重试x次。
                .retry(2)
                .build();
        NoHttp.initialize(config);
        Logger.setDebug(true);
        Logger.setTag("NoHttp");
    }
}
