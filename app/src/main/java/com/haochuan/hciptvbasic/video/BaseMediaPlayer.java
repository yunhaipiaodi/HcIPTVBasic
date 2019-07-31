package com.haochuan.hciptvbasic.video;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class BaseMediaPlayer extends FrameLayout {


    public BaseMediaPlayer(@NonNull Context context) {
        super(context);
    }

    public BaseMediaPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseMediaPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*---------------------------操作函数------------------------*/
    /*
    * 播放视频
    *@param url
    * */
    public abstract void play(String url);


    /*
     * 设置开始时间
     *@param url
     * */
    public abstract void setStartTime(int time);

    /*
     * 恢复
     *@param url
     * */
    public abstract void resume();

    /*
     * 暂停
     * */
    public abstract void pause();

    /*
     * 拖动
     * @param 目标位置，单位毫秒
     * */
    public abstract void seek(int position);




    /*
    * 资源释放
    * */
    public abstract void release();

    /*---------------------------获取播放器状态和参数函数------------------------*/


    /*
     * 当前是否正在播放
     * */
    public abstract boolean isPlaying();


    /*
     * 视频是否准备完毕
     * */
    public abstract boolean isPrePared();


    /*
     * 获得视频时长
     * */
    public abstract int getDuration();


    /*
     * 获得当前播放时长
     * */
    public abstract int getCurrentPlayPosition();


}
