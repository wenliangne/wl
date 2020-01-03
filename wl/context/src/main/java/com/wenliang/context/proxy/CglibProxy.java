package com.wenliang.context.proxy;

import com.wenliang.context.cfg.DefaultBeanApplicationContext;
import com.wenliang.core.log.Log;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wenliang
 * @date 2019-07-31
 * 简介：
 */
public class CglibProxy implements MethodInterceptor {

    private Map<String, Map<String,List<ExecutorAspect>>> map = new HashMap<>();

    public Object  getProxy(Class<?> aClass, Map<String,List<ExecutorAspect>> map) {
        this.map.put(aClass.getName(), map);
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(aClass);
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        Map<String, List<ExecutorAspect>> ExecutorAspectMap = this.map.get(o.getClass().getSuperclass().getName());
        Object result = null;
        try {
            List<ExecutorAspect> aspectBefore = ExecutorAspectMap.get("AspectBefore");
            if (aspectBefore != null) {
                for (ExecutorAspect executorAspect : aspectBefore) {
                    if (executorAspect.getMatcherMethod().contains(method.toString())) {
                        executeAspect(executorAspect, null,method);
                    }
                }
            }

            result = methodProxy.invokeSuper(o, objects);

            List<ExecutorAspect> aspectAfter = ExecutorAspectMap.get("AspectAfter");
            if (aspectAfter != null) {
                for (ExecutorAspect executorAspect : aspectAfter) {
                    if (executorAspect.getMatcherMethod().contains(method.toString())) {
                        executeAspect(executorAspect, null,method);
                    }
                }
            }
        } catch (Exception e) {
            List<ExecutorAspect> aspectThrowing = ExecutorAspectMap.get("AspectThrowing");
            if (aspectThrowing != null) {
                for (ExecutorAspect executorAspect : aspectThrowing) {
                    if (executorAspect.getMatcherMethod().contains(method.toString())) {
                        executeAspect(executorAspect, e,method);
                    }
                }
            }

        }finally {
            List<ExecutorAspect> aspectFinally = ExecutorAspectMap.get("AspectFinally");
            if (aspectFinally != null) {
                for (ExecutorAspect executorAspect : aspectFinally) {
                    if (executorAspect.getMatcherMethod().contains(method.toString())) {
                        executeAspect(executorAspect, null,method);
                    }
                }
            }
        }
        return result;
    }
    private void executeAspect(ExecutorAspect executorAspect,Exception e,Method method) throws InvocationTargetException, IllegalAccessException {
        Class<?>[] parameterTypes = executorAspect.getMethod().getParameterTypes();
        Object[] args=new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            if (Exception.class.isAssignableFrom(parameterTypes[i])) {
                args[i] = e;
            } else if (HttpServletRequest.class.isAssignableFrom(parameterTypes[i])) {
                Map<String, Object> map = DefaultBeanApplicationContext.getThreadLocal().get();
                if (map == null || map.get("request") == null) {
                    Log.WARN("无法在切面方法中注入Request对象，可能执行的方法不是一个Request请求所调用!  方法名："+method.toString()+"；也可能是未配置Request监听器！  配置类名：tomcat.listenerClassName=com.wenliang.context.listener.RequestListener!");
                    args[i] = null;
                } else {
                    args[i]=map.get("request");
                }
            } else {
                args[i] = null;
            }
        }
        executorAspect.getMethod().invoke(executorAspect.getBean(), args);
    }

}
