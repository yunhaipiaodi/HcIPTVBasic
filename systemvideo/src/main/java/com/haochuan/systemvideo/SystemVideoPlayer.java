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
import com.haochuan.core.util.MediaStatusCode;

import static com.haochuan.core.util.MessageCode.PLAYER_OBJ_NULL;

public class SystemVideoPlayer extends BaseMediaPlayer {

    //全局参数
    private VideoView videoView;   //系统播放器对象
    private IVideoPlayer iVideoPlayer; //播放器事件监控
    private MediaPlayer mediaPlayer;
    private int playerStatus = MediaStatusCode.STOP;
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
        Logger.d("SystemVideoPlayer,init()");
        View videoGroup = View.inflate(getContext(), R.layout.layout_system_video, this);
        videoView = videoGroup.findViewById(R.id.sys_video);
        initVideoView();
        videoView.resume();
    }

    private void initVideoView(){
        Logger.d("SystemVideoPlayer,initVideoView()");
        videoView.setOnPreparedListener((mp) ->{
            mediaPlayer = mp;
            iVideoPlayer.onPlaying();
            playerStatus = MediaStatusCode.PLAY;
            mHadPrepared = true;
            seekToStartTime();
        });

        videoView.setOnInfoListener((mp,what,extra) -> {
            mediaPlayer = mp;
            switch (what){          //信息类型
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    // MediaPlayer暂时暂停内部播放以缓冲更多数据。
                    iVideoPlayer.onPlayingBuffering();
                    playerStatus = MediaStatusCode.BUFFER;
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    // 填充缓冲区后，MediaPlayer正在恢复播放。
                    iVideoPlayer.onPlaying();
                    playerStatus = MediaStatusCode.PLAY;
                    break;
                default:
                    break;
            }
            return true;
        });

        videoView.setOnErrorListener((mp,what,extra) -> {
            mediaPlayer = mp;
            iVideoPlayer.onError(what,extra);
            playerStatus = MediaStatusCode.STOP;
            mHadPrepared = false;
            return true;
        });

        videoView.setOnCompletionListener((mp) ->{
            mediaPlayer = mp;
            iVideoPlayer.onCompletion();
            mHadPrepared = false;
            playerStatus = MediaStatusCode.COMPLETE;
        });
    }

    /*-------------------------BaseMediaPlayer继承函数----------------------*/

    @Override
    public void play(String url, String examineId, String examineType) {
        Logger.d(String.format("SystemVideoPlayer,play(%s,%s,%s)",
                url,examineId,examineType));
        if (videoView == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return;
        }
        videoView.setVideoPath(url);
        videoView.start();
        playerStatus = MediaStatusCode.PREPARE;  //视频准备中；
    }

    @Override
    public void setStartTime(int time) {
        Logger.d(String.format("SystemVideoPlayer,setStartTime(%s)",
                time));
        this.startTime = time;
    }

    @Override
    public void resume() {
        Logger.d("SystemVideoPlayer,resume()");
        if (videoView == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return;
        }
        if(isPrePared()){
            videoView.start();
            playerStatus = MediaStatusCode.PLAY;   //播放中；
            iVideoPlayer.onResume();
        }else{
            Logger.w("视频未准备好，不能继续播放");
        }
    }

    @Override
    public void pause() {
        Logger.d("SystemVideoPlayer,pause()");
        if (videoView == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return;
        }
        if(isPrePared()){
            videoView.pause();
            playerStatus = MediaStatusCode.PAUSE;   //暂停中；
            iVideoPlayer.onPause();
        }else{
            Logger.w("视频未准备好，不能暂停");
        }
    }

    @Override
    public void seek(int position) {
        Logger.d(String.format("SystemVideoPlayer,seek(%s)",
                position));
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
        Logger.d("SystemVideoPlayer,release()");
        if (videoView == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return;
        }
        videoView.stopPlayback();
        videoView.suspend();
        mHadPrepared = false;
        playerStatus = MediaStatusCode.STOP;   //暂停中；
        iVideoPlayer.onDestroy();
    }

    @Override
    public boolean isPlaying() {
        Logger.d("SystemVideoPlayer,isPlaying()");
        if (mediaPlayer == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return false;
        }
        return mediaPlayer.isPlaying();
    }

    @Override
    public boolean isPrePared() {
        Logger.d("SystemVideoPlayer,isPrePared()");
        return mHadPrepared;
    }

    @Override
    public int getDuration() {
        Logger.d("SystemVideoPlayer,getDuration()");
        if (mediaPlayer == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return 0;
        }
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPlayPosition() {
        Logger.d("SystemVideoPlayer,getCurrentPlayPosition()");
        if (mediaPlayer == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return 0;
        }
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public int getCurrentStatus() {
        Logger.d("SystemVideoPlayer,getCurrentStatus()");
        return playerStatus;
    }

    @Override
    public void setVideoPlayerListener(@NonNull IVideoPlayer iVideoPlayer) {
        Logger.d("SystemVideoPlayer,setVideoPlayerListener()");
        this.iVideoPlayer = iVideoPlayer;
    }

    /*----------------------功能函数-----------------------------*/
    /*
     * 在播放器准备好后，跳转到传入的startTime处
     * */
    private void seekToStartTime(){
        Logger.d("SystemVideoPlayer,seekToStartTime()");
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
