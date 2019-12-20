package com.wenliang.context.cfg;

import java.util.List;

/**
 * @author wenliang
 * @date 2019-07-19
 * 简介：
 */
public class ConfigurationClassBean extends Bean {

    private Object configureBean;

    private List<BeanToMethod> beanToMethods;

    public List<BeanToMethod> getBeanToMethods() {
        return beanToMethods;
    }

    public void setBeanToMethods(List<BeanToMethod> beanToMethods) {
        this.beanToMethods = beanToMethods;
    }

    public Object getConfigureBean() {
        return configureBean;
    }

    public void setConfigureBean(Object configureBean) {
        this.configureBean = configureBean;
    }
}
