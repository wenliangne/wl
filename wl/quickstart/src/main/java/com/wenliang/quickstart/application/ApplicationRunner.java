package com.wenliang.quickstart.application;


import java.util.Properties;

import com.wenliang.quickstart.tomcat.QuickStartConfig;
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
        String listenerClassName = QuickStartConfig.getProperties().getProperty("tomcat.listenerClassName");
        if ("".equals(listenerClassName)) {
            QuickStartConfig.getProperties().put("tomcat.listenerClassName", "com.wenliang.context.listener.ContextListener");
        } else {
            QuickStartConfig.getProperties().put("tomcat.listenerClassName", listenerClassName+",com.wenliang.context.listener.ContextListener");
        }
        TomcatRunner.run();
    }

}
