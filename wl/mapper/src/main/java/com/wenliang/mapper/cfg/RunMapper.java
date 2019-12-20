package com.wenliang.mapper.cfg;

import com.wenliang.core.io.Resources;
import com.wenliang.mapper.sqlsession.SqlSession;
import com.wenliang.mapper.sqlsession.SqlSessionFactory;
import com.wenliang.mapper.sqlsession.defaults.DefaultSqlSessionFactoryBuilder;

import java.io.InputStream;

/**
 * @author wenliang
 * @date 2019-07-15
 * 简介：
 */
public class RunMapper {
    private RunMapper() {
    }
    public static SqlSession Run(){
        return Run("SqlMapConfig.xml");
    }

    public static SqlSession Run(String filePath) {
        InputStream in = Resources.getResourceAsStream(filePath);
        //2.创建SqlSessionFactory工厂
        DefaultSqlSessionFactoryBuilder builder = new DefaultSqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(in);
        //3.使用工厂生产SqlSession对象
        return factory.openSession();
    }
}
