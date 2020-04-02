package com.haochuan.hciptvbasic;

/*
* 这是主页面的基类，负责webview的初始化工作
*
* */


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.haochuan.core.BaseMediaPlayer;
import com.haochuan.core.IVideoPlayer;
import com.haochuan.core.Logger;
import com.haochuan.core.http.RequestServer;
import com.haochuan.core.http.ResponseListener;
import com.haochuan.core.http.bean.ApkSettingBean;
import com.haochuan.core.http.bean.ResponseBean;
import com.haochuan.core.util.ELS;
import com.haochuan.core.util.HandlerUtil;
import com.haochuan.gsyvideo.HCGsyVideoPlayer;
import com.haochuan.hciptvbasic.webview.PayToJS;
import com.haochuan.hciptvbasic.webview.PlayerToJS;
import com.haochuan.hciptvbasic.webview.HCWebChromeClient;
import com.haochuan.hciptvbasic.webview.UtilToJS;
import com.haochuan.systemvideo.SystemVideoPlayer;
import com.haochuan.weilai_video.CNTVLogin;
import com.haochuan.weilai_video.WeiLaiVideoPlayer;
import com.haochuan.weilai_video.util.ReportCNTVLog;

import java.util.List;

public abstract class BaseWebActivity extends AppCompatActivity {
    private WebView webView;                                    //整个应用唯一的webview
    private PlayerToJS playerToJS;                              //PlayerToJS类实例
    private PayToJS payToJS;                                    // PayToJS类实例
    private UtilToJS utilToJS;                                  //ToolToJS实例
    String playerToJSName = PlayerToJS.class.getSimpleName();    //playerToJS类名
    String payToJSName = PayToJS.class.getSimpleName();         //payToJS类名
    String toolToJSName = UtilToJS.class.getSimpleName();       //toolToJS类名

    private CNTVLogin cntvLogin;
    //播放器
    private BaseMediaPlayer mHCPlayer = null;
    protected ELS els;

    /**-----------------------虚函数-----------------------*/

    //获取启动页web地址
    protected abstract String getIndexURL();

    /*--------------------生命周期---------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        els = ELS.getInstance(this);
        String uid = "";
        //访问接口确定是否需要上传日志
        if (!TextUtils.isEmpty(uid)) {
            RequestServer.getInstance().getApkSetting(uid, new ResponseListener<ApkSettingBean>() {
                @Override
                public void onSuccess(ApkSettingBean response) {
                    Logger.d("getApkSetting:" + response.toString());
                    List<ApkSettingBean.DataBean> dataList = response.getData();
                    for (ApkSettingBean.DataBean data : dataList) {
                        //遍历寻找对应配置
                        if ("open_apk_log_status".equals(data.getSetting_name())) {
                            if ("1".equals(data.getSetting_value())) {
                                Logger.setLogNeedWriteToFile(true);
                                els.saveBoolData(ELS.LAST_LOG_SWITCH, true);
                            } else {
                                els.saveBoolData(ELS.LAST_LOG_SWITCH, false);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(String code, String message) {
                    els.saveBoolData(ELS.LAST_LOG_SWITCH, false);
                }
            });
        }

        //每次启动应用时,检查一下上一次使用是否要上传日志,
        // 因为在应用关闭时上传文件可能导致内存内泄漏或上传失败,所以放在应用启动时上传
        if (els.getBoolData(ELS.LAST_LOG_SWITCH)) {
            String lasLogFileName = els.getStringData(ELS.LOG_FILE_NAME);
            Logger.d("uploadLogFile lasLogFileName:" + lasLogFileName);
            if (!TextUtils.isEmpty(lasLogFileName)) {
                RequestServer.getInstance().uploadLogFile(lasLogFileName,
                        new ResponseListener<ResponseBean>() {
                            @Override
                            public void onSuccess(ResponseBean response) {
                                Logger.d("uploadLogFile:" + response.toString());
                            }

                            @Override
                            public void onFailure(String code, String message) {
                            }
                        });
            }
        }

        //初始化日志
        Logger.init(this,getWebView());

        //如果是未来版本，需要先初始化其sdk;如果是其他版本，可以
        // 注释该段代码
        cntvLogin = new CNTVLogin();
        if(BuildConfig.player_type == 2){
            CNTVInit();
        }

        //初始化播放器
        initPlayer();

        webView = new WebView(this);
        webView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        setContentView(webView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initWebSetting(webView);

        //未来版本需要先初始化SDK成功再加载页面
        if(BuildConfig.player_type != 2){
            runH5();
        }

    }

    private void runH5(){
        webView.loadUrl(getIndexURL());
    }

    @Override
    protected void onStart() {
        super.onStart();
        //关闭软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
        }
        if(mHCPlayer !=null){
            mHCPlayer.resume();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
        }
        if(mHCPlayer !=null){
            mHCPlayer.pause();
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        boolean isForeground = isRunningForeground(this);
        if (!isForeground) {
            Handler handler = new Handler(getMainLooper());
            handler.postDelayed(this::AppExit, 500);
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.removeJavascriptInterface(playerToJSName);
            webView.clearCache(true);
            webView.clearFormData();
            webView.clearMatches();
            webView.clearSslPreferences();
            webView.clearDisappearingChildren();
            webView.clearHistory();
            webView.clearAnimation();
            webView.loadUrl("about:blank");
            webView.removeAllViews();

            webView = null;
        }
        if(mHCPlayer !=null){
            mHCPlayer.release();
        }
        super.onDestroy();
    }

    /**
     * webView对象获取"返回"按键事件
     */
    @Override
    public void onBackPressed() {
        //如果是未来版本，请用这段代码
        if(cntvLogin.isOpenAdshow()){
            Log.d("djbl","onBackPressed close ad");
            cntvLogin.removeADImage(this);
        }else{
            utilToJS.onBackPressed();
        }

        //如果是除未来其他版本，请用这段代码
        utilToJS.onBackPressed();
    }

    /*--------------------------初始化函数---------------------------*/
    /*
    * 初始化播放器
    * */
    private void initPlayer(){
        Logger.d("BaseWebActivity,initPlayer()");
        switch (BuildConfig.player_type){
            case 1:
                mHCPlayer = new SystemVideoPlayer(this);
                break;
            case 2:
                mHCPlayer = new WeiLaiVideoPlayer(this);
                break;
            case 3:
                mHCPlayer = new HCGsyVideoPlayer(this);
                break;
            default:
                break;
        }
        mHCPlayer.setVideoPlayerListener(new IVideoPlayer() {
            @Override
            public void onPreparing() {
                playerToJS.onPlayerPreparing();
            }

            @Override
            public void onPlaying() {
                playerToJS.onPlayerPlaying();
            }

            @Override
            public void onResume() {
                playerToJS.onPlayerResume();
            }

            @Override
            public void onPause() {
                playerToJS.onPlayerPause();
            }

            @Override
            public void onDestroy() {
            }

            @Override
            public void onPlayingBuffering() {
                playerToJS.onPlayingBuffer();
            }

            @Override
            public void onCompletion() {
                playerToJS.onPlayerComplete();
            }

            @Override
            public void onError(int what, int extra) {
                playerToJS.onPlayerError(what,extra);
                //取消提示框显示，改由前端页面实现
               /* if(BuildConfig.player_type == 2){
                    CNTVPlayerErrorAlert();
                }*/
            }

        });
    }



    /*---------------------------------------未来电视方法-------------------------------------*/
    //这部分代码只在接入未来电视播放时有，如果不是，请注释掉

    /*
    *初始化未来SDK
    * 加载未来电视广告图片
    * */
    private void CNTVInit(){
        Logger.d("BaseWebActivity,CNTVInit()");
        cntvLogin.init(this, new CNTVLogin.OnCNTVListener() {
            @Override
            public void onOttLoginSuccess() {
                runH5();
            }

            @Override
            public void onOttLoginFail(String code, String msg) {
                OttLoginFailAlert(code,msg);
            }

            @Override
            public void onOttLoginError(Throwable throwable) {
                OttLoginFailAlert("-1",throwable == null ? "发生未知异常" : throwable.getMessage());
            }
        });
    }

    /*
    *ott登陆失败提示
    * */
    private void OttLoginFailAlert(String code,String message){
        Logger.d(String.format("BaseWebActivity,OttLoginFailAlert(%s,%s)",code,message));
        HandlerUtil.runOnUiThread(()->{
            String msg = String.format("认证失败 code:%s,失败信息：%s",code,message);
            new AlertDialog.Builder(this)
                    .setTitle("ott认证失败")
                    .setMessage(msg)
                    .setPositiveButton("确定", (dialog, which) -> {
                        dialog.dismiss();
                        AppExit();
                    })
                    .show();
        });
    }

    /*
     * 未来电视功能：视频播放错误，提示并且退出播放
     * */
    private void CNTVPlayerErrorAlert(){
        Logger.d("BaseWebActivity,CNTVPlayerErrorAlert()");
        HandlerUtil.runOnUiThread(()->{
            new AlertDialog.Builder(BaseWebActivity.this)
                    .setTitle("提示")
                    .setMessage("该节目已下线!")
                    .setCancelable(false)
                    .setPositiveButton("确定", (dialog, which) -> {
                        playerToJS.stop();
                    }).show();
        });
    }

    /*---------------------------------------未来电视方法 end-------------------------------------*/

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface", "AddJavascriptInterface"})
    private void initWebSetting(WebView webView) {
        Logger.d("BaseWebActivity,initWebSetting()");
        try{
            WebSettings webSettings = webView.getSettings();
            // 由H5端适配屏幕，具体参考文档：https://developer.chrome.com/multidevice/webview/pixelperfect
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            // 设置JS交互
            webSettings.setJavaScriptEnabled(true);

            HCWebChromeClient hcWebChromeClient = new HCWebChromeClient();
            setPlayerToJS();
            setPayToJS();
            setToolToJS();

            webView.addJavascriptInterface(playerToJS,playerToJSName);
            webView.addJavascriptInterface(payToJS,payToJSName);
            webView.addJavascriptInterface(utilToJS,toolToJSName);
            // 设置WebClient
            webView.setWebViewClient(new WebViewClient());
            webView.setWebChromeClient(hcWebChromeClient);
            // 设置是否开启web内容调试，具体调试方式查看：https://developers.google.com/web/tools/chrome-devtools/remote-debugging/?utm_source=dcc&utm_medium=redirect&utm_campaign=2016q3
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(BuildConfig.isDebug);
            }

            webView.requestFocus();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setPlayerToJS(){
        playerToJS = new PlayerToJS(this,webView,mHCPlayer);
    }
    private void setPayToJS(){ payToJS = new PayToJS(this,webView); }
    private void setToolToJS(){ utilToJS = new UtilToJS(this,webView); }

    /*-----------------------------------功能函数 start----------------------------------*/

    /**
     * 判断应用是否处于前台
     *
     * @return <code>true</code>为前台，反之为后台
     */
    public boolean isRunningForeground(Context context) {
        Logger.d("BaseWebActivity,isRunningForeground()");
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) return false;
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
        // 枚举进程
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
            if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcessInfo.processName.equals(context.getApplicationInfo().processName)) {
                    Logger.d("应用处于前台状态");
                    return true;
                }
            }
        }
        Logger.d("应用退到后台");
        return false;
    }

    /*
     * 退出应用
     * */
    public void AppExit() {
        Logger.d("BaseWebActivity,AppExit()");
        if(BuildConfig.player_type == 2){
            new ReportCNTVLog().reportExitLog();
        }
        android.os.Process.killProcess(android.os.Process.myPid());   //获取PID
        System.exit(0);
    }

    /*------------------------子类获取实例接口------------------------------*/

    /**
     * 获取当前WebView对象
     */
    protected WebView getWebView(){return this.webView;}

    /*
     * 获取播放器实例
     * */
    protected BaseMediaPlayer getMediaPlayer(){return this.mHCPlayer;}



    /*
    * 获取PlayerToJs实例
    * */
    protected PlayerToJS getPlayerToJS(){return playerToJS;}

    /*
     * 获取PayToJS实例
     * */
    protected PayToJS getPayToJS(){return payToJS;}


    /*
     * 获取ToolToJS实例
     * */
    protected UtilToJS getUtilToJS(){return utilToJS;}


}
