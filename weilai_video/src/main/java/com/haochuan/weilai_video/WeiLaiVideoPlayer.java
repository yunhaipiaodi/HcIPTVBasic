package com.haochuan.weilai_video;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.haochuan.core.BaseMediaPlayer;
import com.haochuan.core.IVideoPlayer;
import com.haochuan.core.Logger;
import com.haochuan.core.util.JSONUtil;

import org.json.JSONObject;

import tv.icntv.been.IcntvPlayerInfo;
import tv.icntv.icntvplayersdk.IcntvPlayer;
import tv.icntv.icntvplayersdk.iICntvPlayInterface;

import static com.haochuan.core.util.MessageCode.PLAYER_OBJ_NULL;

public class WeiLaiVideoPlayer extends BaseMediaPlayer {
    //全局参数
    private IcntvPlayer icntvPlayer = null;             //cntv播放器实例
    private FrameLayout icntvPlayerContainer = null;    //cntv播放器容器
    private IVideoPlayer iVideoPlayer;
    protected boolean mHadPrepared = false;                 //Prepared
    private int playerStatus = 6;
    private int startTime = 0;                                 //播放器开始的时间,单位毫秒


    public WeiLaiVideoPlayer(@NonNull Context context) {
        super(context);
        init(context);
    }

    public WeiLaiVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WeiLaiVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    /*
     * 初始化
     * */
    private void init(Context context){
        View.inflate(context, R.layout.player_wl,this);
        icntvPlayerContainer = findViewById(R.id.video_view);
    }

    /*
    * 设置事件接口
    * */
    @Override
    public void setVideoPlayerListener(@NonNull IVideoPlayer iVideoPlayer){
       this.iVideoPlayer = iVideoPlayer;
    }



    @Override
    public void play(String url,String examineId,String examineType) {
        initIcntvPlayer(url,examineId,examineType);
    }

    @Override
    public void setStartTime(int time) {
        this.startTime = time;
    }

    @Override
    public void resume() {
        if(icntvPlayer == null){
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return;
        }
        if(isPrePared()){
            if(!icntvPlayer.isPlaying()){
                icntvPlayer.startVideo();
                playerStatus = 2;   //播放中
            }else {
                Logger.w("当前已经在播放，不用执行resume");
            }
        }else{
            Logger.w("视频未准备，不能执行resume");
        }
    }

    @Override
    public void pause() {
        if(icntvPlayer == null){
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return;
        }
        if(isPrePared()){
            if(icntvPlayer.isPlaying()){
                icntvPlayer.pauseVideo();
                playerStatus = 3;   //暂停中
            }
        }else{
            Logger.w("视频未准备，不能暂停");
        }
    }

    @Override
    public void seek(int position) {
        if(icntvPlayer == null){
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return;
        }
        if(isPrePared()){
            icntvPlayer.seekTo(position);
        }else{
            Logger.w("视频未准备，不能拖动");
        }
    }

    @Override
    public void release() {
        if(icntvPlayer == null){
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return;
        }
        cntvPlayerRelease();
    }

    @Override
    public boolean isPlaying() {
        if(icntvPlayer == null){
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return false;
        }
        if(isPrePared()){
            return icntvPlayer.isPlaying();
        }else{
            return false;
        }
    }

    @Override
    public boolean isPrePared() {
        return mHadPrepared;
    }

    @Override
    public int getDuration() {
        if(icntvPlayer == null){
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return 0;
        }
        if(isPrePared()){
            return icntvPlayer.getDuration();
        }else{
            return 0;
        }
    }

    @Override
    public int getCurrentPlayPosition() {
        if(icntvPlayer == null){
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return 0;
        }
        if(isPrePared()){
            return icntvPlayer.getCurrentPosition();
        }else{
            return 0;
        }
    }

    @Override
    public int getCurrentStatus() {
        return playerStatus;
    }


    /*---------------------------功能函数------------------------------------*/

    /*
    * 初始化icntvPlayer对象
    * */
    private void initIcntvPlayer(String url,String examineId,String examineType){
        try{
            IcntvPlayerInfo icntvPlayerInfo = new IcntvPlayerInfo();    //播放器所需传入参数实例
            icntvPlayerInfo.setPlayUrl(url);
            icntvPlayerInfo.setApp_id(BuildConfig.icntv_app_id);
            icntvPlayerInfo.setCheckType(examineType);
            icntvPlayerInfo.setProgramID(examineId);
            icntvPlayerInfo.setDuration(0);      //还未实现，等待接口更新
            if (icntvPlayer != null) {
                cntvPlayerRelease();
                icntvPlayer = null;
            }
            icntvPlayer = new IcntvPlayer(getContext(), icntvPlayerContainer, icntvPlayerInfo, iICntvPlayInterface);
            iVideoPlayer.onPreparing();
            playerStatus = 1;   //视频准备中；
        }catch (Exception e){
            e.printStackTrace();
            Logger.e(e.getMessage());
        }
    }

    /**
     * 销毁CNTV播放器
     */
    public void cntvPlayerRelease() {
        if (icntvPlayer == null) {
            Logger.e(PLAYER_OBJ_NULL,"播放器对象为null,退出执行");
            return;
        }
        if (icntvPlayer.isPlaying()) {
            icntvPlayer.pauseVideo();
        }
        icntvPlayer.release();
        icntvPlayer = null;
        mHadPrepared = false;
        playerStatus = 6;
    }

    /*
    * 在播放器准备好后，跳转到传入的startTime处
    * */
    private void seekToStartTime(){
        if(icntvPlayer == null){
            Logger.e(PLAYER_OBJ_NULL,"icntvPlayer is null, can`t seekToStartTime");
            return;
        }
        if(startTime > 0){
            if(startTime >= getDuration()){
                //如果开始时间大于或者等于视频总时长，则跳转到距离结束5秒的位置
                icntvPlayer.seekTo(getDuration() -5000);
            }else{
                icntvPlayer.seekTo(startTime);
            }
        }
    }

    /*
    * 处理未来播放器错误
    * */
    private void handleError(int i, int i1, String s){
        String cnTvError = "";
        switch (i) {
            case 1001:
                cnTvError = "未来电视播放错误. " + "错误类型:"
                        + i + "; 错误值:" + i1 + "; 错误消息:" + "CDN视频源调度失败";
                break;
            case 123456:
                cnTvError = "未来电视播放错误. " + "错误类型:"
                        + i + "; 错误值:" + i1 + "; 错误消息:" + "SDK鉴权失败";
                break;
            default:
                cnTvError = s;
                break;
        }
        Logger.e(cnTvError);
        iVideoPlayer.onError(i,i1);
    }


    /*------------------------接口实现----------------------------------*/
    private iICntvPlayInterface iICntvPlayInterface = new iICntvPlayInterface() {
        @Override
        public void onPrepared() {
            mHadPrepared = true;
            iVideoPlayer.onPlaying();
            playerStatus = 2;
            seekToStartTime();
        }

        @Override
        public void onCompletion() {
            iVideoPlayer.onCompletion();
            playerStatus = 5;
        }

        @Override
        public void onBufferStart(String s) {
            iVideoPlayer.onPlayingBuffering();
            playerStatus = 4;
        }

        @Override
        public void onBufferEnd(String s) {
            iVideoPlayer.onPlaying();
            playerStatus = 2;
        }

        @Override
        public void onError(int i, int i1, String s) {
            mHadPrepared = false;
            playerStatus = 6;
            handleError(i,i1,s);
        }

        @Override
        public void onTimeout() {
            iVideoPlayer.onError(100,10001);
            mHadPrepared = false;
            playerStatus = 6;
        }
    };
}
