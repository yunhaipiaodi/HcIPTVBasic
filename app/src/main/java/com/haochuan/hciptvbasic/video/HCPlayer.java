package com.haochuan.hciptvbasic.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.haochuan.hciptvbasic.R;
import com.haochuan.hciptvbasic.Util.Logger;

public class HCPlayer extends BaseMediaPlayer implements IVideoPlayer{

    private HCGsyVideoPlayer mHcGsyVideoPlayer;

    private IVideoPlayer iVideoPlayerListener;

    public HCPlayer(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HCPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HCPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    /**--------------------------功能性函数------------------------*/

    /*
    * 视频VIEW初始化
    * */
    private void init(Context context){
        View.inflate(context, R.layout.player_hc,this);
        mHcGsyVideoPlayer = findViewById(R.id.hc_gsy_player);
        mHcGsyVideoPlayer.setVideoPlayerListener(this);
    }

    public void setIVideoPlayerListener(IVideoPlayer listener){
        this.iVideoPlayerListener = listener;
    }

    /*--------------------------播放器通用事件接口-----------------------------*/
    @Override
    public void onPreparing() {
        if(iVideoPlayerListener != null){
            iVideoPlayerListener.onPreparing();
        }else{
            Logger.e("HCPlayer 未设置监听接口");
        }
    }

    @Override
    public void onPlaying() {
        if(iVideoPlayerListener != null){
            iVideoPlayerListener.onPlaying();
        }else{
            Logger.e("HCPlayer 未设置监听接口");
        }
    }

    @Override
    public void onResume() {
        resume();
    }

    @Override
    public void onPause() {
        if(iVideoPlayerListener != null){
            iVideoPlayerListener.onPause();
        }else{
            Logger.e("HCPlayer 未设置监听接口");
        }
    }


    @Override
    public void onDestroy() {
        release();
    }

    @Override
    public void onPlayingBuffering() {
        if(iVideoPlayerListener != null){
            iVideoPlayerListener.onPlayingBuffering();
        }else{
            Logger.e("HCPlayer 未设置监听接口");
        }
    }

    @Override
    public void onCompletion() {
        if(iVideoPlayerListener != null){
            iVideoPlayerListener.onCompletion();
        }else{
            Logger.e("HCPlayer 未设置监听接口");
        }
    }


    @Override
    public void onError(int what, int extra) {
        if(iVideoPlayerListener != null){
            iVideoPlayerListener.onError(what,extra);
        }else{
            Logger.e("HCPlayer 未设置监听接口");
        }
    }

    /*------------------------通用操作接口-------------------------*/
    @Override
    public void play(String url) {
        mHcGsyVideoPlayer.play(url);
    }

    @Override
    public void setStartTime(int time){mHcGsyVideoPlayer.setStartTime(time);}

    @Override
    public void resume() {
        mHcGsyVideoPlayer.resume();
    }

    @Override
    public void pause() {
        mHcGsyVideoPlayer.pause();
    }

    @Override
    public void seek(int position) {
        mHcGsyVideoPlayer.seek(position);
    }

    @Override
    public void release() {
        mHcGsyVideoPlayer.release();
    }

    @Override
    public boolean isPlaying() {
        return mHcGsyVideoPlayer.isPlaying();
    }

    @Override
    public boolean isPrePared() {
        return mHcGsyVideoPlayer.isPrePared();
    }

    @Override
    public int getDuration() {
        return mHcGsyVideoPlayer.getDuration();
    }

    @Override
    public int getCurrentPlayPosition() {
        return mHcGsyVideoPlayer.getCurrentPlayPosition();
    }

    @Override
    public int getCurrentStatus() {
        return 0;
    }

    public void setSpeed(@NonNull float speed){
        mHcGsyVideoPlayer.setSpeed(speed);
    }



}
