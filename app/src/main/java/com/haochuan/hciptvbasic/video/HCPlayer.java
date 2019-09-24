package com.haochuan.hciptvbasic.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.haochuan.hciptvbasic.R;
import com.haochuan.hciptvbasic.Util.Logger;

import static com.haochuan.hciptvbasic.Util.MessageCode.PLAYER_OBJ_NULL;
import static com.haochuan.hciptvbasic.Util.MessageCode.SUCCESS;

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
        if(mHcGsyVideoPlayer != null){
            mHcGsyVideoPlayer.resume();
        }else{
            Logger.w("mHcGsyVideoPlayer is null,can`t resume");
        }
    }

    @Override
    public void pause() {
        if(mHcGsyVideoPlayer != null){
            mHcGsyVideoPlayer.pause();
        }else{
            Logger.w("mHcGsyVideoPlayer is null,can`t pause");
        }
    }

    @Override
    public void seek(int position) {
        if(mHcGsyVideoPlayer != null){
            mHcGsyVideoPlayer.seek(position);
        }else{
            Logger.w("mHcGsyVideoPlayer is null,can`t seek");
        }
    }

    @Override
    public void release() {
        if(mHcGsyVideoPlayer != null){
            mHcGsyVideoPlayer.release();
        }else{
            Logger.w("mHcGsyVideoPlayer is null,can`t release");
        }
    }

    @Override
    public boolean isPlaying() {
        if(mHcGsyVideoPlayer != null){
            return mHcGsyVideoPlayer.isPlaying();
        }else{
            Logger.w("mHcGsyVideoPlayer is null,can`t get isPlaying state");
            return false;
        }
    }

    @Override
    public boolean isPrePared() {
        if(mHcGsyVideoPlayer != null){
            return mHcGsyVideoPlayer.isPrePared();
        }else{
            Logger.w("mHcGsyVideoPlayer is null,can`t get prepared state");
            return false;
        }
    }

    @Override
    public int getDuration() {
        if(mHcGsyVideoPlayer != null){
            return mHcGsyVideoPlayer.getDuration();
        }else{
            Logger.w("mHcGsyVideoPlayer is null,can`t get duration");
            return 0;
        }
    }

    @Override
    public int getCurrentPlayPosition() {
        if(mHcGsyVideoPlayer != null){
            return mHcGsyVideoPlayer.getCurrentPlayPosition();
        }else{
            Logger.w("mHcGsyVideoPlayer is null,can`t get current play position");
            return 0;
        }
    }

    @Override
    public int getCurrentStatus() {
        if(mHcGsyVideoPlayer != null){
            return mHcGsyVideoPlayer.getCurrentStatus();
        }else{
            Logger.w("mHcGsyVideoPlayer is null,can`t get current status");
            return 0;
        }
    }

    public int setSpeed(@NonNull float speed){
        mHcGsyVideoPlayer.setSpeed(speed);
        if(mHcGsyVideoPlayer != null){
            mHcGsyVideoPlayer.setSpeed(speed);
            return SUCCESS;
        }else{
            Logger.w("mHcGsyVideoPlayer is null,can`t get current status");
            return PLAYER_OBJ_NULL;
        }
    }



}
