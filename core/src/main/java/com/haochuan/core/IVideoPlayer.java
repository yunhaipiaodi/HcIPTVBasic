package com.haochuan.core;

public interface IVideoPlayer {
    /*---------------事件接口----------------*/
    void onPreparing();

    void onPlaying();

    void onResume();

    void onPause();

    void onDestroy();

    void onPlayingBuffering();

    void onCompletion();

    void onError(int what, int extra);

}
