package com.haochuan.gsyvideo;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.haochuan.core.IVideoPlayer;
import com.haochuan.core.Logger;
import com.haochuan.core.util.MediaStatusCode;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

/**
 * 无任何控制ui的播放
 * Created by 许林 on 2019/8/6.
 */

public class EmptyControlVideoView extends StandardGSYVideoPlayer {

    IVideoPlayer iVideoPlayerListener ;
    private int startTime = 0;                                 //播放器开始的时间,单位毫秒

    public EmptyControlVideoView(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public EmptyControlVideoView(Context context) {
        super(context);
    }

    public EmptyControlVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public int getLayoutId() {
        return R.layout.view_video_control_empty;
    }

    @Override
    protected void touchSurfaceMoveFullLogic(float absDeltaX, float absDeltaY) {
        super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY);
        //不给触摸快进，如果需要，屏蔽下方代码即可
        mChangePosition = false;

        //不给触摸音量，如果需要，屏蔽下方代码即可
        mChangeVolume = false;

        //不给触摸亮度，如果需要，屏蔽下方代码即可
        mBrightness = false;
    }

    @Override
    protected void touchDoubleUp() {
        //super.touchDoubleUp();
        //不需要双击暂停
    }
    /*---------------------------播放器生命周期-----------------------------*/
    @Override
    protected void changeUiToPreparingShow() {
        super.changeUiToPreparingShow();
        Logger.d("EmptyControlVideoView,changeUiToPreparingShow()");
        if(iVideoPlayerListener != null){
            iVideoPlayerListener.onPreparing();
        }
    }

    @Override
    protected void changeUiToPlayingShow() {
        super.changeUiToPlayingShow();
        Logger.d("EmptyControlVideoView,changeUiToPlayingShow()");
        if(iVideoPlayerListener != null){
            iVideoPlayerListener.onPlaying();
        }

    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        Logger.d("EmptyControlVideoView,onPrepared()");
        if(startTime > 0){
            if(startTime >= getDuration()){
                //如果开始时间大于或者等于视频总时长，则跳转到距离结束5秒的位置
                seekTo(getDuration() -5000);
            }else{
                seekTo(startTime);
            }
        }
    }

    @Override
    protected void changeUiToPauseShow() {
        super.changeUiToPauseShow();
        Logger.d("EmptyControlVideoView,changeUiToPauseShow()");
        if(iVideoPlayerListener != null){
            iVideoPlayerListener.onPause();
        }
    }

    @Override
    protected void changeUiToPlayingBufferingShow() {
        super.changeUiToPlayingBufferingShow();
        Logger.d("EmptyControlVideoView,changeUiToPlayingBufferingShow()");
        if(iVideoPlayerListener != null){
            iVideoPlayerListener.onPlayingBuffering();
        }
    }

    @Override
    protected void changeUiToCompleteShow() {
        super.changeUiToCompleteShow();
        Logger.d("EmptyControlVideoView,changeUiToCompleteShow()");
        if(iVideoPlayerListener != null){
            iVideoPlayerListener.onCompletion();
        }
    }

    @Override
    public void onError(int what, int extra){
        super.changeUiToError();
        Logger.d(String.format("EmptyControlVideoView,onError(%s,%s)",what,extra));
        if(iVideoPlayerListener != null){
            iVideoPlayerListener.onError(what,extra);
        }
    }


    /**---------------------功能函数------------------*/
    public boolean isPrePared(){
        Logger.d("EmptyControlVideoView,isPrePared()");
        return mHadPrepared;
    }

    public boolean isPlaying(){
        Logger.d("EmptyControlVideoView,isPlaying()");
        return (mCurrentState >= 0 && mCurrentState != CURRENT_STATE_NORMAL
                && mCurrentState != CURRENT_STATE_AUTO_COMPLETE
                && mCurrentState != CURRENT_STATE_ERROR && mCurrentState != CURRENT_STATE_PAUSE);
    }

    public Context getCurrentContext(){
        return getActivityContext();
    }

    public void setVideoPlayerListener(@NonNull IVideoPlayer iVideoPlayer){
        Logger.d("EmptyControlVideoView,setVideoPlayerListener()");
        this.iVideoPlayerListener = iVideoPlayer;
    }

    public void setStartTime(int time){
        Logger.d(String.format("EmptyControlVideoView,setStartTime(%s)",time));
        this.startTime = time * 1000;
    }

    public int getCurrentStatus(){
        Logger.d("EmptyControlVideoView,getCurrentStatus()");
        int currentStatus = 6;
        switch (mCurrentState){
            case 1:
                currentStatus = MediaStatusCode.PREPARE;
                break;
            case 2:
                currentStatus = MediaStatusCode.PLAY;
                break;
            case 5:
                currentStatus = MediaStatusCode.PAUSE;
                break;
            case 3:
                currentStatus = MediaStatusCode.BUFFER;
                break;
            case 6:
                currentStatus = MediaStatusCode.COMPLETE;
                break;
            default:
                currentStatus = MediaStatusCode.STOP;
                break;
        }
        return currentStatus;
    }

    public void enableDebug(){
        Debuger.enable();
    }

    /*-----------------*/


}
