package com.haochuan.core.util;

import com.haochuan.core.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONUtil {

    //获取Json对象String类型值
    public static String getString(JSONObject json,String key,String defaultValue){
        try{
            Logger.d(String.format("JSONUtil,getString('%s','%s','%s')",
                    json,key,defaultValue));
            return json.has(key)?json.get(key).toString():defaultValue;
        }catch (Exception e){
            e.printStackTrace();
            return defaultValue;
        }
    }

    //获取Json对象int类型值
    public static int getInt(JSONObject json,String key,int defaultValue){
        try{
            Logger.d(String.format("JSONUtil,getInt('%s','%s','%s')",
                    json,key,defaultValue));
            return json.has(key)?Integer.parseInt(json.get(key).toString()):defaultValue;
        }catch (Exception e){
            e.printStackTrace();
            return defaultValue;
        }
    }

    //获取Json对象
    public static JSONObject getJsonObject(JSONObject json,String key){
        try{
            Logger.d(String.format("JSONUtil,getJsonObject('%s','%s')",
                    json,key));
            return json.has(key)?json.getJSONObject(key):null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //获取Json对象
    public static JSONArray getJsonArray(JSONObject json, String key){
        try{
            Logger.d(String.format("JSONUtil,getJsonArray('%s','%s')",
                    json,key));
            return json.has(key)?json.getJSONArray(key):null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


}
