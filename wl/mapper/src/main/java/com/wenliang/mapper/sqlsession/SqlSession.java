package com.wenliang.mapper.sqlsession;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author wenliang
 * @date 2019-06-25
 * 简介：数据库连接会话，用于创建实体类对应的代理mapper
 */
public interface SqlSession {

    /**
     * 根据参数创建mapper的代理对象
     * @param daoInterfaceClass
     * @param <T>
     * @return
     */
    <T> T getMapper(Class<T> daoInterfaceClass);

    public void setDs(DataSource ds);

    public DataSource getDs();

    public Connection getConnection();

}
