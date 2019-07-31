package com.haochuan.hciptvbasic;

/*
* 这是主页面的基类，负责webview的初始化工作
*
* */


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.haochuan.hciptvbasic.video.BaseMediaPlayer;
import com.haochuan.hciptvbasic.video.HCPlayer;
import com.haochuan.hciptvbasic.video.IVideoPlayer;
import com.haochuan.hciptvbasic.webview.PayToJS;
import com.haochuan.hciptvbasic.webview.PlayerToJS;
import com.haochuan.hciptvbasic.webview.HCWebChromeClient;
import com.haochuan.hciptvbasic.webview.ToolToJS;

public abstract class BaseWebActivity extends AppCompatActivity {
    private WebView webView;                                    //整个应用唯一的webview
    private PlayerToJS playerToJS;                              //PlayerToJS类实例
    private PayToJS payToJS;                                    // PayToJS类实例
    private ToolToJS toolToJS;                                  //ToolToJS实例
    String playerToJSName = PlayerToJS.class.getSimpleName();    //playerToJS类名
    String payToJSName = PayToJS.class.getSimpleName();         //payToJS类名
    String toolToJSName = ToolToJS.class.getSimpleName();       //toolToJS类名
    //播放器
    private HCPlayer mHCPlayer = null;

    /**-----------------------虚函数-----------------------*/

    //获取启动页web地址
    protected abstract String getIndexURL();
    //处理intent中的参数
    protected abstract void handleIntent(Intent intent);

    /*--------------------生命周期---------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            handleIntent(getIntent());
        }

        initPlayer();

        webView = new WebView(this);
        webView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
//        webView.setBackgroundResource(R.drawable.img_start_bg);
        setContentView(webView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initWebSetting(webView);
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
            mHCPlayer.onResume();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
        }
        if(mHCPlayer !=null){
            mHCPlayer.onPause();
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
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
            mHCPlayer.onDestroy();
        }
        super.onDestroy();
    }

    /**
     * webView对象获取"返回"按键事件
     */
    @Override
    public void onBackPressed() {
        toolToJS.onBackPressed();
    }

    /*--------------------------初始化函数---------------------------*/
    /*
    * 初始化播放器
    * */
    private void initPlayer(){
        mHCPlayer = new HCPlayer(this);
        mHCPlayer.setIVideoPlayerListener(new IVideoPlayer() {
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
                playerToJS.onPlayerDestroy();
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
            }
        });
    }


    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface", "AddJavascriptInterface"})
    private void initWebSetting(WebView webView) {
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
        webView.addJavascriptInterface(toolToJS,toolToJSName);
        // 设置WebClient
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(hcWebChromeClient);
        // 设置是否开启web内容调试，具体调试方式查看：https://developers.google.com/web/tools/chrome-devtools/remote-debugging/?utm_source=dcc&utm_medium=redirect&utm_campaign=2016q3
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(BuildConfig.isDebug);
        }
    }

    private void setPlayerToJS(){
        playerToJS = new PlayerToJS(this,webView,mHCPlayer);
    }
    private void setPayToJS(){ payToJS = new PayToJS(this,webView); }
    private void setToolToJS(){ toolToJS = new ToolToJS(this,webView); }

    /*------------------------子类获取实例接口------------------------------*/

    /**
     * 获取当前WebView对象
     */
    protected WebView getWebView(){return this.webView;}


    /*
    * 获取PlayerToJs实例
    * */
    protected PlayerToJS getPlayerToJS(){return playerToJS;}

    /*
    * 获取播放器实例
    * */
    protected BaseMediaPlayer getMediaPlayer(){return this.mHCPlayer;}

}
