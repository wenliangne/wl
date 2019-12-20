package com.wenliang.context.cfg;

import com.wenliang.context.proxy.ExecutorCommonAspect;
import com.wenliang.core.container.BeanApplicationContext;

import java.util.*;

/**
 * @author wenliang
 * @date 2019-07-09
 * 简介：
 */
public class DefaultBeanApplicationContext implements BeanApplicationContext {

    private static Properties properties = new Properties();
    private static ContextConfiguration contextConfiguration=new ContextConfiguration();

    private static Map<String, Object> context = new HashMap<>();

    private static List<ContextListenerBean> contextListenerBean = new ArrayList<>();

    private static List<ExecutorCommonAspect> executorCommonAspectList = new ArrayList<>();

    private static ThreadLocal<Map<String, Object>> threadLocal=new ThreadLocal<>();

    public static Map getContext() {
        return context;
    }

    public static Object get(String key) {
        return context.get(key);
    }

    public static Object put(String key, Object object) {
        return context.put(key, object);
    }

    public static void putAll(Map<String, Object> map) {
        context.putAll(map);
    }

    public static Set<String> keySet() {
        return context.keySet();
    }

    public static Set<Map.Entry<String, Object>> entrySet() {
        return context.entrySet();
    }

    public static ContextConfiguration getContextConfiguration() {
        return contextConfiguration;
    }

    public static void setContextConfiguration(ContextConfiguration contextConfiguration) {
        DefaultBeanApplicationContext.contextConfiguration = contextConfiguration;
    }

    public static Properties getProperties() {
        return properties;
    }

    public static void setProperties(Properties properties) {
        DefaultBeanApplicationContext.properties = properties;
    }

    public static List<ContextListenerBean> getContextListenerBean() {
        return contextListenerBean;
    }

    public static void setContextListenerBean(List<ContextListenerBean> contextListenerBean) {
        DefaultBeanApplicationContext.contextListenerBean = contextListenerBean;
    }

    public static List<ExecutorCommonAspect> getExecutorCommonAspectList() {
        return executorCommonAspectList;
    }

    public static void setExecutorCommonAspectList(List<ExecutorCommonAspect> executorCommonAspectList) {
        DefaultBeanApplicationContext.executorCommonAspectList = executorCommonAspectList;
    }

    public static ThreadLocal<Map<String, Object>> getThreadLocal() {
        return threadLocal;
    }

    public static void setThreadLocal(ThreadLocal<Map<String, Object>> threadLocal) {
        DefaultBeanApplicationContext.threadLocal = threadLocal;
    }

    private DefaultBeanApplicationContext() {

    }
}
