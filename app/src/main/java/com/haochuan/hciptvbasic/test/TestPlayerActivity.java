package com.haochuan.hciptvbasic.test;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Application;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.haochuan.hciptvbasic.R;
import com.haochuan.hciptvbasic.Util.Logger;
import com.haochuan.hciptvbasic.video.HCPlayer;
import com.haochuan.hciptvbasic.video.IVideoPlayer;

import java.math.BigDecimal;
import java.util.Formatter;
import java.util.Locale;

public class TestPlayerActivity extends AppCompatActivity implements IVideoPlayer {

    //页面组件对象
    private HCPlayer hcPlayer;
    private ProgressBar loadingBar;
    private LinearLayout bottomContainer;
    private SeekBar videoProgressBar;
    private TextView showTimeView;

    //全局参数
    private String testUrl = "https://gzhc-sxrj.oss-cn-shenzhen.aliyuncs.com/gzhc-djbl/djbl01.mp4";
    private int duration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_player);

        hcPlayer = findViewById(R.id.hc_player);
        loadingBar = findViewById(R.id.loading);
        bottomContainer = findViewById(R.id.layout_bottom);
        videoProgressBar = findViewById(R.id.progress_bar);
        showTimeView = findViewById(R.id.current_total);

        loadingBar.setMax(100);
        loadingBar.setProgress(0);

        hcPlayer.setIVideoPlayerListener(this);
        hcPlayer.play(testUrl);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if(event.getAction() == KeyEvent.ACTION_DOWN){
                    seekForward();
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if(event.getAction() == KeyEvent.ACTION_DOWN){
                    seekBack();
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
                showExitDialog();
                break;
            default:
                break;
        }

        return super.dispatchKeyEvent(event);
    }



    /*------------------------------具体功能实现------------------------------*/
    //向前拖动
    private void seekForward(){
        hcPlayer.seek(hcPlayer.getCurrentPlayPosition() + 1000);
        showBottomContainer();
    }

    //向后拖动
    private void seekBack(){
        hcPlayer.seek(hcPlayer.getCurrentPlayPosition() - 1000);
        showBottomContainer();
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
                hcPlayer.play(testUrl);
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


    /*--------------------IVideoPlayer 接口实现---------------------*/

    @Override
    public void onPreparing() {
        loadingBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPlaying() {
        loadingBar.setVisibility(View.GONE);
        duration = hcPlayer.getDuration();
        Logger.d("duration:" + duration);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPlayingBuffering() {
        loadingBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCompletion() {
        showExitDialog();
    }

    @Override
    public void onError(int what, int extra) {

    }
}
