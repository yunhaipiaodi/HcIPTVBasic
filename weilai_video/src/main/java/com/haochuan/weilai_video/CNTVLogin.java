package com.haochuan.weilai_video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.haochuan.core.Logger;
import com.haochuan.core.http.DownloadServer;
import com.haochuan.core.util.FileUtil;
import com.haochuan.core.util.HandlerUtil;
import com.haochuan.core.util.JSONUtil;
import com.haochuan.core.util.Md5Util;
import com.haochuan.weilai_video.store.LocalStore;
import com.haochuan.weilai_video.util.AdDataUtil;
import com.haochuan.weilai_video.util.ReportCNTVLog;

import org.json.JSONObject;

import java.io.File;

import tv.icntv.adsdk.AdSDK;
import tv.icntv.logsdk.logSDK;
import tv.icntv.ottlogin.loginSDK;
import tv.newtv.upgradesdk.upgradeSDK;

public class CNTVLogin {
    private static CNTVLogin instance;
    private boolean isOpenAdshow = false;  //当前是否开屏广告中
    private int adShowTime = 0;     //广告显示时间

    //显示广告图片组件
    private ImageView adImageView;
    private TextView adTimeTextView;
    private ViewGroup viewGroup;

    private OnCNTVListener mListener;

    public void init(Context context, OnCNTVListener listener) {
        new Thread(() -> {
            Activity activity = (Activity) context;
            viewGroup = (ViewGroup) activity.getWindow().getDecorView();
            try {
                //第一步，先ott登陆
                boolean initResult = loginSDK.getInstance().sdkInit(loginSDK.TYPE_COMMON, BuildConfig.icntv_app_channel, BuildConfig.icntv_app_key, BuildConfig.icntv_app_secret, context);
                if (initResult) {//登陆成功
                    //开始认证
                    String loginRet = loginSDK.getInstance().deviceLogin();
                    if (loginRet.equals("1")) {
                        ottLoginSuccess(context, listener);
                    } else {
                        ottLoginFail(loginRet, listener);
                    }
                } else { //登陆失败
                    activity.runOnUiThread(() -> listener.onOttLoginFail("-1", "sdkInit初始化失败"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                activity.runOnUiThread(() -> listener.onOttLoginError(e));
            }
        }).start();
    }

    /*
     * ott登陆成功
     * */
    private void ottLoginSuccess(Context context, OnCNTVListener listener) {
        Logger.d("CNTVLogin,ottLoginSuccess()");

        mListener = listener;
        //ott登陆成功后，开始以下动作
        StringBuffer tf = new StringBuffer();

        //初始化日志
        loginSDK.getInstance().getValueByKey("EXT_VERSION_TYPE", tf);
        String extVersionType = tf.toString();

        loginSDK.getInstance().getValueByKey("EXT_VERSION_CODE", tf);
        String extVersionCODE = tf.toString();

        loginSDK.getInstance().getServerAddress("USER_LOG", tf);
        String serverLog = tf.toString();

        loginSDK.getInstance().getDeviceID(tf);       //设备ID
        String deviceID = tf.toString();

        loginSDK.getInstance().getValueByKey("EXT_GET_LOGIN_MAC", tf);
        String mac = tf.toString();

        initLog(extVersionType, extVersionCODE, serverLog, deviceID, mac);

        //初始化广告
        loginSDK.getInstance().getServerAddress("AD", tf);
        String serverAD = tf.toString();

        loginSDK.getInstance().getAppKey(tf);
        String platformID = tf.toString();

        initAD(context, serverAD, deviceID, platformID);

        //检查是否由升级
        loginSDK.getInstance().getServerAddress("APP_UPDATE", tf);
        String serverAppUpdate = tf.toString();
        initUpgrade(context, serverAppUpdate, listener);

        /*一开始显示开屏广告图片是和CNTVLogin系列初始化进程并行执行的，这就导致
        开屏广告显示期间，前端页面已经加载完毕，这时操作遥控，会播放视频同时广告并没有关闭；
        所以将显示开屏广告图片转移到CNTVLogin初始化序列化中，只有广告关闭后，才开始加载前端页面*/
        HandlerUtil.runOnUiThread(() -> showAdImage(context));
        //HandlerUtil.runOnUiThread(listener::onOttLoginSuccess);
    }

    /*
     * ott登陆失败
     * */
    private void ottLoginFail(String loginRet, OnCNTVListener listener) {
        // 认证失败，上报错误日志，提示框展示
        loginSDK.getInstance().logUpload();
        // 失败后上报认证错误日志，用于远程问题定位 String msg = loginSDK.getInstance().loginStatusToMsg(loginRet);
        String msg = loginSDK.getInstance().loginStatusToMsg(loginRet);

        new ReportCNTVLog().reportLog(10, String.format("1,%s,%s,%s", "COMMON", BuildConfig.VERSION_CODE, loginRet));//认证成功

        HandlerUtil.runOnUiThread(() -> listener.onOttLoginFail(loginRet, msg));
    }

    /*
     *初始化日志
     * */
    private void initLog(String extVersionType, String extVersionCode, String serverLog, String deviceID, String mac) {
        Logger.d(String.format("CNTVLogin,initLog(%s,%s,%s,%s,%s)",
                extVersionType, extVersionCode, serverLog, deviceID, mac));
        if (logSDK.getInstance().sdkInit(serverLog, "", deviceID, BuildConfig.icntv_app_channel, BuildConfig.icntv_app_key)) {
            new ReportCNTVLog().reportHomeLog(extVersionType, extVersionCode);
        }
    }

    /*
     * 初始化广告
     * */
    private void initAD(Context context, String serverAD, String deviceId, String platformID) {
        Logger.d(String.format("广告初始化：serverAD: %s ,deviceId: %s ,platformID: %s", serverAD, deviceId, platformID));
        //开始初始化广告SDK组件
        boolean adInitResult = AdSDK.getInstance().init(serverAD, deviceId, BuildConfig.icntv_app_key, BuildConfig.icntv_app_channel, null, context);
        if (adInitResult) { //初始化成功
            //获取广告数据
            StringBuffer sb = new StringBuffer();
            int getAdResult = AdSDK.getInstance().getAD("open", null, null, null, null, null, sb);
            if (getAdResult != 0) {
                Logger.d("获取广告数据失败,失败code:" + getAdResult);
                return;
            }
            String adJsonStr = sb.toString();
            //adJsonStr = "{\"adspaces\":{\"open\":[{\"ext\":\"\",\"pos\":\"\",\"materials\":[{\"file_path\":\"http://image.adott.ottcn.com/images/icntvad/201909/1568106992_65299039243b6a6183b587b4851d7429.jpg\",\"event_content\":\"\",\"event_type\":\"null\",\"file_inject\":\"public\",\"file_md5\":\"6fa6570323d69389a55a7a626def67d7\",\"file_name\":\"0f9c28ec662c4bd8e59acccdefcecc4.jpg\",\"name\":\"联合产品-apk开屏测试素材\",\"file_source\":\"network\",\"id\":\"764\",\"type\":\"image\",\"file_size\":230687,\"play_time\":6}],\"mid\":549,\"aid\":\"25\"}]},\"status\":1}";
            if (TextUtils.isEmpty(adJsonStr)) {
                //广告数据为空，退出
                Logger.d("未获取到广告数据");
                deleteAdImageAndOpenAdJson(context);
                return;
            }
            Logger.d(String.format("获取到的广告数据json字符串：%s", adJsonStr));

            if (AdDataUtil.getStatus(adJsonStr) == 1) {  //成功获取数据
                String openAdJson = AdDataUtil.getOpenAdJson(adJsonStr);
                if (!TextUtils.isEmpty(openAdJson)) {     //json中包含open对象
                    LocalStore.getInstance().putOpenAdJson(context, openAdJson); //将openAdJson先存储起来
                    int adShowTime = AdDataUtil.getAdShowTime(openAdJson);
                    LocalStore.getInstance().putAdPlayTime(context, adShowTime);
                    String adImageUrl = AdDataUtil.getAdImageUrl(openAdJson);
                    if (!TextUtils.isEmpty(adImageUrl)) {
                        downloadAdImage(context, adImageUrl);    // 下载图片到本地保存；
                    } else {
                        deleteAdImageAndOpenAdJson(context);
                    }
                } else {
                    deleteAdImageAndOpenAdJson(context);
                }
            } else {
                Logger.d("获取广告数据失败");
                deleteAdImageAndOpenAdJson(context);
            }
        } else {
            deleteAdImageAndOpenAdJson(context);
        }
    }

    /*
     * 删除广告图片和openAdJson信息
     * */
    private void deleteAdImageAndOpenAdJson(Context context) {
        LocalStore.getInstance().putOpenAdJson(context, "");
        String adImagePath = LocalStore.getInstance().getAdImagePath(context);
        if (TextUtils.isEmpty(adImagePath)) {
            return;
        }
        FileUtil.deleteForce(adImagePath);
        LocalStore.getInstance().putAdImagePath(context, "");
    }

    /*
     * 下载图片
     * */
    private void downloadAdImage(Context context, String adImageUrl) {
        String folder = context.getCacheDir().getPath() + File.separator + "ad";
        String filename = String.valueOf(adImageUrl.hashCode());

        DownloadServer.getInstance().download(context, adImageUrl, folder, filename, new DownloadServer.DownloadServerListener() {
            @Override
            public void onFinish(String filePath) {
                LocalStore.getInstance().putAdImagePath(context, filePath);
            }

            @Override
            public void onError(int what, Exception exception) {
                Logger.e(String.format("下载异常:%s", exception.getCause().getMessage()));
            }
        });
    }

    /*
     * 初始化升级
     * */
    private void initUpgrade(Context context, String server, OnCNTVListener listener) {
        if (upgradeSDK.getInstance().init(0)) {
            updateVersionOnUiThread(context, server, listener);
        }
    }

    /*
     * 检查是否有升级并升级
     * */
    private void updateVersionOnUiThread(Context context, String server, OnCNTVListener listener) {
        HandlerUtil.runOnUiThread(() -> updateVersion(context, server, listener));
    }

    private void updateVersion(Context context, String server, OnCNTVListener listener) {
        try {
            StringBuffer sb = new StringBuffer();
            int responseCode = upgradeSDK.getInstance().J_getAppUpgradeInfo(server, BuildConfig.icntv_app_key, BuildConfig.icntv_app_channel,
                    String.valueOf(BuildConfig.VERSION_CODE), sb);
            Logger.d(String.format("app更新信息：%s", sb.toString()));
            JSONObject json = new JSONObject(sb.toString());
            String upgradeAddr = JSONUtil.getString(json, "packageAddr", "");
            String md5 = JSONUtil.getString(json, "packageMD5", "");
            if (TextUtils.isEmpty(upgradeAddr) || TextUtils.isEmpty(md5)) {
                Logger.e(String.format("upgradeAddr is empty or md5 is empty,upgradeAddr:%s,md5:%s",
                        upgradeAddr, md5));
                return;
            } else {
                if (TextUtils.equals(upgradeAddr, "null") || TextUtils.equals(md5, "null")) {
                    Logger.e(String.format("upgradeAddr is null or md5 is null,upgradeAddr:%s,md5:%s",
                            upgradeAddr, md5));
                    return;
                }
            }
            DownloadInstallApk(context, upgradeAddr, md5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载并安装apk
     */
    private void DownloadInstallApk(Context context, String url, String md5) {
        String folder = context.getCacheDir().getPath() + File.separator + "ad";
        String filename = getFileNameFromUrl(url);

        DownloadServer.getInstance().download(context, url, folder, filename, new DownloadServer.DownloadServerListener() {
            @Override
            public void onFinish(String filePath) {
                File file = new File(filePath);
                String apkMD5 = Md5Util.getFileMD5(file).toUpperCase();
                String updateD5 = md5.toUpperCase();
                if (!apkMD5.equals(updateD5)) {
                    file.delete();
                    DownloadInstallApk(context, url, md5);
                } else {
                    apkInstall(context, file);
                }
            }

            @Override
            public void onError(int what, Exception exception) {
                Logger.e("下载异常");
            }
        });
    }

    private String getFileNameFromUrl(String url) {
        String[] splitArray = url.split("/");
        return splitArray[splitArray.length - 1];
    }

    private void apkInstall(Context context, File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile),
                    "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }


    /*
     * 显示开屏广告图片
     * */
    public void showAdImage(Context context) {
        Logger.d("CNTVLogin,showAdImage()");
        String adImagePath = LocalStore.getInstance().getAdImagePath(context);
        if (TextUtils.isEmpty(adImagePath)) {
            Logger.d("没有找到广告图片路径，退出");
            //通知BaseWebActivityCNTVLogin执行完毕，加载页面
            HandlerUtil.runOnUiThread(mListener::onOttLoginSuccess);
            return;
        }
        adShowTime = LocalStore.getInstance().getAdPlayTime(context);
        adImageView = new ImageView(context);
        adImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        adImageView.setImageDrawable(Drawable.createFromPath(adImagePath));
        viewGroup.addView(adImageView);
        isOpenAdshow = true;

        adTimeTextView = new TextView(context);
        adTimeTextView.setText(String.format("广告剩余%s秒", adShowTime));
        adTimeTextView.setBackgroundResource(R.drawable.bg_ad_time);
        adTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        adTimeTextView.setPadding(10, 10, 10, 10);
        adTimeTextView.setTextColor(context.getResources().getColor(android.R.color.black));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.RIGHT;
        layoutParams.topMargin = 20;
        layoutParams.rightMargin = 20;
        viewGroup.addView(adTimeTextView, layoutParams);

        HandlerUtil.runOnUiThreadDelay(new Runnable() {
            @Override
            public void run() {
                if (adShowTime <= 0) {
                    Log.d("djbl", "time to close ad");
                    removeADImage(context);
                } else {
                    adShowTime--;
                    adTimeTextView.setText(String.format("广告剩余%s秒", adShowTime));
                    HandlerUtil.runOnUiThreadDelay(this, 1000);
                }
            }
        }, 1000);
    }

    /*
     * 当前是否在显示开屏广告
     * */
    public boolean isOpenAdshow() {
        Logger.d("CNTVLogin,isOpenAdshow()");
        return isOpenAdshow;
    }

    /*
     * 将广告显示移除
     * */
    public void removeADImage(Context context) {
        Logger.d("CNTVLogin,removeADImage()");
        if (adImageView == null || adTimeTextView == null) {
            //通知BaseWebActivityCNTVLogin执行完毕，加载页面
            HandlerUtil.runOnUiThread(mListener::onOttLoginSuccess);
            return;
        }
        adImageView.setImageDrawable(null);
        viewGroup.removeView(adImageView);
        viewGroup.removeView(adTimeTextView);
        reportAd(context);
        isOpenAdshow = false;
        //通知BaseWebActivityCNTVLogin执行完毕，加载页面
        HandlerUtil.runOnUiThread(mListener::onOttLoginSuccess);
    }

    /*
     *
     * */
    public void reportAd(Context context) {
        String openAdJson = LocalStore.getInstance().getOpenAdJson(context);
        if (TextUtils.isEmpty(openAdJson)) {
            return;
        }
        try {
            String mid = AdDataUtil.getMid(openAdJson);
            String aid = AdDataUtil.getAid(openAdJson);
            String id = AdDataUtil.getMaterialId(openAdJson);
            boolean reportAD = AdSDK.getInstance().report(String.valueOf(mid), String.valueOf(aid), id,
                    "", "", "", "");
            if (reportAD && BuildConfig.isDebug) {
                Logger.d(String.format("上传广告日志：%s", openAdJson));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*--------------------接口------------------------*/
    public interface OnCNTVListener {
        void onOttLoginSuccess();

        void onOttLoginFail(String code, String msg);

        void onOttLoginError(Throwable throwable);

    }

}