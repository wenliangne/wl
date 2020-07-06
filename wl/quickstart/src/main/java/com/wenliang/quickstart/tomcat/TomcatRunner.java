package com.wenliang.quickstart.tomcat;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestListener;

import com.wenliang.core.io.Resources;
import com.wenliang.core.log.Log;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.apache.catalina.startup.Tomcat;

/**
 * @author wenliang
 * @date 2019-12-17
 * 简介：
 */
public class TomcatRunner {
    private static Tomcat tomcat;
    private static StandardContext context;

    /**
     * 带上参数启动tomcat
     * @param host 主机名
     * @param port 端口号
     * @param contextPath 虚拟目录
     * @param docBase 静态资源路径
     */
    public static void run(String host,int port,String contextPath,String docBase) {
        if (host != null && !"".equals(host)) {
            QuickStartConfig.getProperties().put("tomcat.host",host);
        }
        if (port != 0) {
            QuickStartConfig.getProperties().put("tomcat.port", port);
        }
        if (contextPath != null && !"".equals(contextPath)&& !"/".equals(contextPath)) {
            QuickStartConfig.getProperties().put("tomcat.contextPath",contextPath);
        }
        if (docBase != null && !"".equals(docBase)) {
            QuickStartConfig.getProperties().put("tomcat.docBase",docBase);
        }
        run();
    }

    /**
     * 启动tomcat
     */
    public static void run() {
        //添加tomcat并进行基本设置
        addTomcatAndBasicSetting();
        //添加context容器并进行基本设置
        addContextAndBasicSetting();
        // 执行扩展任务，需要实现TomcatExtension接口，并配置extensionClassName参数
        extension(tomcat,context);
        // 将context加入tomcat
        tomcat.getHost().addChild(context);
        //添加欢迎页面
        addWelcome(context);
        // 添加监听器
        addListener(context);
        //添加过滤器
        addFilter(context);
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

    private static void addContextAndBasicSetting() {
        StandardContext context = new StandardContext();
        // 设置资源路径
//        String docBase = (TomcatRunner.class.getClassLoader().getResource("").getPath() + getProperty("tomcat.docBase")).replace("\\", "/");
        context.setDocBase(getDocBase());
        // 设置应用路径
        context.setPath(QuickStartConfig.getProperty("tomcat.contextPath"));
        //设置热部署
        context.setReloadable(Boolean.parseBoolean(QuickStartConfig.getProperty("tomcat.reloadable")));
        //new Tomcat.DefaultWebXmlListener()
        context.addLifecycleListener(new Tomcat.FixContextListener());
        // 设置是否允许表单上传enctype="multipart/form-data"类型的数据
        context.setAllowCasualMultipartParsing(Boolean.parseBoolean(QuickStartConfig.getProperty("tomcat.allowCasualMultipartParsing")));
        // 设置缓存大小
        context.setCacheObjectMaxSize(Integer.parseInt(QuickStartConfig.getProperty("tomcat.cacheObjectMaxSize")));
        TomcatRunner.context = context;
    }

    private static void addTomcatAndBasicSetting() {
        Tomcat tomcat = new Tomcat();
        // 设置主机名称
        tomcat.setHostname(QuickStartConfig.getProperty("tomcat.host"));
        // 设置端口
        tomcat.setPort(Integer.parseInt(QuickStartConfig.getProperty("tomcat.port")));
        tomcat.setBaseDir(System.getProperty("user.dir"));
        TomcatRunner.tomcat = tomcat;
    }

    /**
     * 获取资源路径
     * @return
     */
    private static String getDocBase() {
        String path = TomcatRunner.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (!path.contains(QuickStartConfig.getProperty("tomcat.currentJarName"))) {
            path = path.substring(0, path.lastIndexOf("/")) + File.separator;
        } else {
            path = TomcatRunner.class.getClassLoader().getResource("").getPath();
        }
        path = path + QuickStartConfig.getProperty("tomcat.docBase");
        path = path.replace("/", File.separator).replace("\\", File.separator);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        return path;
    }

    /**
     * 添加欢迎页面
     * @param context
     */
    private static void addWelcome(Context context) {
        String welcomePages = QuickStartConfig.getProperty("tomcat.welcome");
        String[] welcomePagesArr = welcomePages.split(",");
        for (int i = 0; i < welcomePagesArr.length; i++) {
            context.addWelcomeFile(welcomePagesArr[i]);
        }
    }

    /**
     * 添加自定义servlet
     * @param context
     */
    private static void addCustomServlet(Context context) {
        List<ServletConfig> servletConfigList = null;
        try {
            servletConfigList = new ServletScanner(QuickStartConfig.getProperty("tomcat.servletScannerPath")).scan();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        jspServlet.setServletClass(QuickStartConfig.getProperty("tomcat.defaultJspServletClassName"));
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
        defaultServlet.setServletClass(QuickStartConfig.getProperty("tomcat.defaultServletClassName"));
        defaultServlet.addInitParameter("debug", "0");
        defaultServlet.addInitParameter("listings", "false");
        defaultServlet.setLoadOnStartup(1);
        defaultServlet.setOverridable(true);
        context.addChild(defaultServlet);
        context.addServletMapping("/", "default");
    }

    /**
     * 添加监听器
     * @param context
     */
    private static void addListener(StandardContext context) {
        String listenerClassName = QuickStartConfig.getProperty("tomcat.listenerClassName");
        if ("".equals(listenerClassName)) {
            return;
        } else {
            String[] listenerClassNameArr = listenerClassName.split(",");
            for (int i = 0; i < listenerClassNameArr.length; i++) {
                if ("".equals(listenerClassNameArr[i])) {
                    continue;
                }
                try {
                    Class<?> applicationListener = Class.forName(listenerClassNameArr[i]);
                    Object o = applicationListener.newInstance();
                    if (ServletContextListener.class.isAssignableFrom(applicationListener)) {
                        context.addApplicationLifecycleListener(o);
                    } else {
                        context.addApplicationEventListener(o);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 添加过滤器
     * @param context
     */
    private static void addFilter(StandardContext context) {
        String filterClassName = QuickStartConfig.getProperty("tomcat.filterClassName");
        if ("".equals(filterClassName)) {
            return;
        } else {
            String[] filterClassNameArr = filterClassName.split(",");
            for (int i = 0; i < filterClassNameArr.length; i++) {
                if ("".equals(filterClassNameArr[i])) {
                    continue;
                }
                try {
                    int index = filterClassNameArr[i].indexOf(':');
                    String className = filterClassNameArr[i].substring(0, index);
                    String path = filterClassNameArr[i].substring(index+1);

                    FilterDef filterDef = new FilterDef();
                    filterDef.setFilterName(className);
                    filterDef.setFilterClass(className);
                    context.addFilterDef(filterDef);

                    FilterMap filterMap = new FilterMap();
                    filterMap.setFilterName(className);
                    filterMap.addURLPattern(path);
                    context.addFilterMap(filterMap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 提供给用户并支持接口TomcatExtension用于一些扩展设置
     * @param tomcat
     * @param context
     */
    private static void extension(Tomcat tomcat,StandardContext context) {
        String extensionClassName = QuickStartConfig.getProperty("tomcat.extensionClassName");
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



}
