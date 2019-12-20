package com.wenliang.core.util;

import com.wenliang.core.container.DefaultBeanNameMap;

import java.util.Map;

/**
 * @author wenliang
 * @date 2019-07-30
 * 简介：
 */
public class ClassUtils {
    private ClassUtils() {
    }

    public static boolean isStringAndNumber(Class<?> aClass) {
        if (int.class.isAssignableFrom(aClass) || Integer.class.isAssignableFrom(aClass)) {
            return true;
        } else if (float.class.isAssignableFrom(aClass) || Float.class.isAssignableFrom(aClass)){
            return true;
        } else if (double.class.isAssignableFrom(aClass) || Double.class.isAssignableFrom(aClass)) {
            return true;
        } else if (long.class.isAssignableFrom(aClass) || Long.class.isAssignableFrom(aClass)) {
            return true;
        }else if (byte.class.isAssignableFrom(aClass) || Byte.class.isAssignableFrom(aClass)) {
            return true;
        } else if (String.class.isAssignableFrom(aClass)) {
            return true;
        }
        return false;
    }

    /**
     * 将某个类的父类及所有实现的接口递归作为key，将value放入指定的map
     * @param aClass
     * @param value
     */
    public static void putValueToIntefaceAndSuper(Class<?> aClass, String value) {
        DefaultBeanNameMap.put(aClass.getName(),value);
        Class<?>[] interfaces = aClass.getInterfaces();
        if (interfaces != null) {
            for (Class<?> anInterface : interfaces) {
                DefaultBeanNameMap.put(anInterface.getName(),value);
                putValueToIntefaceAndSuper(anInterface,value);
            }
        }
        Class<?> superclass = aClass.getSuperclass();
        if (superclass != null) {
            DefaultBeanNameMap.put(superclass.getName(),value);
            putValueToIntefaceAndSuper(superclass, value);
        }

    }
}
