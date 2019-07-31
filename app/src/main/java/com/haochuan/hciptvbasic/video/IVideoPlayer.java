package com.haochuan.hciptvbasic.video;

public interface IVideoPlayer {
    void onPreparing();

    void onPlaying();

    void onResume();

    void onPause();

    void onDestroy();

    void onPlayingBuffering();

    void onCompletion();

    void onError(int what, int extra);
}
