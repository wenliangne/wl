package com.wenliang.context.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wenliang
 * @date 2019-07-09
 * 简介：
 */
public class Bean {

    private String beanName;  //对象的名称
    private Class<?> beanClass;  //对象的全限定类名
    private Map<String,Property> properties;  //对象需要注入的属性
    private Class<?> type;  //注解类型

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Map<String, Property> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Property> properties) {
        this.properties = properties;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public void addProperty(Property property) {
        if (properties == null) {
            properties = new HashMap<>();
        }
        properties.put(property.getFieldName(), property);
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
}
