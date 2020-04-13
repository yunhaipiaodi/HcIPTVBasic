package com.haochuan.hciptvbasic.webview;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.haochuan.core.BaseMediaPlayer;
import com.haochuan.core.Logger;
import com.haochuan.core.util.JSONUtil;
import com.haochuan.core.util.JsUtil;
import com.haochuan.core.util.MathUtil;
import com.haochuan.core.util.MediaStatusCode;
import com.haochuan.core.util.RegexUtil;
import com.haochuan.core.util.ScreenSnap;
import com.haochuan.weilai_video.WeiLaiVideoPlayer;

import org.json.JSONObject;

import static com.haochuan.core.util.MessageCode.EXCEPTION_ERROR;
import static com.haochuan.core.util.MessageCode.PARAM_ERROR;
import static com.haochuan.core.util.MessageCode.PLAYER_NO_INIT;
import static com.haochuan.core.util.MessageCode.PLAYER_OBJ_NULL;
import static com.haochuan.core.util.MessageCode.SUCCESS;


public class PlayerToJS {
    private Context context;                        //MainActivity 句柄
    private BaseMediaPlayer baseMediaPlayer;        //播放器
    private WebView webView;


    /*--------------------传给前端的播放器事件--------------------------*/
    private String JS_EVENT_PLAYERROR="javascript:onPlayerError(%s,%s)";
    private String JS_PLAYER_STATUS = "javascript:onPlayerStatus(%s)";


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
        Logger.d("PlayerToJS,onPlayerPreparing()");
        executePlayStatusEvent(MediaStatusCode.PREPARE);
    }

    /*
     * 播放器开始播放事件
     * */
    public void onPlayerPlaying(){
        Logger.d("PlayerToJS,onPlayerPlaying()");
        executePlayStatusEvent(MediaStatusCode.PLAY);
    }

    /*
     * 播放器继续播放事件
     * */
    public void onPlayerResume(){
        Logger.d("PlayerToJS,onPlayerResume()");
        executePlayStatusEvent(MediaStatusCode.PLAY);
    }

    /*
     * 播放器暂停播放事件
     * */
    public void onPlayerPause(){
        Logger.d("PlayerToJS,onPlayerPause()");
        executePlayStatusEvent(MediaStatusCode.PAUSE);
    }

    /*
     * 播放器缓冲事件
     * */
    public void onPlayingBuffer(){
        Logger.d("PlayerToJS,onPlayerBuffer()");
        executePlayStatusEvent(MediaStatusCode.BUFFER);
    }

    /*
     * 播放器播放完毕事件
     * */
    public void onPlayerComplete(){
        Logger.d("PlayerToJS,onPlayerComplete()");
        executePlayStatusEvent(MediaStatusCode.COMPLETE);
    }

    /*
     * 播放器播放错误事件
     * */
    public void onPlayerError(int what, int extra){
        Logger.d(String.format("PlayerToJS,onPlayerError(%s,%s)",what,extra));
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
        Logger.d("PlayerToJS,play(),playParamJson:" + playParamJson);
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
            String examineId = JSONUtil.getString(playParam,"examine_id","");
            String examineType = JSONUtil.getString(playParam,"examine_type","program");
            return videoPlay(url,seekTime,x,y,width,height,examineId,examineType);
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
        Logger.d("PlayerToJS,change(),changeParamJson:" + changeParamJson);
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
        Logger.d("PlayerToJS,pause()");
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
        Logger.d("PlayerToJS,resume()");
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
        Logger.d("PlayerToJS,seek(),paramJson:" + paramJson);
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
        Logger.d("PlayerToJS,stop()");
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
        Logger.d("PlayerToJS,getPlayerStatus()");
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
        Logger.d("PlayerToJS,getDuration()");
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
        Logger.d("PlayerToJS,getCurrentPlayTime()");
        if(baseMediaPlayer == null){
            Logger.e("播放器为空,不能退出");
            return PLAYER_OBJ_NULL;
        }
        return baseMediaPlayer.getCurrentPlayPosition();
    }

    /*
     * 获得未来播放器是否在广告期间
     * */
    @JavascriptInterface
    public int getCNTVPlayerAding(){
        Logger.d("PlayerToJS,getCNTVPlayerAding()");
        int result = 0; //1,广告期间；0，非广告期间
        if(baseMediaPlayer == null){
            Logger.e("播放器为空,不能退出");
            result = 0;
        }
        if(baseMediaPlayer instanceof WeiLaiVideoPlayer){
            WeiLaiVideoPlayer weiLaiVideoPlayer
                    =(WeiLaiVideoPlayer)baseMediaPlayer;
            result = weiLaiVideoPlayer.isAding()?1:0;
        }
        return result;
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
    private int videoPlay(String url,String seekTime, String x,String y,String width, String height,
                          String examineId,String examineType){
        Logger.d(String.format("PlayerToJS,videoPlay(%s,%s,%s,%s,%s,%s,%s,%s)",url,seekTime,x,y,
                width,height,examineId,examineType));
        try{
            if(baseMediaPlayer == null){
                Logger.e(PLAYER_OBJ_NULL,"播放器对象为空,不能播放");
                return PLAYER_OBJ_NULL;
            }
            if(!RegexUtil.isUrl(url)){
                Logger.e(PARAM_ERROR,"调用play函数，url格式不正确，不能执行播放，请检查;url:" + url);
                return PARAM_ERROR;
            }


            if(MathUtil.isDigitsOnly(x) && MathUtil.isDigitsOnly(y) && MathUtil.isDigitsOnly(width) && MathUtil.isDigitsOnly(height) && MathUtil.isDigitsOnly(seekTime)){

                //判断x，y是否小于零
                int Dx = Integer.parseInt(x);
                int Dy = Integer.parseInt(y);
                x = (Dx < 0?"0":x);
                y = (Dy < 0?"0":y);



                int screenWidth = ScreenSnap.getScreenWidth(context);
                int screenHeight = ScreenSnap.getScreenHeight(context);
                int transformX = (int) (Float.parseFloat(x) * screenWidth / 1280);
                int transformY = (int) (Float.parseFloat(y) * screenHeight / 720);
                int transformWidth = (int) (Float.parseFloat(String.valueOf(width)) * screenWidth / 1280);
                int transformHeight = (int) (Float.parseFloat(String.valueOf(height)) * screenHeight / 720);

                int transformSeekTime = Integer.parseInt(seekTime);
                transformSeekTime = transformSeekTime < 0 ? 0 :transformSeekTime;
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
                    baseMediaPlayer.play(url,examineId,examineType);
                    baseMediaPlayer.setStartTime(realSeekTime);
                    webView.requestFocus();
                });
                return SUCCESS;
            }else{
                Logger.e(PARAM_ERROR,String.format("请正确传递play函数参数：x:%s;y:%s;width:%s;height:%s;seekTime:%s",
                        x,y,width,height,seekTime));
                return PARAM_ERROR;
            }
        }catch (Exception e){
            e.printStackTrace();
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
        Logger.d(String.format("PlayerToJS,initVideoParamsIfNoInit(%s,%s,%s,%s)",x,y,
                width,height));
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
        Logger.d(String.format("PlayerToJS,videoChange(%s,%s,%s,%s)",x,y,
                width,height));
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
            Logger.d(String.format("PlayerToJS,animChanged(%s,%s,%s,%s,%s,%s,%s,%s)",fromX,toX,fromY,toY,
                    fromWidth,toWidth,fromHeight,toHeight));
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
            Logger.d("PlayerToJS,destroyVideo()");
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

    private void executePlayStatusEvent(int status){
        Logger.d(String.format("PlayerToJS,executePlayStatusEvent()",status));
        JsUtil.evaluateJavascript(context,webView,String.format(JS_PLAYER_STATUS,status));
    }
}
