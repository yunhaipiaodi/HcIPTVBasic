package com.haochuan.gsyvideo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.haochuan.core.BaseMediaPlayer;
import com.haochuan.core.IVideoPlayer;
import com.haochuan.core.Logger;

import static com.haochuan.core.util.MessageCode.PLAYER_OBJ_NULL;


public class HCGsyVideoPlayer extends BaseMediaPlayer{

    private EmptyControlVideoView mEmptyControlVideo;        //GSY播放器实例
    private IVideoPlayer iVideoPlayer; //播放器事件监控

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
        Logger.d("HCGsyVideoPlayer,init()");
        View.inflate(context, R.layout.player_gsy_hc,this);
        mEmptyControlVideo = findViewById(R.id.empty_control_video);
        mEmptyControlVideo.enableDebug();
    }

    @Override
    public void setVideoPlayerListener(@NonNull IVideoPlayer iVideoPlayer){
        Logger.d("HCGsyVideoPlayer,setVideoPlayerListener()");
        this.iVideoPlayer = iVideoPlayer;
        if(mEmptyControlVideo != null){
            mEmptyControlVideo.setVideoPlayerListener(iVideoPlayer);
        }else{
            Logger.w("mEmptyControlVideo is null,can`t setVideoPlayerListener");
        }
    }


    /*----------------------------从父类继承 播放器功能函数----------------------------------*/
    @Override
    public void play(String url,String examineId,String examineType) {
        Logger.d(String.format("HCGsyVideoPlayer,play(%s,%s,%s)",
                url,examineId,examineType));
        if (mEmptyControlVideo == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return;
        }
        mEmptyControlVideo.setUp(url,false,"");
        mEmptyControlVideo.startPlayLogic();
    }

    @Override
    public void setStartTime(int time){
        Logger.d(String.format("HCGsyVideoPlayer,setStartTime(%s)",
                time));
        if (mEmptyControlVideo == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return;
        }
        mEmptyControlVideo.setStartTime(time);
    }

    @Override
    public void resume() {
        Logger.d("HCGsyVideoPlayer,resume()");
        if (mEmptyControlVideo == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return;
        }
        if(isPrePared()){
            mEmptyControlVideo.onVideoResume();
            iVideoPlayer.onResume();
        }else{
            Logger.w("视频未准备，不能执行resume");
        }
    }

    @Override
    public void pause() {
        Logger.d("HCGsyVideoPlayer,pause()");
        if (mEmptyControlVideo == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return;
        }
        if(isPrePared()){
            mEmptyControlVideo.onVideoPause();
            iVideoPlayer.onPause();
        }else{
            Logger.w("视频未准备，不能暂停");
        }
    }

    @Override
    public void seek(int position) {
        Logger.d(String.format("HCGsyVideoPlayer,seek(%s)",
                position));
        if (mEmptyControlVideo == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return;
        }
        if(isPrePared()){
            mEmptyControlVideo.seekTo(position);
        }else{
            Logger.w("视频未准备，不能拖动");
        }
    }

    public void setSpeed(float speed){
        Logger.d(String.format("HCGsyVideoPlayer,setSpeed(%s)",
                speed));
        if (mEmptyControlVideo == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return;
        }
        mEmptyControlVideo.setSpeed(speed);
    }


    public boolean isPlaying() {
        Logger.d("HCGsyVideoPlayer,isPlaying()");
        if (mEmptyControlVideo == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return false;
        }
        return mEmptyControlVideo.isPlaying();
    }


    public boolean isPrePared() {
        Logger.d("HCGsyVideoPlayer,isPrePared()");
        if (mEmptyControlVideo == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return false;
        }
        return mEmptyControlVideo.isPrePared();
    }


    public int getDuration() {
        Logger.d("HCGsyVideoPlayer,getDuration()");
        if (mEmptyControlVideo == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return 0;
        }
        if(isPrePared()){
            return mEmptyControlVideo.getDuration();
        }else{
            Logger.w("视频未准备，不能获得视频总时长");
            return 0;
        }
    }


    public int getCurrentPlayPosition() {
        Logger.d("HCGsyVideoPlayer,getCurrentPlayPosition()");
        if (mEmptyControlVideo == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return 0;
        }
        if(isPrePared()){
            return mEmptyControlVideo.getCurrentPositionWhenPlaying();
        }else{
            Logger.w("视频未准备，不能获得视频当前位置");
            return 0;
        }
    }


    public int getCurrentStatus() {
        Logger.d("HCGsyVideoPlayer,getCurrentStatus()");
        if (mEmptyControlVideo == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return 6;
        }
        return mEmptyControlVideo.getCurrentStatus();
    }


    public void release(){
        Logger.d("HCGsyVideoPlayer,release()");
        if (mEmptyControlVideo == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return;
        }
        mEmptyControlVideo.release();
        iVideoPlayer.onDestroy();
    }

}
