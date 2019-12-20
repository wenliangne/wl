package com.wenliang.quickstart.application;


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
        TomcatRunner.getProperties().put("tomcat.allContextListenerClassName","");
        TomcatRunner.run();
    }

    /**
     * 启动整个应用
     */
    public static void runTomcatAndApplication() {
        TomcatRunner.getProperties().put("tomcat.allContextListenerClassName","com.wenliang.context.listener.ContextListener");
        TomcatRunner.run();
    }

}
