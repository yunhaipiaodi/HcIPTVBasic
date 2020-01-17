package com.haochuan.hciptvbasic;

import android.os.Bundle;

import com.haochuan.core.Logger;


public class MainActivity extends BaseWebActivity {
    private String mBasicUrl = "http://120.78.169.79:8090/h5/game_apk/html/video_play.html";    //入口地址

    private int testCount = 0;
    private String TAG = "status";

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
        Logger.d("getIndexURL(),mBasicUrl:"+mBasicUrl);
        return mBasicUrl;
    }



}
