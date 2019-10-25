package com.haochuan.core.util;

import java.util.regex.Pattern;

public class RegexUtil {
    /*
     * 判断字符串是否为正确的url
     * */
    public static boolean isUrl(String url){
        if (null == url || "".equals(url)) {
            return false;
        }
        Pattern pattern = Pattern.compile("(http?|https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
        return pattern.matcher(url).matches();
    }

    /*
     * 判断字符串是否为正确的包名或者类名
     * */
    public static boolean isPackageName(String name){
        if (null == name || "".equals(name)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^([a-zA-Z_][a-zA-Z0-9_]*)+([.][a-zA-Z_][a-zA-Z0-9_]*)+$");
        return pattern.matcher(name).matches();
    }
}
