package com.haochuan.hciptvbasic;

import android.content.Context;


import androidx.test.platform.app.InstrumentationRegistry;

import androidx.test.rule.ActivityTestRule;

import com.haochuan.hciptvbasic.Util.Logger;
import com.haochuan.hciptvbasic.Util.ScreenSnap;
import com.haochuan.hciptvbasic.webview.PayToJS;
import com.haochuan.hciptvbasic.webview.ToolToJS;

import org.junit.Rule;
import org.junit.Test;


public class MainActivityTest{

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule
            = new ActivityTestRule<>(MainActivity.class);



   private Context getContext(){
       return InstrumentationRegistry.getInstrumentation().getContext();
   }



    @Test
    public void playTest(){
       try{
           MainActivity activity = mActivityRule.getActivity();
           String url = "https://gzhc-sxrj.oss-cn-shenzhen.aliyuncs.com/gzhc-djbl/djbl01.mp4";
           int x = 0;
           int y = 0;
           int width = ScreenSnap.getScreenWidth(getContext());
           int height = ScreenSnap.getScreenHeight(getContext());

       }catch (Throwable throwable) {
           throwable.printStackTrace();
       }

    }

    @Test
    public void testPayToJs(){
        MainActivity activity = mActivityRule.getActivity();
        PayToJS payToJS = new PayToJS(getContext(),activity.getWebView());
        payToJS.getUserId();
    }

    @Test
    public void testToolToJs(){
        MainActivity activity = mActivityRule.getActivity();
        ToolToJS toolToJS = new ToolToJS(activity,activity.getWebView());
        String filePath = "/storage/emulated/0/Android/data/com.haochuan.hciptvbasic/cache/temp/com.haochuan.hciptvbasic1.apk";
        String url = "http://117.169.11.222:8018/tv/index.php?m=Home&c=Activity&a=getActStatus";
        String paramJson = "{\"m\":\"Home\",\"c\":\"Activity\",\"a\":\"getActStatus\"}";
        String headJson = "{\"cookie\":\"head=123123123131fdfsfsdfs\"}";
        toolToJS.clientWebRequest(url,paramJson,headJson,2,false,"test");
        //Logger.d("md5:" + md5);
        //toolToJS.download("http://202.99.114.74:56251/dudu_youxi/h5/gameList/apk/jiSuKuangBiao.apk");

    }
}