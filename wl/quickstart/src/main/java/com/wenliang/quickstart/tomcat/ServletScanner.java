package com.wenliang.quickstart.tomcat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.wenliang.core.util.ClassUtils;
import com.wenliang.core.util.StringUtils;


/**
 * @author wenliang
 * @date 2019-12-17
 * 简介：
 */
public class ServletScanner {
    private String classPath;
    public List<ServletConfig> scan() {
        ArrayList<ServletConfig> servletConfigArrayList = new ArrayList<ServletConfig>();
        Set<Class<?>> servletSet = ClassUtils.getClassWithAnnotation(classPath, Servlet.class);
        if (servletSet == null) {
            return servletConfigArrayList;
        }
        Iterator<Class<?>> iterator = servletSet.iterator();
        while (iterator.hasNext()) {
            Class<?> servletClass = iterator.next();
            Servlet servlet = servletClass.getAnnotation(Servlet.class);
            String servletURL = servlet.value();
            String servletDefaultName = StringUtils.convertFirstCharToLowerCase(servletClass.getSimpleName());
            if ("".equals(servletURL)) {
                servletConfigArrayList.add(new ServletConfig("/" + servletDefaultName, servletDefaultName, servletClass.getName()));
            } else {
                servletConfigArrayList.add(new ServletConfig(servletURL, servletDefaultName, servletClass.getName()));
            }
        }
        return servletConfigArrayList;
    }



    public ServletScanner(String classPath) {
        this.classPath = classPath;
    }
    public String getClassPath() {

        return classPath;
    }
    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }
}
