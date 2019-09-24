package com.haochuan.hciptvbasic.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.haochuan.hciptvbasic.R;
import com.haochuan.hciptvbasic.Util.Logger;

public class HCGsyVideoPlayer extends BaseMediaPlayer {

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

    public void setVideoPlayerListener(@NonNull IVideoPlayer iVideoPlayer){
        if(mEmptyControlVideo != null){
            mEmptyControlVideo.setVideoPlayerListener(iVideoPlayer);
        }else{
            Logger.w("mEmptyControlVideo is null,can`t setVideoPlayerListener");
        }
    }


    /*----------------------------从父类继承 播放器功能函数----------------------------------*/
    @Override
    public void play(@NonNull String url) {
        mEmptyControlVideo.setUp(url,false,"");
        mEmptyControlVideo.startPlayLogic();
    }

    @Override
    public void setStartTime(int time){
        mEmptyControlVideo.setStartTime(time);
    }

    @Override
    public void resume() {
        mEmptyControlVideo.onVideoResume();
    }

    @Override
    public void pause() {
        mEmptyControlVideo.onVideoPause();
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

    @Override
    public boolean isPlaying() {
        return mEmptyControlVideo.isPlaying();
    }

    @Override
    public boolean isPrePared() {
        return mEmptyControlVideo.isPrePared();
    }

    @Override
    public int getDuration() {
        if(isPrePared()){
            return mEmptyControlVideo.getDuration();
        }else{
            Logger.w("视频未准备，不能获得视频总时长");
            return 0;
        }
    }

    @Override
    public int getCurrentPlayPosition() {
        if(isPrePared()){
            return mEmptyControlVideo.getCurrentPositionWhenPlaying();
        }else{
            Logger.w("视频未准备，不能获得视频当前位置");
            return 0;
        }
    }

    @Override
    public int getCurrentStatus() {
        return mEmptyControlVideo.getCurrentStatus();
    }

    @Override
    public void release(){
        mEmptyControlVideo.release();
    }


}
