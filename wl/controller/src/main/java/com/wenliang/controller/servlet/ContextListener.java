package com.wenliang.controller.servlet;

import com.wenliang.controller.cfg.defaults.DefaultURLBuilder;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * @author wenliang
 * @date 2019-06-15
 * 简介：
 */
public class ContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        DefaultURLBuilder builder = new DefaultURLBuilder();
        builder.load();
        builder.builder();
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
