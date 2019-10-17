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

import com.haochuan.hciptvbasic.Util.JSONUtil;
import com.haochuan.hciptvbasic.Util.JsUtil;
import com.haochuan.hciptvbasic.Util.Logger;
import com.haochuan.hciptvbasic.Util.MathUtil;
import com.haochuan.hciptvbasic.Util.MessageCode;
import com.haochuan.hciptvbasic.Util.RegexUtil;
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
        Logger.d("onPlayerResume");
        JsUtil.evaluateJavascript(context,webView,JS_EVENT_RESUME);
    }

    /*
     * 播放器暂停播放事件
     * */
    public void onPlayerPause(){
        Logger.d("onPlayerPause");
        JsUtil.evaluateJavascript(context,webView,JS_EVENT_PAUSE);
    }



    /*
     * 播放器缓冲事件
     * */
    public void onPlayingBuffer(){
        Logger.d("onPlayerBuffer");
        JsUtil.evaluateJavascript(context,webView,JS_EVENT_PLAYINGBUFFER);
    }

    /*
     * 播放器播放完毕事件
     * */
    public void onPlayerComplete(){
        Logger.d("onPlayerComplete");
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
    *  @param url                 播放链接,type参数为1时必填
    *  @param code                播放代码，,type参数为2时必填,该版本没用到
    *  @param type                播放类型，改版本固定用url播放，1,url链接播放；2，传递code值播放；
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
            String url = "";
            String typeStr = JSONUtil.getString(playParam,"type","1");
            int type = 1;
            if(TextUtils.isEmpty(typeStr)){
                type = 1;
            }
            if(MathUtil.isDigitsOnly(typeStr)){
                type = Integer.parseInt(typeStr);
            }else{
                Logger.e(PARAM_ERROR,"调用play函数，type参数错误，type:" + type);
            }
            switch (type){
                case 1:
                    url = JSONUtil.getString(playParam,"url","");
                    break;
                case 2:
                    //该版本没有code获取url的功能，暂缺，请根据实际情况添加
                    break;
                default:
                    url = JSONUtil.getString(playParam,"url","");
                    break;
            }
            if(TextUtils.isEmpty(url)){
                Logger.e(PARAM_ERROR,"调用play函数，url为空，不能执行播放");
                return PARAM_ERROR;
            }
            String seekTime = JSONUtil.getString(playParam,"seek_time","0");
            String x = JSONUtil.getString(playParam,"x","0");
            String y = JSONUtil.getString(playParam,"y","0");
            String width = JSONUtil.getString(playParam,"width","1280");
            String height = JSONUtil.getString(playParam,"height","720");
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
            String x = JSONUtil.getString(changeParam,"x","0");
            String y = JSONUtil.getString(changeParam,"y","0");
            String width = JSONUtil.getString(changeParam,"width","1280");
            String height = JSONUtil.getString(changeParam,"height","720");
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
            Activity activity = (Activity)context;
            activity.runOnUiThread(()->baseMediaPlayer.pause());
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
            Activity activity = (Activity)context;
            activity.runOnUiThread(()->baseMediaPlayer.resume());
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
    public int seek(String paramJson){
        if(baseMediaPlayer == null){
            Logger.e("播放器为空,不能拖动");
            return PLAYER_OBJ_NULL;
        }
        try{
            JSONObject jsonObject = new JSONObject(paramJson);
            int position = JSONUtil.getInt(jsonObject,"time",-1);
            if(position == -1){
                return PARAM_ERROR;
            }
            Activity activity = (Activity)context;
            activity.runOnUiThread(()->baseMediaPlayer.seek(position));
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
    * 状态说明：1，视频准备中；2，播放；3，暂停；4，缓冲；5，播放完成停止；
    * */
    @JavascriptInterface
    public int getPlayerStatus(){
        if(baseMediaPlayer == null){
            Logger.e("播放器为空,不能退出");
            return PLAYER_OBJ_NULL;
        }
        return baseMediaPlayer.getCurrentStatus();
    }

    /*
    * 获得视频时长
    * */
    @JavascriptInterface
    public int getDuration(){
        if(baseMediaPlayer == null){
            Logger.e("播放器为空,不能退出");
            return PLAYER_OBJ_NULL;
        }
        return baseMediaPlayer.getDuration();
    }

    /*
     * 获得当前播放时间
     * */
    @JavascriptInterface
    public int getCurrentPlayTime(){
        if(baseMediaPlayer == null){
            Logger.e("播放器为空,不能退出");
            return PLAYER_OBJ_NULL;
        }
        return baseMediaPlayer.getCurrentPlayPosition();
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
        if(!RegexUtil.isUrl(url)){
            Logger.e(PARAM_ERROR,"调用play函数，url格式不正确，不能执行播放，请检查;url:" + url);
            return PARAM_ERROR;
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
            Logger.w("播放器为空,不能执行initVideoParamsIfNoInit函数");
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
            Logger.e(PLAYER_OBJ_NULL,"baseMediaPlayer is null,不能调用play函数");
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
            Activity activity = (Activity)context;
            activity.runOnUiThread(()->{
                animChanged(fromX,toX,fromY,toY,fromWidth,toWidth,fromHeight,toHeight);
            });
            return SUCCESS;
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
                Activity activity = (Activity)context;
                activity.runOnUiThread(()->baseMediaPlayer.release());
                ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
                viewGroup.removeView(baseMediaPlayer);
            }else{
                Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,不能调用destroyVideo函数");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
