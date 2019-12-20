package com.wenliang.controller.cfg.defaults;


import com.wenliang.core.container.BeanApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author wenliang
 * @date 2019-07-09
 * 简介：
 */
public class DefaultControllerApplicationContext implements BeanApplicationContext{

    private static Map<String, Object> beans = new HashMap<>();
    private static Map<String, Object> context = new HashMap<>();
    public static Properties properties = new Properties();

    public static Properties getProperties() {
        return properties;
    }

    public static void setProperties(Properties properties) {
        DefaultControllerApplicationContext.properties = properties;
    }

    public static Map<String, Object> getBeans() {
        return beans;
    }

    public static void setBeans(Map<String, Object> beans) {
        DefaultControllerApplicationContext.beans = beans;
    }

    public static Map<String, Object> getContext() {
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




    private DefaultControllerApplicationContext() {

    }
}
