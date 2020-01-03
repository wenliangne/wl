package com.wenliang.context.proxy;

import com.wenliang.context.annotations.*;
import com.wenliang.context.cfg.DefaultBeanApplicationContext;
import com.wenliang.context.proxy.ExecutorCommonAspect;
import com.wenliang.core.log.Log;
import com.wenliang.core.util.ClassUtils;

import org.dom4j.Element;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author wenliang
 * @date 2019-08-02
 * 简介：
 */
public class AspectScanner {

    public void scan(String[] packageNames) {
        loadAspectToAnnotation((packageNames));
    }

    public void scan(Element root) {
        loadAspectToXml(root);
    }

    private void loadAspectToXml(Element root) {

    }

    private void loadAspectToAnnotation(String[] packageNames) {
        Set<Class<?>> classForAspect = ClassUtils.getClassWithAnnotation(packageNames,Aspect.class);
        if (classForAspect != null) {
            for (Class<?> aClass : classForAspect) {
                Method[] declaredMethods = aClass.getDeclaredMethods();
                for (int i = 0; i < declaredMethods.length; i++) {
                    createExecutorCommonAspect(AspectBefore.class, declaredMethods[i]);
                    createExecutorCommonAspect(AspectAfter.class,declaredMethods[i]);
                    createExecutorCommonAspect(AspectThrowing.class,declaredMethods[i]);
                    createExecutorCommonAspect(AspectFinally.class,declaredMethods[i]);
                }
            }
        }
    }

    private void createExecutorCommonAspect(Class<? extends Annotation> aClass, Method method) {
        Annotation annotation = method.getAnnotation(aClass);
        if (annotation == null) {
            return;
        }
        ExecutorCommonAspect executorCommonAspect = new ExecutorCommonAspect();
        try {
            executorCommonAspect.setBean(method.getDeclaringClass().newInstance());
            executorCommonAspect.setMethod(method);
            String value = null;
            if (AspectBefore.class.isAssignableFrom(annotation.getClass())) {
                value = ((AspectBefore) annotation).value();
                executorCommonAspect.setAnnotationType(AspectBefore.class);
            } else if (AspectAfter.class.isAssignableFrom(annotation.getClass())){
                value = ((AspectAfter) annotation).value();
                executorCommonAspect.setAnnotationType(AspectAfter.class);
            } else if (AspectThrowing.class.isAssignableFrom(annotation.getClass())){
                value = ((AspectThrowing) annotation).value();
                executorCommonAspect.setAnnotationType(AspectThrowing.class);
            } else if (AspectFinally.class.isAssignableFrom(annotation.getClass())) {
                value = ((AspectFinally) annotation).value();
                executorCommonAspect.setAnnotationType(AspectFinally.class);
            } else {
                throw new RuntimeException("错误的注解！");
            }
            executorCommonAspect.setMatchString(value);
            DefaultBeanApplicationContext.getExecutorCommonAspectList().add(executorCommonAspect);
        } catch (Exception e) {
            Log.ERROR("解析切面失败："+method.toString(),e);
        }




    }

}
