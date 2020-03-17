package com.haochuan.hciptvbasic;

import android.os.Bundle;

import com.haochuan.core.Logger;
import com.haochuan.core.http.DownloadServer;
import com.haochuan.core.http.RequestServer;


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


    //因为重写了onBackPress方法,所以在onDestroy中复写方法不会回调,相应操作都放在该方法中进行
    @Override
    public void AppExit() {
        //关掉网络请求队列
        RequestServer.getInstance().stop();
        DownloadServer.getInstance().stop();
        super.AppExit();
    }
}
