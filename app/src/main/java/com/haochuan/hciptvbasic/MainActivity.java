package com.haochuan.hciptvbasic;

import android.content.Intent;
import android.os.Bundle;

import com.haochuan.hciptvbasic.Util.Juge;
import com.haochuan.hciptvbasic.Util.Logger;
import com.haochuan.hciptvbasic.webview.PlayerToJS;

import org.json.JSONObject;

import java.util.Set;


public class MainActivity extends BaseWebActivity {
    private String mBasicUrl = "http://10.255.25.176:8091/sxrj/loading.html";    //入口地址
    private String mIntentParamsUrl = "";                                               //启动参数拼接地址
    private String mIntentParamsJson = "";                                              //启动参数集合json

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    /*    //test
        String url = "https://gzhc-sxrj.oss-cn-shenzhen.aliyuncs.com/gzhc-djbl/djbl01.mp4";
        int x = 0;
        int y = 0;
        int width = ScreenSnap.getScreenWidth(this);
        int height = ScreenSnap.getScreenHeight(this);
        playVideo(url,x,y,width,height);*/
    }





    /**-------------------BaseWebActivity重载函数 start--------------------------*/

    /*
    * 获取WebView入口地址
    * */
    @Override
    protected String getIndexURL() {
        //mIntentParams不能为空，而且不能是纯IP地址(深圳天威VV玩具就是纯IP地址)，才能加到入口地址中
        if(!mIntentParamsUrl.isEmpty() && !Juge.isPureIp(mBasicUrl)){
            mBasicUrl += mIntentParamsUrl;
        }
        return mBasicUrl;
    }

    //处理启动参数，1,将参数添加到启动链接的参数中；2将其转化为json，传给前端
    @Override
    protected void handleIntent(Intent intent) {
        try{
            if(intent != null){
                Bundle bundle = intent.getExtras();
                if(bundle != null){
                    Set<String> keySet = bundle.keySet();
                    JSONObject intentJson = new JSONObject();
                    int i = 0;
                    StringBuilder sb = new StringBuilder(mIntentParamsUrl);
                    for(String key : keySet){
                        String bundleValue = String.valueOf(bundle.get(key));
                        intentJson.put(key,bundleValue);
                        if(i==0 && !mBasicUrl.contains("?")){
                            sb.append("?");
                            sb.append(key);
                            sb.append("=");
                            sb.append(bundleValue);
                        }else{
                            sb.append("&");
                            sb.append(key);
                            sb.append("=");
                            sb.append(bundleValue);
                        }
                        i++;
                    }
                    mIntentParamsJson = sb.toString();
                    mIntentParamsJson = intentJson.toString();
                    Logger.show(this,"mIntentParamsJson:" + mIntentParamsJson);

                }
            }else{
                Logger.show(this,"get intent is null");
            }
        }catch (Exception e){
            Logger.show(this,"get intent exception");
            e.printStackTrace();
        }
    }


    /**-------------------BaseWebActivity重载函数 end--------------------------*/


    /*-----------------------------------功能函数 start----------------------------------*/

    /*
    *  将日志发给前端
    *  @param log    日志内容
    * */

    public void loggerToJs(String log){
        getPlayerToJS().logToJs(log);
    }




}
