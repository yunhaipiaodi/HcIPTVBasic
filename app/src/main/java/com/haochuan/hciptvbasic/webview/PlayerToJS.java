package com.haochuan.hciptvbasic.webview;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.annotation.StringDef;

import com.haochuan.hciptvbasic.Util.Logger;
import com.haochuan.hciptvbasic.video.BaseMediaPlayer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.haochuan.hciptvbasic.webview.PlayerToJS.JsEvent.JS_EVENT_COMPLETE;
import static com.haochuan.hciptvbasic.webview.PlayerToJS.JsEvent.JS_EVENT_DESTROY;
import static com.haochuan.hciptvbasic.webview.PlayerToJS.JsEvent.JS_EVENT_LOG;
import static com.haochuan.hciptvbasic.webview.PlayerToJS.JsEvent.JS_EVENT_PAUSE;
import static com.haochuan.hciptvbasic.webview.PlayerToJS.JsEvent.JS_EVENT_PLAYERROR;
import static com.haochuan.hciptvbasic.webview.PlayerToJS.JsEvent.JS_EVENT_PLAYING;
import static com.haochuan.hciptvbasic.webview.PlayerToJS.JsEvent.JS_EVENT_PLAYINGBUFFER;
import static com.haochuan.hciptvbasic.webview.PlayerToJS.JsEvent.JS_EVENT_PREPARING;
import static com.haochuan.hciptvbasic.webview.PlayerToJS.JsEvent.JS_EVENT_RESUME;

public class PlayerToJS {
    private Context context;                        //MainActivity 句柄
    private BaseMediaPlayer baseMediaPlayer;        //播放器
    private WebView webView;


    public PlayerToJS(Context context, WebView webView, BaseMediaPlayer mediaPlayer) {
        this.context = context;
        this.baseMediaPlayer = mediaPlayer;
        this.webView = webView;
    }

    /**
     * JS调用类型
     */
    @StringDef({JS_EVENT_LOG,JS_EVENT_PREPARING,JS_EVENT_PLAYING,
            JS_EVENT_RESUME,JS_EVENT_PAUSE,JS_EVENT_DESTROY,JS_EVENT_PLAYINGBUFFER,
            JS_EVENT_COMPLETE,JS_EVENT_PLAYERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface JsEvent{


        /**
         * 将日志传递给js
         */
        String JS_EVENT_LOG = "javascript:onLog('%s')";

        /*--------------------传给前端的播放器事件--------------------------*/
        String JS_EVENT_PREPARING = "javascript:onPlayerPreparing()";
        String JS_EVENT_PLAYING = "javascript:onPlayerPlaying()";
        String JS_EVENT_RESUME = "javascript:onPlayerResume()";
        String JS_EVENT_PAUSE = "javascript:onPlayerPause()";
        String JS_EVENT_DESTROY="javascript:onPlayerDestroy()";
        String JS_EVENT_PLAYINGBUFFER = "javascript:onPlayingBuffer()";
        String JS_EVENT_COMPLETE = "javascript:onPlayerComplete()";
        String JS_EVENT_PLAYERROR="javascript:onPlayerError(%s,%s)";
    }

    /*--------------------功能性函数-----------------------------*/
    /**
     * 调用js事件
     */
    private void evaluateJavascript(WebView webView, @JsEvent String script) {
        Logger.show(context,"PlayerToJS 执行脚本：" + script);
        if (webView == null) {
            Logger.show(context,"webView对象为空，JS事件调用无法执行");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(script, value -> {
                //此处为 js 返回的结果
                Logger.show(context,value);
            });
        } else {
            webView.loadUrl(script);
        }
    }

    /**-----------------------------------写入前端函数实现-----------------------------------**/


    /*
    * 将log传递给前端
    * */
    public void logToJs(String log){
        evaluateJavascript(webView,
                String.format(JS_EVENT_LOG,log));
    }

    /*---------------------------事件函数---------------------------*/
    /*
     * 播放器准备事件
     * */
    public void onPlayerPreparing(){
        evaluateJavascript(webView,JS_EVENT_PREPARING);
    }

    /*
     * 播放器开始播放事件
     * */
    public void onPlayerPlaying(){
        evaluateJavascript(webView,JS_EVENT_PLAYING);
    }

    /*
     * 播放器继续播放事件
     * */
    public void onPlayerResume(){
        evaluateJavascript(webView,JS_EVENT_RESUME);
    }

    /*
     * 播放器暂停播放事件
     * */
    public void onPlayerPause(){
        evaluateJavascript(webView,JS_EVENT_PAUSE);
    }

    /*
     * 播放器销毁释放事件
     * */
    public void onPlayerDestroy(){
        evaluateJavascript(webView,JS_EVENT_DESTROY);
    }

    /*
     * 播放器缓冲事件
     * */
    public void onPlayingBuffer(){
        evaluateJavascript(webView,JS_EVENT_PLAYINGBUFFER);
    }

    /*
     * 播放器播放完毕事件
     * */
    public void onPlayerComplete(){
        evaluateJavascript(webView,JS_EVENT_COMPLETE);
    }

    /*
     * 播放器播放错误事件
     * */
    public void onPlayerError(int what, int extra){
        evaluateJavascript(webView,String.format(JS_EVENT_PLAYERROR,what,extra));
    }

    /*----------------------------------播放器功能函数----------------------------------------*/

    /*
    * 播放函数
    * */
    @JavascriptInterface
    public void play(String url, int x,int y,int width, int height){
        if(baseMediaPlayer == null){
            Logger.show(context,"播放器为空,不能播放");
            return;
        }
        Activity activity = (Activity)context;
        activity.runOnUiThread(()->{
            initVideoParamsIfNoInit(baseMediaPlayer,x, y, width, height);
            baseMediaPlayer.play(url);
            webView.requestFocus();
        });
    }

    /*
     * 暂停
     * */
    @JavascriptInterface
    public void pause(){
        if(baseMediaPlayer == null){
            Logger.show(context,"播放器为空,不能暂停");
            return;
        }
        baseMediaPlayer.pause();
    }

    /*
     * 恢复
     * */
    @JavascriptInterface
    public void resume(){
        if(baseMediaPlayer == null){
            Logger.show(context,"播放器为空,不能恢复");
            return;
        }
        baseMediaPlayer.resume();
    }

    /*
     * 快进到指定位置
     * */
    @JavascriptInterface
    public void seek(int position){
        if(baseMediaPlayer == null){
            Logger.show(context,"播放器为空,不能拖动");
            return;
        }
        baseMediaPlayer.seek(position);
    }

  /*  *//*
     * 设定播放速率
     * *//*
    @JavascriptInterface
    public void setPlaySpeed(String speed){

    }*/

    /*
     * 资源释放
     * */
    @JavascriptInterface
    public void release(){
        if(baseMediaPlayer == null){
            Logger.show(context,"播放器为空,不能释放资源");
            return;
        }
        baseMediaPlayer.release();
    }

    /*
    * 退出播放
    * */
    @JavascriptInterface
    public void exit(){
        if(baseMediaPlayer == null){
            Logger.show(context,"播放器为空,不能退出");
            return;
        }
        Activity activity = (Activity)context;
        activity.runOnUiThread(this::destroyVideo);
    }

    /**-------------------------------------------功能函数-----------------------------------------------*/


    /*
     * 在视频未初始化的情况下，调用该函数初始化
     * @param x                  播放器x坐标
     * @param y                  播放器y坐标
     * @param width              播放器宽度
     * @param height             播放器高度
     * */
    private void initVideoParamsIfNoInit(BaseMediaPlayer baseMediaPlayer,int x, int y, int width, int height){
        if(baseMediaPlayer == null){
            Logger.show(context,"播放器为空,不能执行initVideoParamsIfNoInit函数");
            return;
        }
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.width = width;
        params.height = height;
        baseMediaPlayer.setLayoutParams(params);

        baseMediaPlayer.setX(x);
        baseMediaPlayer.setY(y);

        if (baseMediaPlayer.getParent() == null) {
            Activity activity = (Activity)context;
            ViewGroup viewGroup = (ViewGroup)activity.getWindow().getDecorView();
            viewGroup.addView(baseMediaPlayer, 0);
        }
    }


    private void destroyVideo(){
        if(baseMediaPlayer != null && baseMediaPlayer.getParent() != null){
            release();
            Activity activity = (Activity)context;
            ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
            viewGroup.removeView(baseMediaPlayer);
        }
    }
}
