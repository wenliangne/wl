package com.wenliang.context.proxy;

import java.lang.reflect.Method;

/**
 * @author wenliang
 * @date 2019-07-31
 * 简介：
 */
public class ExecutorCommonAspect {
    private String matchString;
    private AspectMatcher aspectMatcher;
    private Object bean;
    private Method method;
    private Class<?> annotationType;



    public String getMatchString() {
        return matchString;
    }

    public void setMatchString(String matchString) {
        this.matchString = matchString;
        this.aspectMatcher = new AspectMatcher(matchString);
    }

    public void setMatchString(Class<?> aClass) {
        this.aspectMatcher = new AspectMatcher(aClass);
        this.matchString = this.aspectMatcher.getMatchString();
    }

    public void setMatchString(Class<?> aClass,String matcher) {
        this.aspectMatcher = new AspectMatcher(aClass,matcher);
        this.matchString = this.aspectMatcher.getMatchString();
    }

    public void setMatchString(Class<?> aClass,Method method) {
        this.aspectMatcher = new AspectMatcher(aClass,method);
        this.matchString = this.aspectMatcher.getMatchString();
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(Class<?> annotationType) {
        this.annotationType = annotationType;
    }

    public AspectMatcher getAspectMatcher() {
        return aspectMatcher;
    }

    public void setAspectMatcher(AspectMatcher aspectMatcher) {
        this.aspectMatcher = aspectMatcher;
    }
}
