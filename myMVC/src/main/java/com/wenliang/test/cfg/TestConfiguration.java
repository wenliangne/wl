package com.wenliang.test.cfg;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.wenliang.context.annotations.*;

import java.beans.PropertyVetoException;

/**
 * @author wenliang
 * @date 2019-07-16
 * 简介：
 */

@Configuration
@ControllerScan({"com.wenliang.test.controller"})
@MapperScan({"com.wenliang.test.dao"})
@ComponentScan({"com.wenliang.test","aaa"})
//@ImportProperties("aaa.properties")
public class TestConfiguration {

    @Value("#{datasource.url}")
    private String url;
    @Value("#{datasource.driver}")
    private String driver;
    @Value("#{datasource.username}")
    private String username;
    @Value("#{datasource.password}")
    private String password;

    @Bean
    public String aaa(String name,String bbb) {
        return username+password+name;
    }

    @Bean
    public ComboPooledDataSource bbb(ComboPooledDataSource dataSource) {
        return dataSource;
    }

    @Bean
    public ComboPooledDataSource dataSource() {
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        try {
            comboPooledDataSource.setDriverClass(driver);
            comboPooledDataSource.setJdbcUrl(url);
            comboPooledDataSource.setUser(username);
            comboPooledDataSource.setPassword(password);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        return comboPooledDataSource;
    }

}
