package com.haochuan.hciptvbasic.test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.haochuan.hciptvbasic.BaseWebActivity;
import com.haochuan.hciptvbasic.R;
import com.haochuan.hciptvbasic.Util.ScreenSnap;
import com.haochuan.hciptvbasic.webview.PlayerToJS;

public class TestActivity extends BaseWebActivity {

    @Override
    protected String getIndexURL() {
        return "";
    }

    @Override
    protected void handleIntent(Intent intent) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Button playBtn = findViewById(R.id.play_btn);
        playBtn.setOnClickListener(v -> {
            String url = "https://gzhc-sxrj.oss-cn-shenzhen.aliyuncs.com/gzhc-djbl/djbl01.mp4";
            int x = 0;
            int y = 0;
            int width = ScreenSnap.getScreenWidth(TestActivity.this);
            int height = ScreenSnap.getScreenHeight(TestActivity.this);
            getPlayerToJS().play(url,x,y,width,height);
        });

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
    }


}
