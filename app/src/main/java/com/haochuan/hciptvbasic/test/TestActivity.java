package com.haochuan.hciptvbasic.test;

import android.os.Bundle;
import android.widget.Button;

import com.haochuan.hciptvbasic.BaseWebActivity;
import com.haochuan.hciptvbasic.R;
import com.haochuan.hciptvbasic.Util.MyLocation;

public class TestActivity extends BaseWebActivity {

    @Override
    protected String getIndexURL() {
        return "";
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Button playBtn = findViewById(R.id.play_btn);
        playBtn.setOnClickListener(v -> {
            String url = "https://gzhc-sxrj.oss-cn-shenzhen.aliyuncs.com/gzhc-djbl/djbl01.mp4";
            String x = "20";
            String y = "30";
            String width = "640";
            String height = "360";
            getPlayerToJS().play(url,"20",x,y,width,height);
        });

        Button changeBtn = findViewById(R.id.change_btn);
        changeBtn.setOnClickListener(v ->
                getPlayerToJS().change("0","0","1280","720"));

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
            getPlayerToJS().seek(seekPos);
        });

        Button backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(v -> {
            int currentPos = getMediaPlayer().getCurrentPlayPosition();
            int seekPos = currentPos - 5000;
            if(seekPos < 0){
                seekPos = 1000;
            }
            getPlayerToJS().seek(seekPos);
        });

        Button exitBtn = findViewById(R.id.exit_btn);
        exitBtn.setOnClickListener(v -> getPlayerToJS().exit());

        //显示位置
        getLocations();

        /*String intentJson = new ToolToJS(this,getWebView()).getIntentJson();
        Logger.d("intentJson:" + intentJson);*/

       /* ToolToJS toolToJS = new ToolToJS(this,getWebView());
        String url = "http://117.169.11.222:8018/tv/index.php";
        String paramJson = "{\"m\":\"Home\",\"c\":\"Activity\",\"a\":\"getActStatus\"}";
        String headJson = "{\"cookie\":\"head=123123123131fdfsfsdfs\"}";
        toolToJS.clientWebRequest(url,paramJson,headJson,2,false,"test");*/
        //toolToJS.download("http://202.99.114.74:56251/dudu_youxi/h5/gameList/apk/jiSuKuangBiao.apk");
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

    /*
    * 获取位置信息
    * */
    private void getLocations(){
        MyLocation myLocation = new MyLocation(this);
        myLocation.getLocation(this);
    }


}
