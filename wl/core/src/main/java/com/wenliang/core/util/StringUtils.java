package com.wenliang.core.util;

import com.wenliang.core.log.Log;

import java.util.Properties;

/**
 * @author wenliang
 * @date 2019-07-12
 * 简介：
 */
public class StringUtils {
    private StringUtils() {
    }

    /**
     * 将字符串的首字母小写
     * @param str
     * @return
     */
    public static String convertFirstCharToLowerCase(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /**
     * 将字符串的首字母大写
     * @param str
     * @return
     */
    public static String convertFirstCharToUpperCase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 将指定分割符及之间的字符串进行替换，替换值从传入的Properties中获取，获取时以分隔符之间的字符串为key。
     * @param value
     * @param conversionSymbolStart
     * @param conversionSymbolEnd
     * @param properties
     * @return
     */
    public static String convertValueToSymbol(String value, String conversionSymbolStart, String conversionSymbolEnd, Properties properties) {
        int start = -1;
        int end = -1;
        while ((start = value.indexOf(conversionSymbolStart)) != -1 &&( end = value.indexOf(conversionSymbolEnd)) != -1 ) {
            String temp = value.substring(start, end + 1);
            String proName = temp.replace(conversionSymbolStart, "").replace(conversionSymbolEnd, "");
            String proValue = properties.getProperty(proName);
            if (proValue == null) {
                Log.WARN("找不到属性:" + temp);
                break;
            }
            value = value.replace(temp, proValue);
        }
        return value;
    }

    /**
     * 将指定分割符进行替换，替换值从传入的Properties中获取，获取时以分隔符为key。
     * @param value
     * @param conversionSymbol
     * @param properties
     * @return
     */
    public static String convertValueToSymbol(String value, String conversionSymbol, Properties properties) {
        String temp = properties.getProperty(conversionSymbol);
        if (temp == null) {
            Log.WARN("找不到属性:" + temp);
            return value;
        }
        return value.replace(conversionSymbol,temp );
    }

    public static Object convertValueToNumber(String value,Class<?> aClass) {
        if (int.class.isAssignableFrom(aClass) || Integer.class.isAssignableFrom(aClass)) {
            return Integer.parseInt(value);
        } else if (float.class.isAssignableFrom(aClass) || Float.class.isAssignableFrom(aClass)){
            return Float.parseFloat(value);
        } else if (double.class.isAssignableFrom(aClass) || Double.class.isAssignableFrom(aClass)) {
            return Double.parseDouble(value);
        } else if (long.class.isAssignableFrom(aClass) || Long.class.isAssignableFrom(aClass)) {
            return Long.parseLong(value);
        }else if (byte.class.isAssignableFrom(aClass) || Byte.class.isAssignableFrom(aClass)) {
            return Byte.parseByte(value);
        } else if (String.class.isAssignableFrom(aClass)) {
            return value;
        }
        Log.WARN("不支持的数字转换类型: "+value+": "+aClass.getName());
        return value;
    }

    /**
     * 将字符串的下划线去掉并将下划线后面的一个字母转换为大写
     * @param fieldName
     * @return
     */
    public static String ConvertUnderscoreToUppercase(String fieldName) {
        int startIndex;
        String tempFieldName=fieldName;
        try {
            while ((startIndex = tempFieldName.indexOf('_')) > -1) {
                tempFieldName = tempFieldName.replaceFirst("_", "");
                if (tempFieldName.length() > startIndex+1) {
                    tempFieldName = tempFieldName.substring(0, startIndex) + tempFieldName.substring(startIndex, startIndex + 1).toUpperCase() + tempFieldName.substring(startIndex + 1);
                } else if (tempFieldName.length() == startIndex + 1) {
                    tempFieldName = tempFieldName.substring(0, startIndex) + tempFieldName.substring(startIndex, startIndex + 1).toUpperCase();
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            Log.ERROR("转换字符串失败:"+fieldName, e);
        }
        return tempFieldName;
    }


}
