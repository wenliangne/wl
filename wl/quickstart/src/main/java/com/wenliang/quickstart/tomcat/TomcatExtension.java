package com.wenliang.quickstart.tomcat;

import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;

/**
 * @author wenliang
 * @date 2019-12-17
 * 简介：
 */
public interface TomcatExtension {
    public void extension(Tomcat tomcat, StandardContext context);
}
