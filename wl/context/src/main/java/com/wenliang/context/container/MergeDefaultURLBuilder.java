package com.wenliang.context.container;

import com.wenliang.context.cfg.DefaultBeanApplicationContext;
import com.wenliang.context.proxy.ExecutorCommonAspect;
import com.wenliang.context.proxy.ProxyBeanFactory;
import com.wenliang.controller.annotation.Controller;
import com.wenliang.controller.annotation.RequestMapping;
import com.wenliang.controller.cfg.defaults.DefaultControllerApplicationContext;
import com.wenliang.controller.cfg.defaults.DefaultURLBuilder;
import com.wenliang.controller.group.ExecutorBean;
import com.wenliang.core.io.Resources;
import com.wenliang.core.log.Log;
import com.wenliang.core.util.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.reflections.Reflections;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * @author wenliang
 * @date 2019-07-15
 * 简介：
 */
public class MergeDefaultURLBuilder extends DefaultURLBuilder {

    @Override
    public void load() {
        load("application.xml");
    }

    @Override
    public void builder() {
        String packageName = DefaultControllerApplicationContext.getProperties().getProperty("controller-scan");
        try {
            List<ExecutorCommonAspect> executorCommonAspectList = DefaultBeanApplicationContext.getExecutorCommonAspectList();
            ProxyBeanFactory proxyBeanFactory = new ProxyBeanFactory();
            Reflections r = new Reflections(packageName);
            Set<Class<?>> controllerList = r.getTypesAnnotatedWith(Controller.class);
            for (Class<?> aClass : controllerList) {
                Object o = proxyBeanFactory.createProxyBean(aClass, executorCommonAspectList);
                if (o == null) {
                    o = aClass.newInstance();
                }
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
                String beanName = o.getClass().getSuperclass().getAnnotation(Controller.class).value();
                if ("".equals(beanName)) {
                    beanName = StringUtils.convertFirstCharToLowerCase(o.getClass().getSuperclass().getSimpleName());
                }
                DefaultControllerApplicationContext.
                        getBeans().put(beanName, o);
            }
            Log.INFO("初始化url容器成功！");
        } catch (Exception e) {
            Log.ERROR("初始化url容器失败！",e);
        }
    }


    public void load(String filePath) {
        Document document=null;
        try {
            SAXReader reader = new SAXReader();
            document = reader.read(Resources.getResourceAsStream(filePath));
        } catch (Exception e) {
            Log.ERROR("加载配置文件失败！", e);
            throw new RuntimeException(e);
        }
        List<Element> list = document.selectNodes("//controller-scan");
        if (list == null || list.size() == 0) {
            DefaultControllerApplicationContext.getProperties().setProperty("controller-scan", "com");
        } else {
            String packageName = list.get(0).attributeValue("value") != null ? list.get(0).attributeValue("value") : list.get(0).getText();
            DefaultControllerApplicationContext.getProperties().setProperty("controller-scan",packageName.trim());
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