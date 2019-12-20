package com.wenliang.mapper.plugins;

import java.sql.Connection;

/**
 * @author wenliang
 * @date 2019-07-18
 * 简介：
 */
public interface TransactionManager {

    public static ThreadLocal<Connection> localConnection=new ThreadLocal<>();

    public void setAutoCommitTrue();

    public void setAutoCommitFalse();

    public void rollback();

    public void commit();

    public void setConnection(Connection connection);

    public void setConnection();

    public Connection getConnection();

    public void release();

}
