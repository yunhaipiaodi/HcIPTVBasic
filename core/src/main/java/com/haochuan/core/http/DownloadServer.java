package com.haochuan.core.http;

import android.content.Context;

import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadQueue;
import com.yanzhenjie.nohttp.download.DownloadRequest;

/**
 * 下载文件单例
 * @since 2019/1/10
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

    private DownloadQueue mDownloadQueue;

    private DownloadServer() {
        mDownloadQueue = NoHttp.newDownloadQueue();
    }

    public void download(int what, DownloadRequest mRequest, DownloadListener mListener) {
        mDownloadQueue.add(what, mRequest, mListener);
    }

    public void download(Context context, String downloadUrl, String fileFolder, String fileName, DownloadServerListener listener) {
        downloadRequest = new DownloadRequest(downloadUrl, RequestMethod.GET, fileFolder,
                fileName, true, true);
        downloadRequest.setCancelSign(context);
        download(100, downloadRequest, new DownloadListener() {
            @Override
            public void onDownloadError(int what, Exception exception) {
                listener.onError(what, exception);
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

    public void cancelBySign(Object sign) {
        if (mDownloadQueue != null) {
            mDownloadQueue.cancelBySign(sign);
        }
    }

    public void cancelAll() {
        if (mDownloadQueue != null) {
            mDownloadQueue.cancelAll();
        }
    }

    //关闭队列,在使用的地方记得及时调用该方法关闭,以防内存泄漏
    public void stop() {
        if (mDownloadQueue != null) {
            mDownloadQueue.stop();
        }
    }

    public interface DownloadServerListener {
        public void onFinish(String filePath);

        public void onError(int what, Exception exception);
    }
}