package com.haochuan.weilai_video.util;

import android.text.TextUtils;

import com.haochuan.core.Logger;
import com.haochuan.core.util.JSONUtil;

import org.json.JSONArray;
import org.json.JSONObject;

public class AdDataUtil {
    public static int getStatus(String json){
        try{
            Logger.d(String.format("AdDataUtil,getStatus('%s')",json));
            return JSONUtil.getInt(new JSONObject(json),"status",0);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

    }
    public static  String getOpenAdJson(String json){
        try{
            Logger.d(String.format("AdDataUtil,getOpenAdJson('%s')",json));
            JSONObject jsonObject = new JSONObject(json);
            JSONObject adspacesJson = JSONUtil.getJsonObject(jsonObject,"adspaces");
            if(adspacesJson == null){
                Logger.e("getOpenAdJson,adspaces is not exist ,return");
                return "";
            }
            JSONArray openArray = JSONUtil.getJsonArray(adspacesJson,"open");
            if(openArray == null){
                Logger.e("getOpenAdJson,open is not exist ,return");
                return "";
            }
            if(openArray.length() == 0){
                Logger.e("getOpenAdJson,open array is 0 length ,return");
                return "";
            }
            return openArray.getJSONObject(0).toString();
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public static  String getMid(String openAdJson){
        try{
            Logger.d(String.format("AdDataUtil,getMid('%s')",openAdJson));
            JSONObject jsonObject = new JSONObject(openAdJson);
            return JSONUtil.getString(jsonObject,"mid","");
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public static  String getAid(String openAdJson){
        try{
            Logger.d(String.format("AdDataUtil,getAid('%s')",openAdJson));
            JSONObject jsonObject = new JSONObject(openAdJson);
            return JSONUtil.getString(jsonObject,"aid","");
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    private static  String getMaterialFirst(String openAdJson){
        try{
            Logger.d(String.format("AdDataUtil,getMaterialFirst('%s')",openAdJson));
            JSONObject jsonObject = new JSONObject(openAdJson);
            JSONArray materialArray = JSONUtil.getJsonArray(jsonObject,"materials");
            if(materialArray == null){
                Logger.e("getMaterialFirst,materials is not exist ,return");
                return "";
            }
            if(materialArray.length() == 0){
                Logger.e("getMaterialFirst,materials array is 0 length ,return");
                return "";
            }
            return materialArray.getJSONObject(0).toString();
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public static  String getAdImageUrl(String openAdJson){
        try{
            Logger.d(String.format("AdDataUtil,getAdImageUrl('%s')",openAdJson));
            String materialJson = getMaterialFirst(openAdJson);
            if(TextUtils.isEmpty(materialJson)){
                Logger.e("getAdImageUrl,material is not exist ,return");
                return "";
            }
            JSONObject jsonObject = new JSONObject(materialJson);
            return JSONUtil.getString(jsonObject,"file_path","");
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public static  int getAdShowTime(String openAdJson){
        try{
            Logger.d(String.format("AdDataUtil,getAdShowTime('%s')",openAdJson));
            String materialJson = getMaterialFirst(openAdJson);
            if(TextUtils.isEmpty(materialJson)){
                Logger.e("getAdShowTime,material is not exist ,return");
                return 0;
            }
            JSONObject jsonObject = new JSONObject(materialJson);
            return JSONUtil.getInt(jsonObject,"play_time",0);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public static  String getMaterialId(String openAdJson){
        try{
            Logger.d(String.format("AdDataUtil,getMaterialId('%s')",openAdJson));
            String materialJson = getMaterialFirst(openAdJson);
            if(TextUtils.isEmpty(materialJson)){
                Logger.e("getMaterialId,material is not exist ,return");
                return "";
            }
            JSONObject jsonObject = new JSONObject(materialJson);
            return JSONUtil.getString(jsonObject,"id","");
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }
}
