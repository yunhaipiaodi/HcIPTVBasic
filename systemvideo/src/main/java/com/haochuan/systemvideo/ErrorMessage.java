package com.haochuan.systemvideo;

import android.media.MediaPlayer;

public class ErrorMessage {

    public static final int EXTRA_ERROR_IO = MediaPlayer.MEDIA_ERROR_IO;
    public static final String MSG_ERROR_IO = "文件/网络连接异常。";

    public static final int EXTRA_ERROR_MALFORMED = MediaPlayer.MEDIA_ERROR_MALFORMED;
    public static final String MSG_ERROR_MALFORMED = "比特流不符合相关的编码标准或文件规范。";

    public static final int EXTRA_ERROR_UNSUPPORTED = MediaPlayer.MEDIA_ERROR_UNSUPPORTED;
    public static final String MSG_ERROR_UNSUPPORTED = "比特流符合相关的编码标准或文件规范，但媒体框架不支持该功能。";

    public static final int EXTRA_ERROR_TIMED_OUT = MediaPlayer.MEDIA_ERROR_TIMED_OUT;
    public static final String MSG_ERROR_TIMED_OUT = "有些操作需要很长时间才能完成，通常超过3-5秒。";

    public static final int EXTRA_ERROR_SYSTEM = -2147483648; // - low-level system error.
    public static final String MSG_ERROR_SYSTEM = "低级系统错误。";



    public static final int WHAT_ERROR_UNKNOWN = MediaPlayer.MEDIA_ERROR_UNKNOWN;
    public static final String MSG_ERROR_UNKNOWN = "未指定的媒体播放器错误。";

    public static final int WHAT_ERROR_SERVER_DIED = MediaPlayer.MEDIA_ERROR_SERVER_DIED;
    public static final String MSG_ERROR_SERVER_DIED = "媒体服务器已挂。应用程序必须释放MediaPlayer对象并实例化一个新对象。";


    public static final int INFO_VIDEO_TRACK_LAGGING = MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING;
    public static final String MSG_ERROR_VIDEO_TRACK_LAGGING = "视频对于解码器而言过于复杂：它无法足够快地解码帧。可能只有音频在这个阶段播放得很好。";

    public static final int INFO_BAD_INTERLEAVING = MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING;
    public static final String MSG_ERROR_BAD_INTERLEAVING = "错误的交织意味着媒体已经被不正确地交织或者根本不被交织，例如，所有的视频样本首先是所有的音频样本。视频正在播放，但可能会发生大量磁盘搜索。";

    public static final int INFO_NOT_SEEKABLE = MediaPlayer.MEDIA_INFO_NOT_SEEKABLE;
    public static final String MSG_ERROR_NOT_SEEKABLE = "无法搜索媒体（例如直播）。";

    public static final int INFO_NOT_UNSUPPORTED_SUBTITLE = MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE;
    public static final String MSG_ERROR_UNSUPPORTED_SUBTITLE = "媒体框架不支持字幕轨道。";

    public static final int INFO_NOT_SUBTITLE_TIMED_OUT = MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT;
    public static final String MSG_ERROR_SUBTITLE_TIMED_OUT = "阅读字幕轨道需要太长时间。";

    public static final int INFO_NOT_NETWORK_BANDWIDTH = 703;// - bandwidth information is available (as extra kbps)
    public static final String MSG_ERROR_NETWORK_BANDWIDTH = "- 带宽信息可用（额外kbps）";
}

