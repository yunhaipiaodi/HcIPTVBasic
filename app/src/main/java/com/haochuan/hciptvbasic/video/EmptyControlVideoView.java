package com.haochuan.hciptvbasic.video;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.haochuan.hciptvbasic.R;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

/**
 * 无任何控制ui的播放
 * Created by 许林 on 2017/8/6.
 */

public class EmptyControlVideoView extends StandardGSYVideoPlayer {

    IVideoPlayer iVideoPlayerListener ;
    private int startTime = 0;                                 //播放器开始的时间

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
            seekTo(startTime);
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

    public Context getCurrentContext(){
        return getActivityContext();
    }

    public void setVideoPlayerListener(@NonNull IVideoPlayer iVideoPlayer){
        this.iVideoPlayerListener = iVideoPlayer;
    }

    public void setStartTime(int time){
        this.startTime = time;
    }

    /*-----------------*/


}
