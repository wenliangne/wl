package com.wenliang.context.plugins.transaction;

import com.wenliang.core.log.Log;
import com.wenliang.mapper.cfg.DefaultRepositoryApplicationContext;
import com.wenliang.mapper.plugins.TransactionManager;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author wenliang
 * @date 2019-08-01
 * 简介：
 */
public class DefaultTransactionManager  implements TransactionManager {
    @Override
    public void setAutoCommitTrue() {
        try {
            Log.INFO("开启事务自动提交!");
            setConnection();
            localConnection.get().setAutoCommit(true);
        } catch (SQLException e) {
            Log.ERROR("设置事务提交方式失败！",e);
        }
    }

    @Override
    public void setAutoCommitFalse() {
        try {
            Log.INFO("关闭事务自动提交!");
            setConnection();
            localConnection.get().setAutoCommit(false);
        } catch (SQLException e) {
            Log.ERROR("设置事务提交方式失败！",e);
        }
    }

    @Override
    public void rollback() {
        try {
            Log.WARN("出现异常，事务回滚!");
            localConnection.get().rollback();
        } catch (SQLException e) {
            Log.ERROR("事务回滚失败！",e);
        }
    }

    @Override
    public void commit() {
        try {
            Log.INFO("事务提交!");
            localConnection.get().commit();
        } catch (SQLException e) {
            Log.ERROR("事务提交失败！",e);
        }
    }

    @Override
    public void setConnection(Connection connection) {
        try {
            localConnection.set(connection);
        } catch (Exception e) {
            Log.ERROR("设置事务管理器数据库连接失败！",e);
        }
    }

    @Override
    public void setConnection() {
        try {
            Connection connection = DefaultRepositoryApplicationContext.getSqlSession().getConnection();
            localConnection.set(connection);
        } catch (Exception e) {
            Log.ERROR("设置事务管理器数据库连接失败！",e);
        }
    }

    @Override
    public Connection getConnection() {
        try {
            return localConnection.get();
        } catch (Exception e) {
            Log.ERROR("获取事务管理器数据库连接失败！",e);
            return null;
        }
    }

    @Override
    public void release() {
        try {
            Log.INFO("释放事务管理连接！");
            localConnection.get().close();
            localConnection.remove();
        } catch (SQLException e) {
            Log.ERROR("关闭连接失败！",e);
        }
    }


}
