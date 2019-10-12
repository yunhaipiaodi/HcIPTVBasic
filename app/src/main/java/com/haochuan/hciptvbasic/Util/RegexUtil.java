package com.haochuan.hciptvbasic.Util;

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
}
