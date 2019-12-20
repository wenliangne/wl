package com.wenliang.quickstart.tomcat;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import com.wenliang.core.io.Resources;
import com.wenliang.core.log.Log;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;

/**
 * @author wenliang
 * @date 2019-12-17
 * 简介：
 */
public class TomcatRunner {
    private static Tomcat tomcat;
    private static StandardContext context;
    private static Properties properties=new Properties();


    static {
        try {
            properties.load(Resources.getResourceAsStream("default.properties"));
        } catch (IOException e) {
            Log.ERROR("加载文件:default.properties失败！", e);
        }
        try {
            properties.load(Resources.getResourceAsStream("application.properties"));
        } catch (IOException e) {
            Log.ERROR("加载文件:application.properties失败！", e);
        }
    }

    /**
     * 带上参数启动tomcat
     * @param host 主机名
     * @param port 端口号
     * @param contextPath 虚拟目录
     * @param docBase 静态资源路径
     */
    public static void run(String host,int port,String contextPath,String docBase) {
        if (host != null && !"".equals(host)) {
            getProperties().put("tomcat.host",host);
        }
        if (port != 0) {
            getProperties().put("tomcat.port", port);
        }
        if (contextPath != null && !"".equals(contextPath)&& !"/".equals(contextPath)) {
            getProperties().put("contextPath.contextPath",contextPath);
        }
        if (docBase != null && !"".equals(docBase)) {
            getProperties().put("tomcat.docBase",docBase);
        }
        run();
    }

    /**
     * 启动tomcat
     */
    public static void run() {
        tomcat = new Tomcat();
        // 设置主机名称
        tomcat.setHostname(getProperty("tomcat.host"));
        // 设置端口
        tomcat.setPort(Integer.parseInt(getProperty("tomcat.port")));
        tomcat.setBaseDir(System.getProperty("user.dir"));

        context = new StandardContext();
        // 设置资源路径
        context.setDocBase((System.getProperty("user.dir")+"\\src\\main\\resources\\"+getProperty("tomcat.docBase")).replace("\\","/"));
        // 设置应用路径
        context.setPath(getProperty("tomcat.contextPath"));
        context.addLifecycleListener(new Tomcat.FixContextListener());
        // 设置是否允许表单上传enctype="multipart/form-data"类型的数据
        context.setAllowCasualMultipartParsing(Boolean.parseBoolean(getProperty("tomcat.allowCasualMultipartParsing")));
        // 设置缓存大小
        context.setCacheObjectMaxSize(Integer.parseInt(getProperty("tomcat.cacheObjectMaxSize")));
        // 将context加入tomcat
        tomcat.getHost().addChild(context);
        // 添加监听器
        addContextListener(context);
        // 执行扩展任务，需要实现TomcatExtension接口，并配置extensionClassName参数
        extension(tomcat,context);
        // 添加处理jsp的servlet
        addJspServlet(context);
        // 添加自定义的servlet
        addCustomServlet(context);
        // 添加处理所有请求的servlet
        addDefaultServlet(context);

        try {
            // 启动tomcat
            tomcat.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
        // 等待请求
        tomcat.getServer().await();
    }

    /**
     * 添加自定义servlet
     * @param context
     */
    private static void addCustomServlet(Context context) {
        List<ServletConfig> servletConfigList = new ServletScanner(getProperty("tomcat.servletScannerPath")).scan();
        for (int i = 0; i < servletConfigList.size(); i++) {
            ServletConfig servletConfig = servletConfigList.get(i);
            Wrapper servlet = context.createWrapper();
            servlet.setName(servletConfig.getServletName());
            servlet.setServletClass(servletConfig.getClassName());
            servlet.addInitParameter("debug", "0");
            servlet.addInitParameter("listings", "false");
            servlet.setLoadOnStartup(5);
            servlet.setOverridable(true);
            context.addChild(servlet);
            context.addServletMapping(servletConfig.getPattern(), servletConfig.getServletName());
        }
    }

    /**
     * 添加jspServlet
     * @param context
     */
    private static void addJspServlet(Context context) {
        Wrapper jspServlet = context.createWrapper();
        jspServlet.setName("jsp");
        jspServlet.setServletClass("org.apache.jasper.servlet.JspServlet");
        jspServlet.addInitParameter("fork", "false");
        jspServlet.setLoadOnStartup(3);
        context.addChild(jspServlet);
        context.addServletMapping("*.jsp", "jsp");
        context.addServletMapping("*.jspx", "jsp");
    }

    /**
     * 添加默认servlet
     * @param context
     */
    private static void addDefaultServlet(Context context) {
        Wrapper defaultServlet = context.createWrapper();
        defaultServlet.setName("default");
        defaultServlet.setServletClass(getProperty("tomcat.defaultServletClassName"));
        defaultServlet.addInitParameter("debug", "0");
        defaultServlet.addInitParameter("listings", "false");
        defaultServlet.setLoadOnStartup(1);
        defaultServlet.setOverridable(true);
        context.addChild(defaultServlet);
        context.addServletMapping("/", "default");
    }

    /**
     * 加入应用监听器用于启动项目
     * @param context
     */
    private static void addContextListener(StandardContext context) {
        Class<?> applicationListener;
        String allContextListenerClassName = getProperty("tomcat.allContextListenerClassName");
        if ("".equals(allContextListenerClassName)) {
            return;
        }
        try {
            applicationListener = Class.forName(allContextListenerClassName);
            context.addApplicationLifecycleListener(applicationListener.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getCause());
        }
    }

    /**
     * 提供给用户并支持接口TomcatExtension用于一些扩展设置
     * @param tomcat
     * @param context
     */
    private static void extension(Tomcat tomcat,StandardContext context) {
        String extensionClassName = getProperty("tomcat.extensionClassName");
        if ("".equals(extensionClassName)) {
            return;
        } else {
            Class<?> extensionClass;
            try {
                extensionClass = Class.forName(extensionClassName);
                TomcatExtension o = (TomcatExtension)extensionClass.newInstance();
                o.extension(tomcat,context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取context容器进行一些特殊的扩展设置
     * @return StandardContext
     */
    public static StandardContext getContext() {
        return context;
    }
    /**
     * 获取tomcat容器进行一些特殊的扩展设置
     * @return Tomcat
     */
    public static Tomcat getTomcat() {
        return tomcat;
    }

    public static Properties getProperties() {
        return properties;
    }

    public static void setProperties(Properties properties) {
        TomcatRunner.properties = properties;

    }

    private static String getProperty(String key) {
        return getProperties().getProperty(key);
    }


}