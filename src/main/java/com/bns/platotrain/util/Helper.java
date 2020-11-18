package com.bns.platotrain.util;

import com.bns.platotrain.security.ExtendedServletRequestWrapper;

import java.util.Map;

public final class Helper {

    public static boolean isNotEmptyAndNull(String someString) {
        return someString != null && someString.trim().length() != 0;
    }

    public static String getOrDefault(String str, String defaultValue) {
        return isNotEmptyAndNull(str) ? str : defaultValue;
    }

    public static void addHeaderValues(ExtendedServletRequestWrapper requestWrapper, Map<String, Object> map) {
        map.forEach((key,value)->{
            requestWrapper.addHeader(key,value.toString());
        });
    }

    public static boolean isNotNullOrZero(Integer value) {
        return value != null && !value.equals(0);
    }

}
