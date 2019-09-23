package com.haochuan.hciptvbasic.webview;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.haochuan.hciptvbasic.Util.JsUtil;
import com.haochuan.hciptvbasic.Util.Logger;
import com.haochuan.hciptvbasic.Util.MathUtil;
import com.haochuan.hciptvbasic.Util.ScreenSnap;
import com.haochuan.hciptvbasic.video.BaseMediaPlayer;

import org.json.JSONObject;

import static com.haochuan.hciptvbasic.Util.MessageCode.EXCEPTION_ERROR;
import static com.haochuan.hciptvbasic.Util.MessageCode.PARAM_ERROR;
import static com.haochuan.hciptvbasic.Util.MessageCode.PLAYER_NO_INIT;
import static com.haochuan.hciptvbasic.Util.MessageCode.PLAYER_OBJ_NULL;
import static com.haochuan.hciptvbasic.Util.MessageCode.SUCCESS;


public class PlayerToJS {
    private Context context;                        //MainActivity 句柄
    private BaseMediaPlayer baseMediaPlayer;        //播放器
    private WebView webView;


    /*--------------------传给前端的播放器事件--------------------------*/
    private String JS_EVENT_PREPARING = "javascript:onPlayerPreparing()";
    private String JS_EVENT_PLAYING = "javascript:onPlayerPlaying()";
    private String JS_EVENT_RESUME = "javascript:onPlayerResume()";
    private String JS_EVENT_PAUSE = "javascript:onPlayerPause()";
    private String JS_EVENT_PLAYINGBUFFER = "javascript:onPlayingBuffer()";
    private String JS_EVENT_COMPLETE = "javascript:onPlayerComplete()";
    private String JS_EVENT_PLAYERROR="javascript:onPlayerError(%s,%s)";


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
    *
     * @param x                  播放器x坐标
     * @param y                  播放器y坐标
     * @param width              播放器宽度
     * @param height             播放器高度
     * @param seekTime           播放初始位置，单位 s
    * */
    @JavascriptInterface
    public int play(String playParamJson){
        Logger.d("调用play函数,参数:" + playParamJson);
        try{
            JSONObject playParam = new JSONObject(playParamJson);
            String url = playParam.has("url")?playParam.get("url").toString():"";
            if(TextUtils.isEmpty(url)){
                Logger.e(PARAM_ERROR,"调用play函数，url为空，不能执行播放");
                return PARAM_ERROR;
            }
            String seekTime = playParam.has("seekTime")?playParam.get("seekTime").toString():"0";
            String x = playParam.has("x")?playParam.get("x").toString():"0";
            String y = playParam.has("y")?playParam.get("y").toString():"0";
            String width = playParam.has("width")?playParam.get("width").toString():"1280";
            String height = playParam.has("height")?playParam.get("height").toString():"720";
            return videoPlay(url,seekTime,x,y,width,height);
        }catch (Exception e){
            e.printStackTrace();
            Logger.e(EXCEPTION_ERROR,"异常抛出：" + e.getMessage());
            return EXCEPTION_ERROR;
        }
    }

    /*
    * 改变播放器尺寸
    * 参数定义同play函数
    * */
    @JavascriptInterface
    public int change(String changeParamJson){
        try{
            JSONObject changeParam = new JSONObject(changeParamJson);
            String x = changeParam.has("x")?changeParam.get("x").toString():"0";
            String y = changeParam.has("y")?changeParam.get("y").toString():"0";
            String width = changeParam.has("width")?changeParam.get("width").toString():"1280";
            String height = changeParam.has("height")?changeParam.get("height").toString():"720";
            return videoChange(x,y,width,height);
        }catch (Exception e){
            e.printStackTrace();
            return EXCEPTION_ERROR;
        }
    }


    /*
     * 暂停
     * */
    @JavascriptInterface
    public int pause(){
        if(baseMediaPlayer == null){
            Logger.e("播放器为空,不能暂停");
            return PLAYER_OBJ_NULL;
        }
        try{
            baseMediaPlayer.pause();
            return SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return EXCEPTION_ERROR;
        }
    }

    /*
     * 恢复
     * */
    @JavascriptInterface
    public int resume(){
        if(baseMediaPlayer == null){
            Logger.e("播放器为空,不能恢复");
            return PLAYER_OBJ_NULL;
        }
        try{
            baseMediaPlayer.resume();
            return SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return EXCEPTION_ERROR;
        }
    }

    /*
     * 快进到指定位置
     * */
    @JavascriptInterface
    public int seek(int position){
        if(baseMediaPlayer == null){
            Logger.e("播放器为空,不能拖动");
            return PLAYER_OBJ_NULL;
        }
        try{
            baseMediaPlayer.seek(position);
            return SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return EXCEPTION_ERROR;
        }
    }


    /*
     * 资源释放
     * */
    @JavascriptInterface
    public int release(){
        if(baseMediaPlayer == null){
            Logger.e("播放器为空,不能释放资源");
            return PLAYER_OBJ_NULL;
        }
        try{
            baseMediaPlayer.release();
            return SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return EXCEPTION_ERROR;
        }
    }

    /*
    * 退出播放
    * */
    @JavascriptInterface
    public int stop(){
        if(baseMediaPlayer == null){
            Logger.e("播放器为空,不能退出");
            return PLAYER_OBJ_NULL;
        }

        try{
            Activity activity = (Activity)context;
            activity.runOnUiThread(this::destroyVideo);
            return SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return EXCEPTION_ERROR;
        }
    }

    /*
    * 获取当前播放状态
    * 状态说明：1，播放；2，暂停；3，播放完成停止；0，其他
    * */
    @JavascriptInterface
    public int getPlayerStatus(){
        if(baseMediaPlayer == null){
            Logger.e("播放器为空,不能退出");
            return PLAYER_OBJ_NULL;
        }
        return baseMediaPlayer.getCurrentStatus();
    }

    /**-------------------------------------------功能函数-----------------------------------------------*/

    /*
    * 播放
    * @param x                  播放器x坐标
    * @param y                  播放器y坐标
    * @param width              播放器宽度
    * @param height             播放器高度
    * @param seekTime           播放初始位置，单位 s
    * */
    private int videoPlay(String url,String seekTime, String x,String y,String width, String height){
        if(baseMediaPlayer == null){
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为空,不能播放");
            return PLAYER_OBJ_NULL;
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

            Logger.d(String.format("调用播放函数。转换坐标(%s, %s)，宽高(%s, %s)", transformX, transformY, transformWidth, transformHeight));

            Activity activity = (Activity)context;
            activity.runOnUiThread(()->{
                initVideoParamsIfNoInit(baseMediaPlayer,transformX, transformY, transformWidth, transformHeight);
                baseMediaPlayer.play(url);
                baseMediaPlayer.setStartTime(realSeekTime);
                webView.requestFocus();
            });
            return SUCCESS;
        }else{
            Logger.e(PARAM_ERROR,String.format("请正确传递play函数参数：x:%s;y:%s;width:%s;height:%s;seekTime:%s",
                    x,y,width,height,seekTime));
            return EXCEPTION_ERROR;
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

    /*
     * @param x                  待改变播放器x坐标
     * @param y                  待改变播放器y坐标
     * @param width              待改变播放器宽度
     * @param height             待改变播放器高度
    * */
    private int videoChange(String x,String y,String width, String height){
        if(baseMediaPlayer == null){
            Logger.w("baseMediaPlayer is null,不能调用play函数");
            return PLAYER_OBJ_NULL;
        }

        if(baseMediaPlayer.getParent() == null){
            Logger.w("当前播放器没有启动,请先调用play函数启动");
            return PLAYER_NO_INIT;
        }

        if(MathUtil.isDigitsOnly(x) && MathUtil.isDigitsOnly(y) && MathUtil.isDigitsOnly(width) && MathUtil.isDigitsOnly(height)){
            int screenWidth = ScreenSnap.getScreenWidth(context);
            int screenHeight = ScreenSnap.getScreenHeight(context);
            int toX = (int) (Float.parseFloat(x) * screenWidth / 1280);
            int toY = (int) (Float.parseFloat(y) * screenHeight / 720);
            int toWidth = (int) (Float.parseFloat(width) * screenWidth / 1280);
            int toHeight = (int) (Float.parseFloat(height) * screenHeight / 720);

            int fromX = (int)baseMediaPlayer.getX();
            int fromY = (int)baseMediaPlayer.getY();
            int fromWidth = baseMediaPlayer.getWidth();
            int fromHeight = baseMediaPlayer.getHeight();
            return animChanged(fromX,toX,fromY,toY,fromWidth,toWidth,fromHeight,toHeight);
        }else{
            Logger.e(PARAM_ERROR,String.format("请正确传递change函数参数：x:%s;y:%s;width:%s;height:%s",
                    x,y,width,height));
            return PARAM_ERROR;
        }
    }

    /*
    * 以动画形式改变播放器尺寸
    * */
    private int animChanged(int fromX, int toX, int fromY, int toY, int fromWidth, int toWidth, int fromHeight, int toHeight) {
        try{
            ValueAnimator animator = new ValueAnimator();
            animator.setValues(
                    PropertyValuesHolder.ofFloat("x", fromX, toX),
                    PropertyValuesHolder.ofFloat("y", fromY, toY),
                    PropertyValuesHolder.ofInt("width", fromWidth, toWidth),
                    PropertyValuesHolder.ofInt("height", fromHeight, toHeight)
            );
            animator.setDuration(200);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(valueAnimator -> {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.width = (int) valueAnimator.getAnimatedValue("width");
                params.height = (int) valueAnimator.getAnimatedValue("height");

                if(baseMediaPlayer != null){
                    Logger.w("baseMediaPlayer is null,不能调用play函数");
                }

                baseMediaPlayer.setLayoutParams(params);

                baseMediaPlayer.setX((Float) valueAnimator.getAnimatedValue("x"));
                baseMediaPlayer.setY((Float) valueAnimator.getAnimatedValue("y"));
            });
            animator.start();
            return SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return EXCEPTION_ERROR;
        }
    }


    private void destroyVideo(){
        try{
            if(baseMediaPlayer != null && baseMediaPlayer.getParent() != null){
                release();
                Activity activity = (Activity)context;
                ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
                viewGroup.removeView(baseMediaPlayer);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
