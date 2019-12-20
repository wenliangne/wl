package com.wenliang.context.cfg;


import com.wenliang.core.log.Log;
import com.wenliang.core.util.StringUtils;

import java.util.Properties;

/**
 * @author wenliang
 * @date 2019-07-09
 * 简介：
 */
public class Property {

    private String fieldName;  //字段名称
    private String refBeanName;  //引用的对象名称
    private Class<?> refBeanClass;  //引用的对象class
    private String value;  //普通类型的值
    private Class<?> type;  //注解类型

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getRefBeanName() {
        return refBeanName;
    }

    public void setRefBeanName(String refBeanName) {
        this.refBeanName = refBeanName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        Properties properties = DefaultBeanApplicationContext.getProperties();
        this.value = StringUtils.convertValueToSymbol(value,"#{","}",properties);
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Class<?> getRefBeanClass() {
        return refBeanClass;
    }

    public void setRefBeanClass(Class<?> refBeanClass) {
        this.refBeanClass = refBeanClass;
    }
}
