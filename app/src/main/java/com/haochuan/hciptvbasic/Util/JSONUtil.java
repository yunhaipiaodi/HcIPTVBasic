package com.haochuan.hciptvbasic.Util;

import org.json.JSONObject;

public class JSONUtil {

    //获取Json对象String类型值
    public static String getString(JSONObject json,String key,String defaultValue){
        try{
            return json.has(key)?json.get(key).toString():defaultValue;
        }catch (Exception e){
            e.printStackTrace();
            return defaultValue;
        }
    }

    //获取Json对象int类型值
    public static int getInt(JSONObject json,String key,int defaultValue){
        try{
            return json.has(key)?Integer.parseInt(json.get(key).toString()):defaultValue;
        }catch (Exception e){
            e.printStackTrace();
            return defaultValue;
        }
    }
}
