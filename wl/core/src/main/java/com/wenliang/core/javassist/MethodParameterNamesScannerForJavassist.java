package com.wenliang.core.javassist;

import com.wenliang.core.log.Log;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wenliang
 * @date 2019-08-16
 * 简介：
 */
public class MethodParameterNamesScannerForJavassist {
    public  static Map<String, String[]> parameterNamesMap = new HashMap<>();
    public static ClassPool classPool = ClassPool.getDefault();
    public static String[] getParameterNames(Method method) {
        String[] paArr = parameterNamesMap.get(method.toString());
        if (paArr != null) {
            return paArr;
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        paArr = new String[parameterTypes.length];
        try {
            CtClass ctClass = classPool.get(method.getDeclaringClass().getName());
            CtClass[] ctParams = new CtClass[paArr.length];
            for (int i = 0; i < paArr.length; i++) {
                ctParams[i] = classPool.getCtClass(parameterTypes[i].getName());
            }
            CtMethod ctMethod = ctClass.getDeclaredMethod(method.getName(), ctParams);
            MethodInfo methodInfo = ctMethod.getMethodInfo();
            LocalVariableAttribute localVariableAttribute = (LocalVariableAttribute) methodInfo.getCodeAttribute().getAttribute(LocalVariableAttribute.tag);
            int pos = Modifier.isStatic(method.getModifiers()) ? 0 : 1;
            for (int i = 0; i < paArr.length; i++) {
                paArr[i] = localVariableAttribute.variableName(i + pos);
            }
            return paArr;
        } catch (NotFoundException e) {
            Log.WARN("获取方法参数失败！",e);
        }
        return paArr;
    }
}
