package com.haochuan.hciptvbasic.Util;


import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;

/**
 * 下载文件工具类
 */
public class DownloadUtils {

    /**
     * 下载文件
     *
     * @param url           下载地址
     * @param fileName      文件名
     * @param fileExtension 文件扩展名，例如exe，apk，xml等等
     * @param listener      下载进度监听
     */
    public static void download(String url, final String fileName, String fileExtension, final DownloadProgressListener listener) {
        Logger.d("准备下载文件:%s");
        final String filePath = FileDownloadUtils.getDefaultSaveRootPath() + File.separator + "temp" + File.separator + fileName + "." + fileExtension;
        if (listener != null) {
            listener.onDownloadStart(filePath);
        }
        FileDownloader.getImpl().create(url)
                .setPath(filePath, false)
                .setCallbackProgressTimes(500)
                .setMinIntervalUpdateSpeed(400)
                .setListener(new FileDownloadLargeFileListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                    }

                    @Override
                    protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                        int progress = (int) ((soFarBytes * 100) / totalBytes);
                        Logger.d(String.format("文件下载中，进度【%s, %s, %s】", soFarBytes, progress, totalBytes));
                        if (listener != null) {
                            listener.onDownloadProgress(progress);
                        }
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        Logger.d(String.format("文件下载成功，路径:%s", filePath));
                        if (listener != null) {
                            listener.onDownloadSuccessful(filePath);
                        }
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        Logger.e(String.format("文件下载异常:%s", e));
                        if (listener != null) {
                            listener.onDownloadFail(e.getMessage());
                        }
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {

                    }
                }).start();
    }

    public interface DownloadProgressListener {
        void onDownloadStart(String fileName);

        void onDownloadProgress(int progress);

        void onDownloadSuccessful(String filePath);

        void onDownloadFail(String message);
    }
}
