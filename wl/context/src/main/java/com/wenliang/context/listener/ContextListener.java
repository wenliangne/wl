package com.wenliang.context.listener;

import com.wenliang.context.cfg.RunContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * @author wenliang
 * @date 2019-06-15
 * 简介：使用此监听器需要在web.xml中进行监听器的配置，或者创建一个类继承此类，并在类上添加tomcat提供的@Listener注解
 */
public class ContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        RunContext.Run();
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
