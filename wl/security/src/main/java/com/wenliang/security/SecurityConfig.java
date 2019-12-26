package com.wenliang.security;

import java.io.InputStream;
import java.util.Properties;

import com.wenliang.core.io.Resources;
import com.wenliang.core.log.Log;

/**
 * @author wenliang
 * @date 2019-12-24
 * 简介：
 */
public class SecurityConfig {
    private Properties properties=new Properties();
    public SecurityConfig() {
        loadProperties();
    }
    private void loadProperties() {
        try {
            InputStream in = Resources.getResourceAsStream("securityDefault.properties");
            properties.load(in);
            Log.INFO("加载文件securityDefault.properties完成！");
        } catch (Exception e) {
            Log.ERROR("加载文件securityDefault.properties失败！");
        }
        try {
            InputStream in = Resources.getResourceAsStream("application.properties");
            properties.load(in);
            Log.INFO("加载文件application.properties完成！");
        } catch (Exception e) {
            Log.ERROR("加载文件application.properties失败！");
        }
    }


    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }

    public Properties getProperties() {
        return properties;
    }
}
