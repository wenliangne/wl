package com.wenliang.core.util;

import java.lang.reflect.Field;

/**
 * @author wenliang
 * @date 2019-07-30
 * 简介：
 */
public class FieldUtils {
    private FieldUtils() {

    }

    /**
     * 用对象转换为数字进行注入
     * @param field
     * @param domain
     * @param value
     * @throws IllegalAccessException
     */
    public static void injectNumber(Field field, Object domain, Object value) throws IllegalAccessException {
        if (int.class.isAssignableFrom(field.getType()) || Integer.class.isAssignableFrom(field.getType())) {
            field.set(domain, Integer.parseInt(value.toString()));
        } else if (float.class.isAssignableFrom(field.getType()) || Float.class.isAssignableFrom(field.getType())){
            field.set(domain, Float.parseFloat(value.toString()));
        } else if (double.class.isAssignableFrom(field.getType()) || Double.class.isAssignableFrom(field.getType())) {
            field.set(domain, Double.parseDouble(value.toString()));
        } else if (long.class.isAssignableFrom(field.getType()) || Long.class.isAssignableFrom(field.getType())) {
            field.set(domain, Long.parseLong(value.toString()));
        }else if (byte.class.isAssignableFrom(field.getType()) || Byte.class.isAssignableFrom(field.getType())) {
            field.set(domain, Byte.parseByte(value.toString()));
        }
    }
}
