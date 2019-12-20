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
public class ContextConfiguration {
    private Map<String,Bean> beans;  //需要创建的对象的集合
    private String[] componentScan;
    private String[] controllerScan;
    private String[] mapperScan;
    private List<String> importFileNames=new ArrayList<>();
    private List<ConfigurationClassBean> configurationClassBeans;

    public Map<String, Bean> getBeans() {
        return beans;
    }

    public void setBeans(Map<String, Bean> beans) {
        this.beans = beans;
    }

    public ContextConfiguration() {
        importFileNames.add("application.properties");
    }

    public void addBean(Bean bean) {
        if (beans == null) {
            beans = new HashMap<>();
        }
        beans.put(bean.getBeanName(),bean);
    }

    public String[] getComponentScan() {
        return componentScan;
    }

    public void setComponentScan(String[] componentScan) {
        this.componentScan = componentScan;
    }

    public String[] getControllerScan() {
        return controllerScan;
    }

    public void setControllerScan(String[] controllerScan) {
        this.controllerScan = controllerScan;
    }

    public String[] getMapperScan() {
        return mapperScan;
    }

    public void setMapperScan(String[] mapperScan) {
        this.mapperScan = mapperScan;
    }

    public List<String> getImportFileNames() {
        return importFileNames;
    }

    public void setImportFileNames(List<String> importFileNames) {
        this.importFileNames.addAll(importFileNames);
    }

    public List<ConfigurationClassBean> getConfigurationClassBeans() {
        if (this.configurationClassBeans == null) {
            this.configurationClassBeans = new ArrayList<>();
        }
        return configurationClassBeans;
    }

    public void setConfigurationClassBeans(List<ConfigurationClassBean> configurationClassBeans) {
        if (this.configurationClassBeans == null) {
            this.configurationClassBeans = new ArrayList<>();
        }
        this.configurationClassBeans.addAll(configurationClassBeans);
    }
}
