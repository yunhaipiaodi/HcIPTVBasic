package com.haochuan.hciptvbasic;

import android.os.Bundle;


public class MainActivity extends BaseWebActivity {
    private String mBasicUrl = "http://10.255.25.176:8091/sxrj/loading.html";    //入口地址

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
