package com.haochuan.hciptvbasic.webview;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.haochuan.hciptvbasic.Util.JsUtil;
import com.haochuan.hciptvbasic.Util.Logger;
import com.haochuan.hciptvbasic.Util.MathUtil;
import com.haochuan.hciptvbasic.Util.ScreenSnap;
import com.haochuan.hciptvbasic.video.BaseMediaPlayer;


public class PlayerToJS {
    private Context context;                        //MainActivity 句柄
    private BaseMediaPlayer baseMediaPlayer;        //播放器
    private WebView webView;


    /*--------------------传给前端的播放器事件--------------------------*/
    String JS_EVENT_PREPARING = "javascript:onPlayerPreparing()";
    String JS_EVENT_PLAYING = "javascript:onPlayerPlaying()";
    String JS_EVENT_RESUME = "javascript:onPlayerResume()";
    String JS_EVENT_PAUSE = "javascript:onPlayerPause()";
    String JS_EVENT_DESTROY="javascript:onPlayerDestroy()";
    String JS_EVENT_PLAYINGBUFFER = "javascript:onPlayingBuffer()";
    String JS_EVENT_COMPLETE = "javascript:onPlayerComplete()";
    String JS_EVENT_PLAYERROR="javascript:onPlayerError(%s,%s)";


    public PlayerToJS(Context context, WebView webView, BaseMediaPlayer mediaPlayer) {
        this.context = context;
        this.baseMediaPlayer = mediaPlayer;
        this.webView = webView;
    }



    /*---------------------------事件函数---------------------------*/
    /*
     * 播放器准备事件
     * */
    public void onPlayerPreparing(){
        JsUtil.evaluateJavascript(context,webView,JS_EVENT_PREPARING);
    }

    /*
     * 播放器开始播放事件
     * */
    public void onPlayerPlaying(){
        JsUtil.evaluateJavascript(context,webView,JS_EVENT_PLAYING);
    }

    /*
     * 播放器继续播放事件
     * */
    public void onPlayerResume(){
        JsUtil.evaluateJavascript(context,webView,JS_EVENT_RESUME);
    }

    /*
     * 播放器暂停播放事件
     * */
    public void onPlayerPause(){
        JsUtil.evaluateJavascript(context,webView,JS_EVENT_PAUSE);
    }

    /*
     * 播放器销毁释放事件
     * */
    public void onPlayerDestroy(){
        JsUtil.evaluateJavascript(context,webView,JS_EVENT_DESTROY);
    }

    /*
     * 播放器缓冲事件
     * */
    public void onPlayingBuffer(){
        JsUtil.evaluateJavascript(context,webView,JS_EVENT_PLAYINGBUFFER);
    }

    /*
     * 播放器播放完毕事件
     * */
    public void onPlayerComplete(){
        JsUtil.evaluateJavascript(context,webView,JS_EVENT_COMPLETE);
    }

    /*
     * 播放器播放错误事件
     * */
    public void onPlayerError(int what, int extra){
        JsUtil.evaluateJavascript(context,webView,String.format(JS_EVENT_PLAYERROR,what,extra));
    }

    /*----------------------------------播放器功能函数----------------------------------------*/

    /*
    * 播放函数
    * */
    @JavascriptInterface
    public void play(String url,String seekTime, String x,String y,String width, String height){
        videoPlay(url,seekTime,x,y,width,height);
    }

    /*
     * 暂停
     * */
    @JavascriptInterface
    public void pause(){
        if(baseMediaPlayer == null){
            Logger.e("播放器为空,不能暂停");
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
            Logger.e("播放器为空,不能恢复");
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
            Logger.e("播放器为空,不能拖动");
            return;
        }
        baseMediaPlayer.seek(position);
    }


    /*
     * 资源释放
     * */
    @JavascriptInterface
    public void release(){
        if(baseMediaPlayer == null){
            Logger.e("播放器为空,不能释放资源");
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
            Logger.e("播放器为空,不能退出");
            return;
        }
        Activity activity = (Activity)context;
        activity.runOnUiThread(this::destroyVideo);
    }

    /**-------------------------------------------功能函数-----------------------------------------------*/

    /*
    * 播放
    * */
    private void videoPlay(String url,String seekTime, String x,String y,String width, String height){
        if(baseMediaPlayer == null){
            Logger.e("播放器为空,不能播放");
            return;
        }
        if(MathUtil.isDigitsOnly(x) && MathUtil.isDigitsOnly(y) && MathUtil.isDigitsOnly(width) && MathUtil.isDigitsOnly(height) && MathUtil.isDigitsOnly(seekTime)){
            int screenWidth = ScreenSnap.getScreenWidth(context);
            int screenHeight = ScreenSnap.getScreenHeight(context);
            int transformX = (int) (Float.parseFloat(x) * screenWidth / 1280);
            int transformY = (int) (Float.parseFloat(y) * screenHeight / 720);
            int transformWidth = (int) (Float.parseFloat(width) * screenWidth / 1280);
            int transformHeight = (int) (Float.parseFloat(height) * screenHeight / 720);

            int transformSeekTime = Integer.parseInt(seekTime);
            if(transformSeekTime < 0){
                transformSeekTime = 0;
            }else{
                transformSeekTime *= 1000;
            }
            final int realSeekTime = transformSeekTime;

            Logger.d(String.format("调用小窗口播放。转换坐标(%s, %s)，宽高(%s, %s)", transformX, transformY, transformWidth, transformHeight));

            Activity activity = (Activity)context;
            activity.runOnUiThread(()->{
                initVideoParamsIfNoInit(baseMediaPlayer,transformX, transformY, transformWidth, transformHeight);
                baseMediaPlayer.play(url);
                baseMediaPlayer.setStartTime(realSeekTime);
                webView.requestFocus();
            });
        }



    }


    /*
     * 在视频未初始化的情况下，调用该函数初始化
     * @param x                  播放器x坐标
     * @param y                  播放器y坐标
     * @param width              播放器宽度
     * @param height             播放器高度
     * */
    private void initVideoParamsIfNoInit(BaseMediaPlayer baseMediaPlayer,int x, int y, int width, int height){
        if(baseMediaPlayer == null){
            Logger.e("播放器为空,不能执行initVideoParamsIfNoInit函数");
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
