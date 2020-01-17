package com.haochuan.core.util;

import android.content.Context;

import com.haochuan.core.Logger;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.InitializationConfig;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.URLConnectionNetworkExecutor;
import com.yanzhenjie.nohttp.cache.DBCacheStore;
import com.yanzhenjie.nohttp.cookie.DBCookieStore;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadQueue;
import com.yanzhenjie.nohttp.download.DownloadRequest;

/**
 * @author jewel
 * @email jewelbao88@gmail.com
 * @gitsite https://github.com/jewelbao
 * @since 2019/1/10
 * 下载配置
 */
public class DownloadServer {

    private static DownloadServer instance;
    private static DownloadRequest downloadRequest;

    public static DownloadServer getInstance() {
        if (instance == null) {
            synchronized (DownloadServer.class) {
                if (instance == null) {
                    instance = new DownloadServer();
                }
            }
        }
        return instance;
    }

    /*
    * 全局配置NoHttp,请在Application中调用
    * */
    public void initHttp(Context context){
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
    }

    private DownloadQueue mDownloadQueue;

    private DownloadServer() {
        mDownloadQueue = NoHttp.newDownloadQueue();
    }

    /*
    * 通过DownloadRequest对象发起下载
    * */
    public void download(int what, DownloadRequest mRequest, DownloadListener mListener) {
        mDownloadQueue.add(what, mRequest, mListener);
    }

    /*
     * 指定下载链接和下载文件存入文件目录进行下载
     * @param downloadUrl,下载地址
     * @param fileFolder,下载文件存入文件目录
     * @param fileName,下载文件名称
     * */
    public void download(Context context, String downloadUrl, String fileFolder, String fileName, DownloadServerListener listener){
        Logger.d(String.format("DownloadServer,download('%s','%s','%s')",downloadUrl,fileFolder,fileName));
        downloadRequest = new DownloadRequest(downloadUrl, RequestMethod.GET, fileFolder,
                fileName, true, true);
        downloadRequest.setCancelSign(context);
        download(100, downloadRequest, new DownloadListener() {
            @Override
            public void onDownloadError(int what, Exception exception) {
                listener.onError(what,exception);
                downloadRequest = null;
            }

            @Override
            public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {

            }

            @Override
            public void onProgress(int what, int progress, long fileCount, long speed) {

            }

            @Override
            public void onFinish(int what, String filePath) {
                listener.onFinish(filePath);
                downloadRequest = null;
            }

            @Override
            public void onCancel(int what) {

            }
        });
    }


    /*
    * 取消单个下载
    * */
    public void cancelBySign(Object sign) {
        Logger.d(String.format("DownloadServer,,cancelBySign(%s)",sign));
        mDownloadQueue.cancelBySign(sign);
    }

    /*
     * 取消全部下载
     * */
    public void cancelAll() {
        mDownloadQueue.cancelAll();
    }

    public interface DownloadServerListener{
        public void onFinish(String filePath);
        public void onError(int what, Exception exception);
    }
}
