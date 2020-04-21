package com.wenliang.context.cfg;

import com.wenliang.context.annotations.*;
import com.wenliang.controller.cfg.defaults.DefaultControllerApplicationContext;
import com.wenliang.core.log.Log;
import com.wenliang.core.util.ClassUtils;
import com.wenliang.core.util.MethodUtils;
import com.wenliang.mapper.cfg.DefaultRepositoryApplicationContext;
import org.dom4j.Element;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author wenliang
 * @date 2019-08-02
 * 简介：
 */
public class BaseConfigurationScanner {
    public void scan(Element root) {
        loadBaseToXml(root);

    }

    public void scan() {
        loadBaseToAnnotation();
    }

    private void loadBaseToAnnotation() {
        ContextConfiguration configuration = DefaultBeanApplicationContext.getContextConfiguration();
        Set<Class<?>> configurations = null;
        try {
            configurations = ClassUtils.getClassWithAnnotation("", Configuration.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (configuration == null) {
            return;
        }
        for (Class<?> aClass : configurations) {
            try {
                ComponentScan componentScan = aClass.getAnnotation(ComponentScan.class);
                if (componentScan != null) {
                    configuration.setComponentScan(componentScan.value());
                }
            } catch (Exception e) {
                Log.ERROR("注解配置错误："+aClass.getName()+ComponentScan.class.getSimpleName(),e);
            }
            try {
                ControllerScan controllerScan = aClass.getAnnotation(ControllerScan.class);
                if (controllerScan != null) {
                    configuration.setControllerScan(controllerScan.value());
                    DefaultControllerApplicationContext.getProperties().setProperty("controller-scan", Arrays.toString(configuration.getControllerScan()));
                }
            } catch (Exception e) {
                Log.ERROR("注解配置错误："+aClass.getName()+ControllerScan.class.getSimpleName(),e);
            }
            try {
                MapperScan mapperScan = aClass.getAnnotation(MapperScan.class);
                if (mapperScan != null) {
                    configuration.setMapperScan(mapperScan.value());
                    DefaultRepositoryApplicationContext.getMapperConfiguration().setMapperScan(configuration.getMapperScan());
                }
            } catch (Exception e) {
                Log.ERROR("注解配置错误："+aClass.getName()+MapperScan.class.getSimpleName(),e);
            }
            try {
                ImportProperties importProperties = aClass.getAnnotation(ImportProperties.class);
                if (importProperties != null) {
                    configuration.setImportFileNames(Arrays.asList(importProperties.value()));
                }
            } catch (Exception e) {
                Log.ERROR("注解配置错误："+aClass.getName()+ImportProperties.class.getSimpleName(),e);

            }
            try {
                ConfigurationClassBean configurationClassBean = new ConfigurationClassBean();
                ArrayList<BeanToMethod> methodList = new ArrayList<>();
                configurationClassBean.setConfigureBean(aClass.newInstance());
                Method[] methods = aClass.getDeclaredMethods();
                for (Method method : methods) {
                    com.wenliang.context.annotations.Bean annotation = method.getAnnotation(com.wenliang.context.annotations.Bean.class);
                    if (annotation != null) {
                        BeanToMethod beanToMethod = new BeanToMethod();
                        beanToMethod.setMethod(method);
                        beanToMethod.setParameterName(MethodUtils.getParameterNames(method));
                        beanToMethod.setParameterType(method.getParameterTypes());
                        beanToMethod.setResultType(method.getReturnType());
                        methodList.add(beanToMethod);
                    }
                }
                configurationClassBean.setBeanToMethods(methodList);
                configuration.getConfigurationClassBeans().add(configurationClassBean);
            } catch (Exception e) {
                Log.ERROR("初始化配置类："+aClass.getName()+"失败！",e);
            }
        }
    }

    private void loadBaseToXml(Element root) {
        ContextConfiguration configuration = DefaultBeanApplicationContext.getContextConfiguration();
        // 获取componentScan标签的属性取值
        List<Element> componentScanList = root.selectNodes("//component-scan");
        try {
            String[] componentScans = transElementToArr(componentScanList);
            if (componentScans.length > 0) {
                configuration.setComponentScan(componentScans);
            }
        } catch (Exception e) {
            Log.ERROR("解析component-scan错误",e);
        }

        // 获取controllerScan标签的属性取值
        List<Element> controllerScanList = root.selectNodes("//controller-scan");
        try {
            String[] controllerScans = transElementToArr(controllerScanList);
            if (controllerScans.length > 0) {
                configuration.setControllerScan(controllerScans);
                DefaultControllerApplicationContext.getProperties().setProperty("controller-scan", Arrays.toString(configuration.getControllerScan()));
            }
        } catch (Exception e) {
            Log.ERROR("解析controller-scan错误",e);
        }

        // 获取mapperScan标签的属性取值
        List<Element> mapperScanList = root.selectNodes("//mapper-scan");
        try {
            String[] mapperScans = transElementToArr(mapperScanList);
            if (mapperScans.length > 0) {
                configuration.setMapperScan(mapperScans);
                DefaultRepositoryApplicationContext.getMapperConfiguration().setMapperScan(configuration.getMapperScan());
            }
        } catch (Exception e) {
            Log.ERROR("解析mapper-scan错误",e);
        }

        // 获取importProperties标签的属性取值
        List<Element> importProperties = root.selectNodes("//import");
        ArrayList<String> importFileNames = new ArrayList<>();
        for (Element importProperty : importProperties) {
            String importFileName = importProperty.attributeValue("value");
            if (importFileName == null||"".equals(importFileName)) {
                importFileName=importProperty.getText();
            }
            importFileNames.add(importFileName);
        }
        configuration.setImportFileNames(importFileNames);
    }

    /**
     * 将多个标签的值组合为一个数组
     * @param mapperScanList
     * @return
     */
    private String[] transElementToArr(List<Element> mapperScanList) {
        String[] mapperScans = new String[mapperScanList.size()];
        for (int i = 0; i < mapperScanList.size(); i++) {
            Element element = mapperScanList.get(i);
            String mapperScan = element.attributeValue("value");
            if (mapperScan == null) {
                mapperScan=element.getText();
            }
            mapperScans[i] = mapperScan.trim();
        }
        return mapperScans;
    }


}
