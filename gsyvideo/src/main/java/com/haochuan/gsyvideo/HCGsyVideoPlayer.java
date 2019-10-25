package com.haochuan.gsyvideo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.haochuan.core.BaseMediaPlayer;
import com.haochuan.core.IVideoPlayer;
import com.haochuan.core.Logger;


public class HCGsyVideoPlayer extends BaseMediaPlayer{

    private EmptyControlVideoView mEmptyControlVideo;        //GSY播放器实例

    public HCGsyVideoPlayer(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HCGsyVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HCGsyVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }



    /*
    * 初始化
    * */
    private void init(Context context){
        View.inflate(context, R.layout.player_gsy_hc,this);
        mEmptyControlVideo = findViewById(R.id.empty_control_video);
        mEmptyControlVideo.enableDebug();
    }

    @Override
    public void setVideoPlayerListener(@NonNull IVideoPlayer iVideoPlayer){
        if(mEmptyControlVideo != null){
            mEmptyControlVideo.setVideoPlayerListener(iVideoPlayer);
        }else{
            Logger.w("mEmptyControlVideo is null,can`t setVideoPlayerListener");
        }
    }


    /*----------------------------从父类继承 播放器功能函数----------------------------------*/
    @Override
    public void play(String url,String examineId,String examineType) {
        mEmptyControlVideo.setUp(url,false,"");
        mEmptyControlVideo.startPlayLogic();
    }

    @Override
    public void setStartTime(int time){
        mEmptyControlVideo.setStartTime(time);
    }

    @Override
    public void resume() {
        if(isPrePared()){
            mEmptyControlVideo.onVideoResume();
        }else{
            Logger.w("视频未准备，不能执行resume");
        }
    }

    @Override
    public void pause() {
        if(isPrePared()){
            mEmptyControlVideo.onVideoPause();
        }else{
            Logger.w("视频未准备，不能暂停");
        }
    }

    @Override
    public void seek(int position) {
        if(isPrePared()){
            mEmptyControlVideo.seekTo(position);
        }else{
            Logger.w("视频未准备，不能拖动");
        }
    }

    public void setSpeed(float speed){
        mEmptyControlVideo.setSpeed(speed);
    }


    public boolean isPlaying() {
        return mEmptyControlVideo.isPlaying();
    }


    public boolean isPrePared() {
        return mEmptyControlVideo.isPrePared();
    }


    public int getDuration() {
        if(isPrePared()){
            return mEmptyControlVideo.getDuration();
        }else{
            Logger.w("视频未准备，不能获得视频总时长");
            return 0;
        }
    }


    public int getCurrentPlayPosition() {
        if(isPrePared()){
            return mEmptyControlVideo.getCurrentPositionWhenPlaying();
        }else{
            Logger.w("视频未准备，不能获得视频当前位置");
            return 0;
        }
    }


    public int getCurrentStatus() {
        return mEmptyControlVideo.getCurrentStatus();
    }


    public void release(){
        mEmptyControlVideo.release();
    }

}
