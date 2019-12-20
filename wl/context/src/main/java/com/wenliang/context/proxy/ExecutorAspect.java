package com.wenliang.context.proxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author wenliang
 * @date 2019-07-31
 * 简介：
 */
public class ExecutorAspect {

    private List<String> matcherMethod;
    private Object bean;
    private Method method;

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

    public List<String> getMatcherMethod() {
        return matcherMethod;
    }

    public void setMatcherMethod(List<String> matcherMethod) {
        this.matcherMethod = matcherMethod;
    }
}
