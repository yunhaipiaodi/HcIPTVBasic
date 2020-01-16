package com.haochuan.hciptvbasic;

import android.os.Bundle;


public class MainActivity extends BaseWebActivity {
    private String mBasicUrl = "http://120.78.169.79:8090/h5/game_apk/html/video_play.html";    //入口地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    /**-------------------BaseWebActivity重载函数 start--------------------------*/

    /*
    * 获取WebView入口地址
    * */
    @Override
    protected String getIndexURL() {
        return mBasicUrl;
    }



}
