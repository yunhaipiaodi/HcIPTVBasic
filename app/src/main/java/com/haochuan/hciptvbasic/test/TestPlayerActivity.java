package com.haochuan.hciptvbasic.test;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.haochuan.core.BaseMediaPlayer;
import com.haochuan.core.IVideoPlayer;
import com.haochuan.core.Logger;
import com.haochuan.hciptvbasic.R;

import java.math.BigDecimal;
import java.util.Formatter;
import java.util.Locale;

public class TestPlayerActivity extends AppCompatActivity implements IVideoPlayer {

    //页面组件对象
    private BaseMediaPlayer hcPlayer;
    private ProgressBar loadingBar;
    private LinearLayout bottomContainer;
    private SeekBar videoProgressBar;
    private TextView showTimeView;
    private LinearLayout seekContainer;
    private TextView seekPercentView;
    private SeekBar seekBar;
    private TextView seekTimeView;

    //全局参数
    private String testUrl = "https://gzhc-sxrj.oss-cn-shenzhen.aliyuncs.com/gzhc-djbl/djbl01.mp4";
    private int duration = 0;
    private int seekPercent = 1;
    private long seekDelay = 2000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_player);

        hcPlayer = findViewById(R.id.hc_player);
        loadingBar = findViewById(R.id.loading);
        bottomContainer = findViewById(R.id.layout_bottom);
        videoProgressBar = findViewById(R.id.progress_bar);
        showTimeView = findViewById(R.id.current_total);
        seekContainer = findViewById(R.id.seek_container);
        seekPercentView = findViewById(R.id.seek_percent);
        seekBar = findViewById(R.id.seek_bar);
        seekTimeView = findViewById(R.id.seek_time);

        loadingBar.setMax(100);
        loadingBar.setProgress(0);

        loadingBar.setMax(100);
        loadingBar.setProgress(0);

        hcPlayer.setVideoPlayerListener(this);
        hcPlayer.play(testUrl,"","");


    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if(event.getAction() == KeyEvent.ACTION_DOWN){
                    seek(true);
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if(event.getAction() == KeyEvent.ACTION_DOWN){
                    seek(false);
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if(event.getAction() == KeyEvent.ACTION_DOWN){
                    showBottomContainer();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if(event.getAction() == KeyEvent.ACTION_DOWN){
                    playOrPause();
                }
                break;
            case KeyEvent.KEYCODE_ENTER:
                if(event.getAction() == KeyEvent.ACTION_DOWN){
                    playOrPause();
                }
                break;
            case KeyEvent.KEYCODE_BACK:
                if(event.getAction() == KeyEvent.ACTION_DOWN){
                    playOrPause();
                    showExitDialog();
                }
                break;
            default:
                break;
        }

        return super.dispatchKeyEvent(event);
    }



    /*------------------------------具体功能实现------------------------------*/
    //向前拖动
    private void seek(boolean back){

        //移除延迟执行任务
        hcPlayer.getRootView().removeCallbacks(delaySeekRunnable);

        //显示seekContainer
        if(seekContainer.getVisibility() != View.VISIBLE){
            //显示进度条,并且读取当前进度
            int currentPosition = hcPlayer.getCurrentPlayPosition();
            int percent = percent(currentPosition,duration);
            seekBar.setProgress(percent);
            seekPercentView.setText(percent+" %");
            seekTimeView.setText(timeToString(currentPosition));
            seekContainer.setVisibility(View.VISIBLE);
        }

        //获取当前进度，根据back是否为真来决定前后拖动进度条
        int curProgress = seekBar.getProgress();
        int seekProgress = back ? (curProgress - seekPercent):(curProgress + seekPercent);
        seekBar.setProgress(seekProgress);
        seekPercentView.setText(seekProgress + " %");
        int seekTime = duration*seekProgress/100;
        String seekTimeStr = timeToString(seekTime);
        Logger.d(String.format("seekTime:%s,seekTimeStr:%s",seekTime,seekTimeStr));
        seekTimeView.setText(seekTimeStr);

        //延迟执行seek任务
        hcPlayer.getRootView().postDelayed(delaySeekRunnable,seekDelay);
    }


    //暂停或者启动
    private void playOrPause(){
        if(hcPlayer.isPrePared()){
            if(hcPlayer.isPlaying()){
                hcPlayer.pause();
                Logger.d("playOrPause,暂停");
                showBottomContainer();
            }else{
                hcPlayer.resume();
                Logger.d("playOrPause,恢复播放");
            }
        }
    }

    //显示底部容器
    private void showBottomContainer(){
        bottomContainer.setVisibility(View.VISIBLE);
        //开始刷新底部容器里的进度条进度和播放时间
        refreshProgressAndTimeImmed();
        //延迟3秒后隐藏
        hideBottomContainerDelay();
    }


    //立即刷新进度和时间
    private void refreshProgressAndTimeImmed(){
        hcPlayer.getRootView().post(showProgressAndTimeRunnable);
        refreshProgressAndTime();
    }

    private void refreshProgressAndTime(){
        hcPlayer.getRootView().postDelayed(showProgressAndTimeRunnable,500L);
    }

    //延迟三秒隐藏底部容器
    private void hideBottomContainerDelay(){
        hcPlayer.getRootView().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideBottomContainer();
            }
        },5000L);
    }

    //隐藏底部容器
    private void hideBottomContainer(){
        bottomContainer.setVisibility(View.INVISIBLE);
        //开始刷新底部容器里的进度条进度和播放时间
        hcPlayer.getRootView().removeCallbacks(showProgressAndTimeRunnable);

    }

    //显示退出框
    private void showExitDialog(){
        new AlertDialog.Builder(this)
                .setTitle("退出")
                .setMessage("确认退出吗?")
                .setNegativeButton("重播", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hcPlayer.play(testUrl,"","");
            }
        }).setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TestPlayerActivity.this.runOnUiThread(() -> {
                    android.os.Process.killProcess(android.os.Process.myPid());   //获取PID
                    System.exit(0);
                });
            }
        }).show();

    }

    //将毫秒转为hh:mm:ss格式
    public static String timeToString(long timeMs) {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        long totalSeconds = timeMs / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 获取比例
     *
     * @param current 分子
     * @param max     分母
     * @return 比例（0-100）
     */
    public static int percent(long current, long max) {
        Logger.d(String.format("current:%s,max:%s",current,max));
        return (int) (halfUp((float) current / max, 2) * zeroCountToNum(2));
    }

    /**
     * 根据count值得到1(count个数的0)的数。例如count为2，则得到100，count为1则得到10，count小于0则得到1
     *
     * @param count 1后补增的0个数
     * @return long
     */
    public static long zeroCountToNum(int count) {
        long num = 1;
        if (count <= 0) {
            return num;
        }
        for (int i = 1; i <= count; i++) {
            num *= 10;
        }
        return num;
    }

    /**
     * 获取四舍五入的值
     *
     * @param value    需要转换的数值
     * @param newScale 小数点保留位数
     * @return 四舍五入后的值。
     */
    public static float halfUp(float value, int newScale) {
        return getBigDecimal(value, newScale, BigDecimal.ROUND_HALF_UP);
    }

    private static float getBigDecimal(float value, int newScale, int roundingMode) {
        BigDecimal bigDecimal = new BigDecimal(value);
        return bigDecimal.setScale(newScale, roundingMode).floatValue();
    }


    /*------------------------Runnable--------------------------*/
    //显示底部栏任务
    private Runnable showProgressAndTimeRunnable =new Runnable() {
        @Override
        public void run() {
            int currentPosition =hcPlayer.getCurrentPlayPosition();
            Logger.d("currentPosition:" + currentPosition);
            //显示右边时间进度
            StringBuilder sb = new StringBuilder();
            sb.append(timeToString(currentPosition));
            sb.append("/");
            sb.append(timeToString(duration));
            showTimeView.setText(sb.toString());

            //显示进度条
            videoProgressBar.setProgress(percent(currentPosition,duration));

            refreshProgressAndTime();
        }
    };

    //延迟执行seek拖动任务
    private Runnable delaySeekRunnable =new Runnable() {
        @Override
        public void run() {
            //获取当前seekBar进度，转化为实际时间数，然后执行seek
            int seekProgress = seekBar.getProgress();
            int seekTime = duration*seekProgress/100;
            Logger.d(String.format("duration:%s,seekProgress:%s,seekTime:%s",
                    duration,seekProgress,seekTime));
            hcPlayer.seek(seekTime);

            //隐藏seekContainer
            seekContainer.setVisibility(View.GONE);

        }
    };


    /*--------------------IVideoPlayer 接口实现---------------------*/

    @Override
    public void onPreparing() {
        Logger.d("onPlayerPreparing");
        loadingBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPlaying() {
        Logger.d("onPlayerPlaying");
        loadingBar.setVisibility(View.GONE);
        duration = hcPlayer.getDuration();
        Logger.d("duration:" + duration);
    }

    @Override
    public void onResume() {
        Logger.d("onPlayerResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Logger.d("onPlayerPause");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPlayingBuffering() {
        Logger.d("onPlayerBuffer");
        loadingBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCompletion() {
        Logger.d("onPlayerComplete");
        showExitDialog();
    }

    @Override
    public void onError(int what, int extra) {

    }
}
