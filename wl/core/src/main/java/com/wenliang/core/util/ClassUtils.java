package com.wenliang.core.util;

import com.wenliang.core.container.DefaultBeanNameMap;
import com.wenliang.core.log.Log;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author wenliang
 * @date 2019-07-30
 * 简介：
 */
public class ClassUtils {
    private ClassUtils() {
    }

    /**
     * 判断是否为复杂对象（及基本数据类型及字符串以外的对象）
     * @param parameterType
     * @return
     */
    private boolean isComplexObject(Class<?> parameterType) {
        if (byte.class.isAssignableFrom(parameterType) || Byte.class.isAssignableFrom(parameterType)
                ||int.class.isAssignableFrom(parameterType) || Integer.class.isAssignableFrom(parameterType)
                ||float.class.isAssignableFrom(parameterType) || Float.class.isAssignableFrom(parameterType)
                ||double.class.isAssignableFrom(parameterType) || Double.class.isAssignableFrom(parameterType)
                ||long.class.isAssignableFrom(parameterType) || Long.class.isAssignableFrom(parameterType)
                ||String.class.isAssignableFrom(parameterType)|| Date.class.isAssignableFrom(parameterType)
                ) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 判断是否为数字和字符串
     * @param aClass
     * @return
     */
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
    public static void putValueToInterfaceAndSuper(Class<?> aClass, String value) {
        DefaultBeanNameMap.put(aClass.getName(),value);
        Class<?>[] interfaces = aClass.getInterfaces();
        if (interfaces != null) {
            for (Class<?> anInterface : interfaces) {
                DefaultBeanNameMap.put(anInterface.getName(),value);
                putValueToInterfaceAndSuper(anInterface,value);
            }
        }
        Class<?> superclass = aClass.getSuperclass();
        if (superclass != null) {
            DefaultBeanNameMap.put(superclass.getName(),value);
            putValueToInterfaceAndSuper(superclass, value);
        }

    }

    public static Set<Class<?>> getClassFromPackage(String packageName) {
        Set<Class<?>> classSet = new HashSet<>();
        File file = new File(ClassUtils.class.getResource("/").getPath() + packageName.replace(".", "/"));
        String basePath = file.getAbsolutePath().replace(packageName.replace(".","\\"),"");
        int baseLen = "".equals(packageName)?basePath.length()+1:basePath.length();
        List<String> fileAbsoluteNameList = FileUtils.getFileAbsoluteNameList(file, ".class");
        for (String fileAbsoluteName : fileAbsoluteNameList) {
            String className = fileAbsoluteName.substring(baseLen, fileAbsoluteName.length() - 6).replace("\\", ".");
            Class<?> aClass = null;
            try {
                aClass = Class.forName(className);
                classSet.add(aClass);
            } catch (Exception e) {
                Log.WARN("类加载失败："+className);
            }
        }
        return classSet;
    }

    public static Set<Class<?>> getClassWithAnnotation(String packageName, Class<? extends Annotation> annotation) {
        Set<Class<?>> classSet = new HashSet<>();
        Set<Class<?>> classFromPackage = getClassFromPackage(packageName);
        for (Class<?> aClass : classFromPackage) {
            if (aClass.getAnnotation(annotation) != null) {
                classSet.add(aClass);
            }
        }
        return classSet;
    }
    public static Set<Class<?>> getClassWithAnnotation(String[] packageNames, Class<? extends Annotation> annotation) {
        Set<Class<?>> classSet = new HashSet<>();
        for (int i = 0; i < packageNames.length; i++) {
            Set<Class<?>> classWithAnnotation = getClassWithAnnotation(packageNames[i], annotation);
            classSet.addAll(classWithAnnotation);
        }
        return classSet;
    }
}
