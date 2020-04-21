package com.wenliang.context.listener;

import com.wenliang.context.annotations.ContextListener;
import com.wenliang.context.cfg.ContextListenerBean;
import com.wenliang.context.cfg.DefaultBeanApplicationContext;
import com.wenliang.context.listener.interfaces.ListenerInterface;
import com.wenliang.core.log.Log;
import com.wenliang.core.util.ClassUtils;

import org.dom4j.Element;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author wenliang
 * @date 2019-08-02
 * 简介：
 */
public class ListenerScanner {
    public void scan(Element root) {
        loadListenerToXml(root);
    }

    public void scan(String[] packageNames) {
        loadListenerToAnnotation(packageNames);
    }

    private void loadListenerToXml(Element root) {
        List<Element> listenerList = root.selectNodes("//listener");
        for (Element element : listenerList) {
            String value = element.attributeValue("value");
            if (value == null || "".equals(value)) {
                value = element.getText();
            }
            try {
                Class<?> aClass = Class.forName(value);
                Class<?>[] interfaces = aClass.getInterfaces();
                for (int i = 0; i < interfaces.length; i++) {
                    if (interfaces[i].isAssignableFrom(ListenerInterface.class)) {
                    } else if (ListenerInterface.class.isAssignableFrom(interfaces[i])){
                        ContextListenerBean contextListenerBean = new ContextListenerBean();
                        try {
                            contextListenerBean.setListener((ListenerInterface) aClass.newInstance());
                            contextListenerBean.setListenerType(interfaces[i]);
                            DefaultBeanApplicationContext.getContextListenerBean().add(contextListenerBean);
                        } catch (Exception e) {
                            Log.ERROR("创建监听器："+aClass.getName()+"失败！",e);
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    private void loadListenerToAnnotation(String[] packageNames) {
        Set<Class<?>> ls = null;
        try {
            ls = ClassUtils.getClassWithAnnotation(packageNames,ContextListener.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (ls == null) {
            return;
        }
        for (Class<?> l : ls) {
            try {
                ContextListenerBean contextListenerBean = new ContextListenerBean();
                contextListenerBean.setListener((ListenerInterface) l.newInstance());
                contextListenerBean.setListenerType(l);
                DefaultBeanApplicationContext.getContextListenerBean().add(contextListenerBean);
            } catch (Exception e) {
                Log.ERROR("创建监听器："+l.getName()+"失败！",e);
            }
        }
    }
}
