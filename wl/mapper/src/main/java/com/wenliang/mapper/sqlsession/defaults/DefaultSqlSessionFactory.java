package com.wenliang.mapper.sqlsession.defaults;

import com.wenliang.mapper.cfg.MapperConfiguration;
import com.wenliang.mapper.sqlsession.SqlSession;
import com.wenliang.mapper.sqlsession.SqlSessionFactory;

/**
 * @author wenliang
 * @date 2019-06-25
 * 简介：
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private MapperConfiguration cfg;

    public DefaultSqlSessionFactory(MapperConfiguration cfg){
        this.cfg = cfg;
    }

    public SqlSession openSession() {
        return new DefaultSqlSession(cfg);
    }
}
