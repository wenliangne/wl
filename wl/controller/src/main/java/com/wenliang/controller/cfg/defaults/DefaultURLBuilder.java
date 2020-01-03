package com.wenliang.controller.cfg.defaults;

import com.wenliang.controller.annotation.Controller;
import com.wenliang.controller.annotation.RequestMapping;
import com.wenliang.controller.cfg.URLBuilder;
import com.wenliang.controller.group.ExecutorBean;
import com.wenliang.core.log.Log;
import com.wenliang.core.util.ClassUtils;
import com.wenliang.core.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author wenliang
 * @date 2019-07-15
 * 简介：
 */
public class DefaultURLBuilder implements URLBuilder {

    @Override
    public void load() {
        load("mvc.properties");
    }

    @Override
    public void builder() {
        String packageNames = DefaultControllerApplicationContext.getProperties().getProperty("controller-scan");
        String[] packageArr = packageNames.replace("[", "").replace("]", "").split(",");
        for (String packageName : packageArr) {
            try {
                Set<Class<?>> controllerList = ClassUtils.getClassWithAnnotation(packageName,Controller.class);
                for (Class<?> aClass : controllerList) {
                    Object o = aClass.newInstance();
                    Method[] methods = aClass.getDeclaredMethods();
                    String rmHead = "";
                    if (aClass.isAnnotationPresent(RequestMapping.class)) {
                        rmHead = aClass.getAnnotationsByType(RequestMapping.class)[0].value();
                    }
                    for (Method method : methods) {
                        RequestMapping rm = method.getAnnotation(RequestMapping.class);
                        if (rm == null||rm.value().equals("")) {
                            continue;
                        }
                        DefaultControllerApplicationContext.
                                put(formatURL(rmHead) + formatURL(rm.value()), new ExecutorBean(o, method));
                    }
                    String beanName = o.getClass().getAnnotation(Controller.class).value();
                    if ("".equals(beanName)) {
                        beanName = StringUtils.convertFirstCharToLowerCase(o.getClass().getSimpleName());
                    }
                    DefaultControllerApplicationContext.
                            getBeans().put(beanName, o);
                }
                Log.INFO("初始化包："+packageName+"下的url容器成功！");
            } catch (Exception e) {
                Log.ERROR("初始化包："+packageName+"下的url容器失败！",e);
            }
        }

    }


    public void load(String filePath) {
        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(filePath);
            DefaultControllerApplicationContext.getProperties().load(in);
        } catch (IOException e) {
            Log.ERROR("加载文件："+filePath+"失败！",e);
        }
    }

    public String formatURL(String url) {
        if ("".equals(url)) {
            return "";
        }
        if (!url.startsWith("/")) {
            url = "/" + url;
        }
        if (url.endsWith("/")) {
            if ("/".equals(url)) {
                return url;
            } else {
                url = url.substring(0, url.length()-1);
            }
        }
        return url;
    }
}