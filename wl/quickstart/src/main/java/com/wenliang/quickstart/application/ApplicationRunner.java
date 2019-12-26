package com.wenliang.quickstart.application;


import java.util.Properties;

import com.wenliang.quickstart.tomcat.TomcatRunner;

/**
 * @author wenliang
 * @date 2019-12-17
 * 简介：
 */
public class ApplicationRunner {
    /**
     * 启动tomcat
     */
    public static void runTomcat() {
        TomcatRunner.run();
    }

    /**
     * 启动整个应用
     */
    public static void runTomcatAndApplication() {
        String listenerClassName = TomcatRunner.getProperties().getProperty("tomcat.listenerClassName");
        if ("".equals(listenerClassName)) {
            TomcatRunner.getProperties().put("tomcat.listenerClassName", "com.wenliang.context.listener.ContextListener");
        } else {
            TomcatRunner.getProperties().put("tomcat.listenerClassName", listenerClassName+",com.wenliang.context.listener.ContextListener");
        }
        TomcatRunner.run();
    }

}
