package com.haochuan.hciptvbasic;

import android.os.Bundle;
import android.util.Log;

import com.haochuan.hciptvbasic.Util.Logger;

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
        /*String playParamJson = "{\n" +
                "    \"url\": \"https://gzhc-sxrj.oss-cn-shenzhen.aliyuncs.com/gzhc-djbl/djbl01.mp4\"\n" +
                "}";
        int state = getPlayerToJS().getPlayerStatus();
        Logger.d("播放器状态：" + state);
        getPlayerToJS().play(playParamJson);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                int state = 0;
                switch (testCount){
                    case 0:
                        String changeParamJson = "{\n" +
                                "    \"x\": 60,\n" +
                                "    \"y\": 60,\n" +
                                "    \"width\": 480,\n" +
                                "    \"height\": 270\n" +
                                "}";
                        getPlayerToJS().change(changeParamJson);
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
                        getPlayerToJS().seek(5);
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
        },0,2000);*/
        /*int requestResult = getUtilToJS().clientWebRequest("{\n" +
                "    \"url\": \"http://117.169.11.222:8018/tv/index.php?m=Home&c=Activity&a=getActStatus\",\n" +
                "    \"method\": 1,\n" +
                "    \"content_type\": \"application/json\",\n" +
                "    \"param_json\": {\n" +
                "        \"act_id\": 123123,\n" +
                "        \"uid\": 123123123\n" +
                "    },\n" +
                "    \"ignore_result\": 0,\n" +
                "    \"tag\": \"11_huodong\"\n" +
                "}");
        Log.d(TAG,"requestResult:" + requestResult);*/
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
