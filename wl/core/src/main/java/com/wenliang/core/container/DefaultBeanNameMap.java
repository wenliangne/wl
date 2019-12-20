package com.wenliang.core.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wenliang
 * @date 2019-07-12
 * 简介：
 */
public class DefaultBeanNameMap {
    private DefaultBeanNameMap() {
    }
    private static Map<String,List<String>> beanNameMap = new HashMap();

    public static Map<String, List<String>> getBeanNameMap() {
        return beanNameMap;
    }

    public static void setBeanNameMap(Map<String, List<String>> beanNameMap) {
        beanNameMap = beanNameMap;
    }

    public static List<String> get(String className) {
        return beanNameMap.get(className);
    }
    public static void put(String className,String beanName) {
        List<String> list = beanNameMap.get(className);
        if (list == null) {
            list = new ArrayList();
            list.add(beanName);
            beanNameMap.put(className, list);
            return;
        }
        if (!list.contains(beanName)) {
            list.add(beanName);
        }
    }



}
