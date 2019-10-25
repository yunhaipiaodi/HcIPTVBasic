package com.haochuan.core.util;

public class Juge {
    //判断当前是不是纯IP地址
    public static boolean isPureIp(String url){
        url = url.replace("http://","");
        return !url.contains("/");
    }
}
