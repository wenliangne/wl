package com.wenliang.mapper.sqlsession.defaults;

import com.wenliang.core.log.Log;
import com.wenliang.core.util.StringUtils;
import com.wenliang.mapper.cfg.DefaultRepositoryApplicationContext;
import com.wenliang.mapper.cfg.MapperConfiguration;
import com.wenliang.mapper.sqlsession.SqlSession;
import com.wenliang.mapper.sqlsession.proxy.MapperProxy;
import com.wenliang.mapper.utils.DataSourceUtil;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author wenliang
 * @date 2019-06-25
 * 简介：
 */
public class DefaultSqlSession implements SqlSession {

    private MapperConfiguration cfg;
    private DataSource ds;

    public DefaultSqlSession(MapperConfiguration cfg){
        this.cfg = cfg;
        ds = DataSourceUtil.getDs();
    }

    @Override
    public void setDs(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public DataSource getDs() {
        return ds;
    }
    @Override
    public Connection getConnection() {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            Log.ERROR("从数据源中获取连接失败！",e);
            return null;
        }
    }

    @Override
    public <T> T getMapper(Class<T> daoInterfaceClass) {
        T o = (T) DefaultRepositoryApplicationContext.get(StringUtils.convertFirstCharToLowerCase(daoInterfaceClass.getSimpleName()));
        if (o != null) {
            return o;
        } else {
            o = (T) Proxy.newProxyInstance(daoInterfaceClass.getClassLoader(),
                    new Class[]{daoInterfaceClass}, new MapperProxy(cfg.getMappers(),getDs() ));
            DefaultRepositoryApplicationContext.put(StringUtils.convertFirstCharToLowerCase(daoInterfaceClass.getSimpleName()), o);
            return o;
        }
    }

}
