package com.wenliang.context.container;

import com.wenliang.context.cfg.*;
import com.wenliang.context.container.interfaces.ApplicationContextBuilder;
import com.wenliang.context.container.interfaces.ApplicationContextFactory;
import com.wenliang.context.listener.*;
import com.wenliang.context.listener.interfaces.AfterScanConfigurationListener;
import com.wenliang.context.listener.interfaces.BeforeScanConfigurationListener;
import com.wenliang.context.plugins.transaction.TransactionManagerScanner;
import com.wenliang.context.proxy.AspectScanner;
import com.wenliang.core.io.Resources;
import com.wenliang.core.log.Log;
import com.wenliang.mapper.cfg.DefaultRepositoryApplicationContext;
import com.wenliang.mapper.cfg.MapperConfiguration;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author wenliang
 * @date 2019-07-10
 * 简介：
 */
public class DefaultApplicationContextBuilder implements ApplicationContextBuilder {
    @Override
    public ContextConfiguration load(InputStream inputStream) {
        Document document=null;
        try {
            SAXReader reader = new SAXReader();
            document = reader.read(inputStream);
        } catch (Exception e) {
            Log.ERROR("加载配置文件失败！", e);
        }

        ContextConfiguration configuration = DefaultBeanApplicationContext.getContextConfiguration();
        // 获取根节点
        Element root = document.getRootElement();

        //扫描基础配置
        BaseConfigurationScanner baseConfigurationScanner = new BaseConfigurationScanner();
        baseConfigurationScanner.scan();
        baseConfigurationScanner.scan(root);

        //加载属性文件并设置数据库连接信息
        loadPropertiesAndSetDataInfo();

        //扫描切面配置
        AspectScanner aspectScanner = new AspectScanner();
        aspectScanner.scan(root);
        aspectScanner.scan(configuration.getComponentScan());

        //扫描事务管理
        TransactionManagerScanner transactionManagerScanner = new TransactionManagerScanner();
        transactionManagerScanner.scan(root);
        transactionManagerScanner.scan(configuration.getComponentScan());

        //扫描监听器
        ListenerScanner listenerScanner = new ListenerScanner();
        listenerScanner.scan(root);
        listenerScanner.scan(configuration.getComponentScan());

        //执行监听器BeforeScanConfigurationListener
        ListenerExecutor listenerExecutor = new ListenerExecutor();
        listenerExecutor.excutr(BeforeScanConfigurationListener.class);

        //扫描bean
        BeanScanner beanScanner = new BeanScanner();
        beanScanner.scan(root);
        beanScanner.scan(configuration.getComponentScan());

        //执行监听器AfterScanConfigurationListener
        listenerExecutor.excutr(AfterScanConfigurationListener.class);

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return configuration;
    }

    @Override
    public ApplicationContextFactory builder() {
        return new DefaultApplicationContextFactory();
    }

    /**
     * 加载Properties文件并将数据库连接信息注入
     */
    private void loadPropertiesAndSetDataInfo() {
        List<String> importFileNames = DefaultBeanApplicationContext.getContextConfiguration().getImportFileNames();
        for (String importFileName : importFileNames) {
            InputStream in = Resources.getResourceAsStream(importFileName);
            try {
                DefaultBeanApplicationContext.getProperties().load(in);
            } catch (IOException e) {
                Log.ERROR("加载配置文件："+importFileName+"失败！",e);
            }
        }
        MapperConfiguration mapperConfiguration = DefaultRepositoryApplicationContext.getMapperConfiguration();
        Properties properties = DefaultBeanApplicationContext.getProperties();
        String driver = properties.getProperty("datasource.driver");
        if (driver != null) {
            mapperConfiguration.setDriver(driver);
        }
        String url = properties.getProperty("datasource.url");
        if (url != null) {
            mapperConfiguration.setUrl(url);
        }
        String username = properties.getProperty("datasource.username");
        if (username!=null) {
            mapperConfiguration.setUsername(username);
        }
        String password = properties.getProperty("datasource.password");
        if (password != null) {
            mapperConfiguration.setPassword(password);
        }
    }





}
