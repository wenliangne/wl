package com.wenliang.mapper.utils;

import com.wenliang.core.log.Log;
import com.wenliang.mapper.cfg.DefaultRepositoryApplicationContext;
import com.wenliang.mapper.cfg.MapperConfiguration;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author wenliang
 * @date 2019-06-25
 * 简介：通过参数获取数据库连接
 */
public class DataSourceUtil {
    private static HikariConfig hikariConfig;
    private static DataSource ds;

    public static void init(MapperConfiguration cfg) {
        hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(cfg.getUrl());
        hikariConfig.setUsername(cfg.getUsername());
        hikariConfig.setPassword(cfg.getPassword());
        hikariConfig.setDriverClassName(cfg.getDriver());
//        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
//        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
//        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(hikariConfig);
    }

    public static DataSource getDs() {
        if (ds == null) {
            try {
                init(DefaultRepositoryApplicationContext.getMapperConfiguration());
            } catch (Exception e) {
                Log.ERROR("创建连接池失败！",e);
                throw e;
            }
        }
        return ds;
    }

    public static void setDs(DataSource ds) {
        DataSourceUtil.ds = ds;
    }

    public static Connection getConnection(MapperConfiguration cfg) {
        Connection connection=null;
        try {
            connection = ds.getConnection();
            if (connection != null) {
                Log.INFO("From pooled Get a database connection!");
                return connection;
            } else {
                Class.forName(cfg.getDriver());
                connection = DriverManager.getConnection(cfg.getUrl(), cfg.getUsername(), cfg.getPassword());
                Log.INFO("Get a database connection!");
                return connection;
            }
        } catch (Exception e) {
            Log.ERROR("Failed to get database connection!",e);
        }
        return null;
    }
}