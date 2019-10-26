package com.haochuan.hciptvbasic.test;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.haochuan.hciptvbasic.BaseWebActivity;
import com.haochuan.hciptvbasic.R;

import java.util.Timer;
import java.util.TimerTask;

public class TestActivity extends BaseWebActivity {


    private Timer timer;
    private TimerTask timerTask;
    private String TAG = "TestActivity";

    @Override
    protected String getIndexURL() {
        return "";
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        timer = new Timer();
        Button playBtn = findViewById(R.id.play_btn);
        playBtn.setOnClickListener(v -> {
            String playParamJson = "{\n" +
                    "    \"examine_id\": 200000253,\n" +
                    "    \"url\": \"https://gzhc-sxrj.oss-cn-shenzhen.aliyuncs.com/gzhc-djbl/djbl01.mp4\",\n" +
                    "    \"seek_time\": 5\n" +
                    "}";
            getPlayerToJS().play(playParamJson);
            playBtn.requestFocus();
            checkStatus();
        });

        Button changeBtn = findViewById(R.id.change_btn);
        String changeParam = "{\n" +
                "    \"x\": 100,\n" +
                "    \"y\": 100,\n" +
                "    \"width\": 640,\n" +
                "    \"height\": 360\n" +
                "}";
        changeBtn.setOnClickListener(v ->
                getPlayerToJS().change(changeParam));

        Button pauseBtn = findViewById(R.id.pause_btn);
        pauseBtn.setOnClickListener(v -> getPlayerToJS().pause());

        Button resumeBtn = findViewById(R.id.resume_btn);
        resumeBtn.setOnClickListener(v -> getPlayerToJS().resume());

        Button forwardBtn = findViewById(R.id.forward_btn);
        forwardBtn.setOnClickListener(v -> {
            int currentPos = getMediaPlayer().getCurrentPlayPosition();
            int seekPos = currentPos + 5000;
            if(seekPos > getMediaPlayer().getDuration()){
                seekPos = getMediaPlayer().getDuration() - 1000;
            }
            String param = "{\n" +
                    "  \"time\": 0\n" +
                    "}";
            getPlayerToJS().seek(param);
        });

        Button backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(v -> {
            int currentPos = getMediaPlayer().getCurrentPlayPosition();
            int seekPos = currentPos - 5000;
            if(seekPos < 0){
                seekPos = 1000;
            }
            String param = "{\n" +
                    "  \"time\": 10\n" +
                    "}";
            getPlayerToJS().seek(param);
        });

        Button exitBtn = findViewById(R.id.exit_btn);
        exitBtn.setOnClickListener(v -> getPlayerToJS().stop());

        Button exitAppBtn = findViewById(R.id.exit_app);
        exitAppBtn.setOnClickListener(v -> getUtilToJS().appExit());

        /*String intentJson = new UtilToJS(this,getWebView()).getIntentJson();
        Logger.d("intentJson:" + intentJson);*/

       /* UtilToJS toolToJS = new UtilToJS(this,getWebView());
        String url = "http://117.169.11.222:8018/tv/index.php";
        String paramJson = "{\"m\":\"Home\",\"c\":\"Activity\",\"a\":\"getActStatus\"}";
        String headJson = "{\"cookie\":\"head=123123123131fdfsfsdfs\"}";
        toolToJS.clientWebRequest(url,paramJson,headJson,2,false,"test");*/
        //toolToJS.download("http://202.99.114.74:56251/dudu_youxi/h5/gameList/apk/jiSuKuangBiao.apk");
    }

    private void checkStatus(){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                int status  = getPlayerToJS().getPlayerStatus();
                Log.d(TAG,"播放状态：" + getStatusMsg(status));
            }
        };
        timer.schedule(timerTask,0,1000);
    }

    private String getStatusMsg(int status){
        String statusMsg = "";
        switch (status){
            case 1:
                statusMsg = "视频准备中";
                break;
            case 2:
                statusMsg = "播放";
                break;
            case 3:
                statusMsg = "暂停";
                break;
            case 4:
                statusMsg = "缓冲";
                break;
            case 5:
                statusMsg = "播放完成停止播放";
                break;
            case 6:
                statusMsg = "未播放";
                break;
            default:
                break;
        }
        return statusMsg;
    }

    @Override
    protected void onStart(){
        super.onStart();
        //MemoryMonitor.getInstance().start(FloatCurveView.MEMORY_TYPE_PSS);
    }

    @Override
    protected void onStop(){
        super.onStop();
        //MemoryMonitor.getInstance().stop();
    }



}
