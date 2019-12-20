package com.wenliang.mapper.cfg;


import com.wenliang.core.container.BeanApplicationContext;
import com.wenliang.mapper.sqlsession.SqlSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author wenliang
 * @date 2019-07-09
 * 简介：
 */
public class DefaultRepositoryApplicationContext implements BeanApplicationContext {

    private static SqlSession sqlSession;
    private static MapperConfiguration mapperConfiguration = new MapperConfiguration();
    private static Map<String,Object> context=new HashMap<>();
    public static Map getContext() {
        return context;
    }


    public static Object get(String key) {
        return context.get(key);
    }

    public static Object put(String key,Object object) {
        return context.put(key, object);
    }

    public static void putAll(Map<String,Object> map) {
        context.putAll(map);
    }

    public static Set<String> keySet() {
        return context.keySet();
    }

    public static Set<Map.Entry<String,Object>> entrySet() {
        return context.entrySet();
    }

    public static MapperConfiguration getMapperConfiguration() {
        return mapperConfiguration;
    }

    public static void setMapperConfiguration(MapperConfiguration configuration) {
        mapperConfiguration = configuration;
    }

    private DefaultRepositoryApplicationContext() {
    }

    public static SqlSession getSqlSession() {
        return sqlSession;
    }

    public static void setSqlSession(SqlSession sqlSession) {
        DefaultRepositoryApplicationContext.sqlSession = sqlSession;
    }
}
