package com.haochuan.hciptvbasic;

import android.os.Bundle;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends BaseWebActivity {
    private String mBasicUrl = "http://10.255.25.176:8091/sxrj/loading.html";    //入口地址

    private int testCount = 0;
    private String TAG = "status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //test
        String playParamJson = "{\n" +
                "    \"examine_id\": 200000253,\n" +
                "    \"url\": \"https://gzhc-sxrj.oss-cn-shenzhen.aliyuncs.com/gzhc-djbl/djbl01.mp4\"\n" +
                "}";
        int state = getPlayerToJS().getPlayerStatus();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getPlayerToJS().play(playParamJson);
            }
        },10000);
        /*new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                int state = 0;
                switch (testCount){
                    case 0:
                        *//*String changeParamJson = "{\n" +
                                "    \"x\": 60,\n" +
                                "    \"y\": 60,\n" +
                                "    \"width\": 480,\n" +
                                "    \"height\": 270\n" +
                                "}";
                        getPlayerToJS().change(changeParamJson);*//*

                        break;
                    case 1:
                        getPlayerToJS().pause();
                        state = getPlayerToJS().getPlayerStatus();
                        Log.d(TAG,"播放器状态：" + state);
                        break;
                    case 2:
                        getPlayerToJS().resume();
                        state = getPlayerToJS().getPlayerStatus();
                        Log.d(TAG,"播放器状态：" + state);
                        break;
                    case 3:
                        String param = "{\n" +
                                "  \"time\": 5\n" +
                                "}";
                        getPlayerToJS().seek(param);
                        state = getPlayerToJS().getPlayerStatus();
                        Log.d(TAG,"播放器状态：" + state);
                        break;
                    case 4:
                        getPlayerToJS().stop();
                        state = getPlayerToJS().getPlayerStatus();
                        Log.d(TAG,"播放器状态：" + state);
                        break;
                    case 5:
                        state = getPlayerToJS().getPlayerStatus();
                        Log.d(TAG,"播放器状态：" + state);
                        break;
                }
                testCount++;
            }
        },10000,5000);*/
        //getUtilToJS().appExit();
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
