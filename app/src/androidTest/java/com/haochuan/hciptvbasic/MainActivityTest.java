package com.haochuan.hciptvbasic;

import android.content.Context;


import androidx.test.platform.app.InstrumentationRegistry;

import androidx.test.rule.ActivityTestRule;

import com.haochuan.hciptvbasic.Util.ScreenSnap;

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
}