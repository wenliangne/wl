package com.wenliang.core.util;

import com.wenliang.core.asm.v5.MethodParameterNamesScanner;
import com.wenliang.core.javassist.MethodParameterNamesScannerForJavassist;
import com.wenliang.core.log.Log;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author wenliang
 * @date 2019-07-30
 * 简介：
 */
public class MethodUtils {

    private MethodUtils() {

    }

    public static String[] getParameterNames(Method method) {
        Parameter[] parameters = method.getParameters();
        if (parameters.length == 0) {
            return new String[0];
        } else if (parameters[0].getName().equals("arg0")) {
            Log.INFO("请设置带参数编译,以提高执行速率！");
            return MethodParameterNamesScanner.getParameterNames(method);
        } else {
            String[] paArr=new String[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                paArr[i] = parameters[i].getName();
            }
            return paArr;
        }
    }
    public static String[] getParameterNamesForJavassist(Method method) {
        return MethodParameterNamesScannerForJavassist.getParameterNames(method);
    }
}
