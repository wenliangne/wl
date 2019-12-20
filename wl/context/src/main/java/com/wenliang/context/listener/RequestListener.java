package com.wenliang.context.listener;

import com.wenliang.context.annotations.Autowired;
import com.wenliang.context.annotations.Resource;
import com.wenliang.context.cfg.DefaultBeanApplicationContext;
import com.wenliang.controller.cfg.defaults.DefaultControllerApplicationContext;
import com.wenliang.controller.group.ExecutorBean;
import com.wenliang.core.log.Log;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wenliang
 * @date 2019-08-01
 * 简介：使用此监听器需要在web.xml中进行监听器的配置，或者创建一个类继承此类，并在类上添加tomcat提供的@Listener注解
 */
public class RequestListener implements ServletRequestListener {
    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
        DefaultBeanApplicationContext.getThreadLocal().remove();
    }
    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        //为对象注入request对象
        HttpServletRequest request = (HttpServletRequest) servletRequestEvent.getServletRequest();
        String servletPath = request.getServletPath();
        ExecutorBean executorBean = (ExecutorBean) DefaultControllerApplicationContext.get(servletPath);
        if (executorBean == null) {
            return;
        }
        Object object = executorBean.getObject();
        Class<?> aClass;
        if (object.getClass().toString().contains("ByCGLIB")) {
            aClass = object.getClass().getSuperclass();
        } else {
            aClass = object.getClass();
        }
        Field[] declaredFields = aClass.getDeclaredFields();
        if (declaredFields == null) {
            return;
        } else {
            for (int i = 0; i < declaredFields.length; i++) {
                if (HttpServletRequest.class.isAssignableFrom(declaredFields[i].getType())
                        &&(declaredFields[i].getAnnotation(Autowired.class)!=null||declaredFields[i].getAnnotation(Resource.class)!=null)) {
                    try {
                        declaredFields[i].setAccessible(true);
                        declaredFields[i].set(object,request);
                    } catch (IllegalAccessException e) {
                        Log.ERROR("注入失败！");
                    }
                }
            }
        }

        //为aop的参数添加request对象
        ThreadLocal<Map<String, Object>> threadLocal = DefaultBeanApplicationContext.getThreadLocal();
        Map<String, Object> map = threadLocal.get();
        if (map == null) {
            map = new HashMap<>();
            threadLocal.set(map);
        }
        map.put("request", request);
    }
}
