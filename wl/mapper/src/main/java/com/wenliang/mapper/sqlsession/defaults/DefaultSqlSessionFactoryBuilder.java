package com.wenliang.mapper.sqlsession.defaults;

import com.wenliang.mapper.cfg.DefaultRepositoryApplicationContext;
import com.wenliang.mapper.cfg.MapperConfiguration;
import com.wenliang.mapper.sqlsession.SqlSessionFactory;
import com.wenliang.mapper.sqlsession.defaults.DefaultSqlSessionFactory;
import com.wenliang.mapper.utils.XMLConfigBuilder;

import java.io.InputStream;

/**
 * @author wenliang
 * @date 2019-06-25
 * 简介：用于构建SqlSessionFactory
 */
public class DefaultSqlSessionFactoryBuilder {
    /**
     * 根据参数的字节输入流来构建一个SqlSessionFactory工厂
     * @param inputStream
     * @return
     */
    public SqlSessionFactory build(InputStream inputStream){
        MapperConfiguration cfg = XMLConfigBuilder.loadConfiguration(inputStream);
        DefaultRepositoryApplicationContext.setMapperConfiguration(cfg);
        return  new DefaultSqlSessionFactory(cfg);
    }
}
