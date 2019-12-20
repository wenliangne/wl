package com.wenliang.mapper.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wenliang
 * @date 2019-06-25
 * 简介：数据库连接的配置
 */
public class MapperConfiguration {

    private String driver;  //数据可驱动
    private String url;  //数据库地址
    private String username;  //数据库用户名
    private String password;  //数据可密码

    private String databaseType;  //数据库类型
    private boolean pooled;  //是否使用数据库连接词

    private Map<String,Mapper> mappers = new HashMap<String,Mapper>();

    private List<Class<?>> interfaceList = new ArrayList<>();

    private String[] mapperScan;

    public MapperConfiguration() {
    }

    public MapperConfiguration(String driver, String url, String username, String password) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, Mapper> getMappers() {
        return mappers;
    }

    public void setMappers(Map<String, Mapper> mappers) {
        this.mappers.putAll(mappers);
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public boolean getPooled() {
        return pooled;
    }

    public void setPooled(boolean pooled) {
        this.pooled = pooled;
    }

    public List<Class<?>> getInterfaceList() {
        return interfaceList;
    }

    public void setInterfaceList(List<Class<?>> interfaceList) {
        this.interfaceList = interfaceList;
    }

    public void addInterfaceList(List<Class<?>> interfaceList) {
        this.interfaceList.addAll(interfaceList);
    }
    public void addInterface(Class<?> Ainterface) {
        this.interfaceList.add(Ainterface);
    }

    public String[] getMapperScan() {
        return mapperScan;
    }

    public void setMapperScan(String[] mapperScan) {
        this.mapperScan = mapperScan;
    }
}