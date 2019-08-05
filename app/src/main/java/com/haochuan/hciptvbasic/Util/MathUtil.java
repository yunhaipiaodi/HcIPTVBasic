package com.haochuan.hciptvbasic.Util;

import java.util.regex.Pattern;

public class MathUtil {

    public static boolean isDigitsOnly(String str) {
        return isInteger(str) || isDouble(str);
    }

    // 判断整数（int）
    private static boolean isInteger(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    //判断浮点数（double和float）
    private static boolean isDouble(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }
}
