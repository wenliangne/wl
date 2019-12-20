package com.wenliang.quickstart.tomcat;

/**
 * @author wenliang
 * @date 2019-12-17
 * 简介：
 */
public class ServletConfig {
    private String pattern;
    private String servletName;
    private String className;

    public ServletConfig(String pattern, String servletName, String className) {
        this.pattern = pattern;
        this.servletName = servletName;
        this.className = className;
    }

    public String getPattern() {
        return pattern;
    }

    public String getServletName() {
        return servletName;
    }

    public String getClassName() {
        return className;
    }
}
