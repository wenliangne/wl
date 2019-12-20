package com.wenliang.context.cfg;

import java.lang.reflect.Method;

/**
 * @author wenliang
 * @date 2019-07-19
 * 简介：
 */
public class BeanToMethod {
    private String[] parameterName;
    private Class<?>[] parameterType;
    private Class<?> resultType;
    private Method method;


    public String[] getParameterName() {
        return parameterName;
    }

    public void setParameterName(String[] parameterName) {
        this.parameterName = parameterName;
    }

    public Class<?>[] getParameterType() {
        return parameterType;
    }

    public void setParameterType(Class<?>[] parameterType) {
        this.parameterType = parameterType;
    }

    public Class<?> getResultType() {
        return resultType;
    }

    public void setResultType(Class<?> resultType) {
        this.resultType = resultType;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
