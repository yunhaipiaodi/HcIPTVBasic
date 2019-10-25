package com.haochuan.gsyvideo;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.haochuan.core.IVideoPlayer;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

/**
 * 无任何控制ui的播放
 * Created by 许林 on 2017/8/6.
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
        if(iVideoPlayerListener != null){
            iVideoPlayerListener.onPreparing();
        }
    }

    @Override
    protected void changeUiToPlayingShow() {
        super.changeUiToPlayingShow();
        if(iVideoPlayerListener != null){
            iVideoPlayerListener.onPlaying();
        }

    }

    @Override
    public void onPrepared() {
        super.onPrepared();
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
        if(iVideoPlayerListener != null){
            iVideoPlayerListener.onPause();
        }
    }

    @Override
    protected void changeUiToPlayingBufferingShow() {
        super.changeUiToPlayingBufferingShow();
        if(iVideoPlayerListener != null){
            iVideoPlayerListener.onPlayingBuffering();
        }
    }

    @Override
    protected void changeUiToCompleteShow() {
       super.changeUiToCompleteShow();
        if(iVideoPlayerListener != null){
            iVideoPlayerListener.onCompletion();
        }
    }

    @Override
    public void onError(int what, int extra){
        super.changeUiToError();
        if(iVideoPlayerListener != null){
            iVideoPlayerListener.onError(what,extra);
        }
    }


    /**---------------------功能函数------------------*/
    public boolean isPrePared(){
        return mHadPrepared;
    }

    public boolean isPlaying(){
        return (mCurrentState >= 0 && mCurrentState != CURRENT_STATE_NORMAL
                && mCurrentState != CURRENT_STATE_AUTO_COMPLETE
                && mCurrentState != CURRENT_STATE_ERROR && mCurrentState != CURRENT_STATE_PAUSE);
    }

    public Context getCurrentContext(){
        return getActivityContext();
    }

    public void setVideoPlayerListener(@NonNull IVideoPlayer iVideoPlayer){
        this.iVideoPlayerListener = iVideoPlayer;
    }

    public void setStartTime(int time){
        this.startTime = time * 1000;
    }

    public int getCurrentStatus(){
        int currentStatus = 0;
        switch (mCurrentState){
            case 1:
                currentStatus =1;
                break;
            case 2:
                currentStatus =2;
                break;
            case 5:
                currentStatus =3;
                break;
            case 3:
                currentStatus =4;
                break;
            case 6:
                currentStatus =5;
                break;
            default:
                currentStatus =6;
                break;
        }
        return currentStatus;
    }

    public void enableDebug(){
        Debuger.enable();
    }

    /*-----------------*/


}
