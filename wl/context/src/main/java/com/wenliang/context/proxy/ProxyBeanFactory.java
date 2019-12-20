package com.wenliang.context.proxy;

import com.wenliang.core.log.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wenliang
 * @date 2019-08-01
 * 简介：
 */
public class ProxyBeanFactory {
    private CglibProxy cglibProxy=new CglibProxy();
    public Object createProxyBean(Class<?> aClass, List<ExecutorCommonAspect> list) {
        try {
            if (list == null) {
                return null;
            } else {
                AspectMatcher aspectMatcher = null;
                Map<String, List<ExecutorAspect>> map = new HashMap<>();
                for (ExecutorCommonAspect executorCommonAspect : list) {
                    aspectMatcher = executorCommonAspect.getAspectMatcher();
                    if (aspectMatcher.isMatchClass(aClass)) {
                        if (aspectMatcher.existMethodForClass(aClass)) {
                            ExecutorAspect executorAspect = new ExecutorAspect();
                            executorAspect.setBean(executorCommonAspect.getBean());
                            executorAspect.setMethod(executorCommonAspect.getMethod());
                            ArrayList<String> methodNames = new ArrayList<>();
                            for (Method method : aClass.getDeclaredMethods()) {
                                if (aspectMatcher.isMatchMethod(method)) {
                                    methodNames.add(method.toString());
                                }
                            }
                            executorAspect.setMatcherMethod(methodNames);
                            List<ExecutorAspect> executorAspectList = map.get(executorCommonAspect.getAnnotationType().getSimpleName());
                            if (executorAspectList == null) {
                                executorAspectList = new ArrayList<>();
                                map.put(executorCommonAspect.getAnnotationType().getSimpleName(),executorAspectList);
                            }
                            executorAspectList.add(executorAspect);
                        }
                    }
                }
                if (map.size() > 0) {
                    return cglibProxy.getProxy(aClass, map);
                } else {
                    return null;
                }
            }

        } catch (Exception e) {
            Log.ERROR("创建代理对象失败："+aClass.getName(),e);
            return null;
        }
    }
}
