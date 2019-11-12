package com.haochuan.systemvideo;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.View;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.haochuan.core.BaseMediaPlayer;
import com.haochuan.core.IVideoPlayer;
import com.haochuan.core.Logger;

import static com.haochuan.core.util.MessageCode.PLAYER_OBJ_NULL;

public class SystemVideoPlayer extends BaseMediaPlayer {

    //全局参数
    private VideoView videoView;   //系统播放器对象
    private IVideoPlayer iVideoPlayer; //播放器事件监控
    private MediaPlayer mediaPlayer;
    private int playerStatus = 6;
    protected boolean mHadPrepared = false;                 //Prepared
    private int startTime = 0;//播放器开始的时间,单位毫秒

    public SystemVideoPlayer(@NonNull Context context) {
        super(context);
        init(context);
    }

    public SystemVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SystemVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        View videoGroup = View.inflate(getContext(), R.layout.layout_system_video, this);
        videoView = videoGroup.findViewById(R.id.sys_video);
        initVideoView();
        videoView.resume();
    }

    private void initVideoView(){
        videoView.setOnPreparedListener((mp) ->{
            mediaPlayer = mp;
            iVideoPlayer.onPlaying();
            playerStatus = 2;
            mHadPrepared = true;
            seekToStartTime();
        });

        videoView.setOnInfoListener((mp,what,extra) -> {
            mediaPlayer = mp;
            switch (what){          //信息类型
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    // MediaPlayer暂时暂停内部播放以缓冲更多数据。
                    iVideoPlayer.onPlayingBuffering();
                    playerStatus = 4;
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    // 填充缓冲区后，MediaPlayer正在恢复播放。
                    iVideoPlayer.onPlaying();
                    playerStatus = 2;
                    break;
                default:
                    break;
            }
            return true;
        });

        videoView.setOnErrorListener((mp,what,extra) -> {
            mediaPlayer = mp;
            iVideoPlayer.onError(what,extra);
            playerStatus = 6;
            mHadPrepared = false;
            return true;
        });

        videoView.setOnCompletionListener((mp) ->{
            mediaPlayer = mp;
            iVideoPlayer.onCompletion();
            mHadPrepared = false;
            playerStatus = 5;
        });
    }

    /*-------------------------BaseMediaPlayer继承函数----------------------*/

    @Override
    public void play(String url, String examineId, String examineType) {
        if (videoView == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return;
        }
        videoView.setVideoPath(url);
        videoView.start();
        playerStatus = 1;   //视频准备中；
    }

    @Override
    public void setStartTime(int time) {
        this.startTime = time;
    }

    @Override
    public void resume() {
        if (videoView == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return;
        }
        if(isPrePared()){
            videoView.start();
            playerStatus = 2;   //播放中；
        }else{
            Logger.w("视频未准备好，不能继续播放");
        }
    }

    @Override
    public void pause() {
        if (videoView == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return;
        }
        if(isPrePared()){
            videoView.pause();
            playerStatus = 3;   //暂停中；
        }else{
            Logger.w("视频未准备好，不能暂停");
        }
    }

    @Override
    public void seek(int position) {
        if (videoView == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return;
        }
        if(isPrePared()){
            videoView.seekTo(position);
        }else{
            Logger.w("视频未准备好，不能seek");
        }
    }

    @Override
    public void release() {
        if (videoView == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return;
        }
        videoView.stopPlayback();
        videoView.suspend();
        mHadPrepared = false;
        playerStatus = 6;   //暂停中；
    }

    @Override
    public boolean isPlaying() {
        if (mediaPlayer == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return false;
        }
        return mediaPlayer.isPlaying();
    }

    @Override
    public boolean isPrePared() {
        return mHadPrepared;
    }

    @Override
    public int getDuration() {
        if (mediaPlayer == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return 0;
        }
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPlayPosition() {
        if (mediaPlayer == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return 0;
        }
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public int getCurrentStatus() {
        return playerStatus;
    }

    @Override
    public void setVideoPlayerListener(@NonNull IVideoPlayer iVideoPlayer) {
        this.iVideoPlayer = iVideoPlayer;
    }

    /*----------------------功能函数-----------------------------*/
    /*
     * 在播放器准备好后，跳转到传入的startTime处
     * */
    private void seekToStartTime(){
        if(videoView == null){
            Logger.e(PLAYER_OBJ_NULL,"videoView is null, can`t seekToStartTime");
            return;
        }
        if(startTime > 0){
            if(startTime >= getDuration()){
                //如果开始时间大于或者等于视频总时长，则跳转到距离结束5秒的位置
                videoView.seekTo(getDuration() -5000);
            }else{
                videoView.seekTo(startTime);
            }
        }
    }

}
