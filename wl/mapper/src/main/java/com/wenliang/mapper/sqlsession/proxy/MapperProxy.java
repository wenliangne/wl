package com.wenliang.mapper.sqlsession.proxy;

import com.wenliang.core.log.Log;
import com.wenliang.mapper.annotations.Delete;
import com.wenliang.mapper.annotations.Insert;
import com.wenliang.mapper.annotations.Select;
import com.wenliang.mapper.annotations.Update;
import com.wenliang.mapper.cfg.Mapper;
import com.wenliang.mapper.sqlsession.defaults.DefaultExecutor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author wenliang
 * @date 2019-06-25
 * 简介：maper的代理对象
 */
public class MapperProxy implements InvocationHandler {

    private Map<String,Mapper> mappers;
    private Connection conn;

    public MapperProxy(Map<String,Mapper> mappers,Connection conn){
        this.mappers = mappers;
        this.conn = conn;
    }

    public  Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //1.获取方法名
        String methodName = method.getName();
        //2.获取方法所在类的名称
        String className = method.getDeclaringClass().getName();
        //3.组合key
        String key = className+"."+methodName;
        //4.获取mappers中的Mapper对象
        Mapper mapper = mappers.get(key);
        //5.判断是否有mapper
        if(mapper == null){
            if ("toString".equals(methodName)) {
                return super.toString();
            } else if ("equals".equals(methodName)) {
                return super.equals(args[0]);
            }
            throw new IllegalArgumentException("传入的参数有误！");
        }
        mapper.getLocalArgs().set(args);
        DefaultExecutor defaultExecutor = new DefaultExecutor();
        Class annotation = mapper.getAnnotation();
        if (Select.class.isAssignableFrom(annotation)) {
            if (List.class.isAssignableFrom(method.getReturnType())) {
                return defaultExecutor.selectList(mapper, conn);
            } else {
                return defaultExecutor.select(mapper, conn);
            }
        } else if (Update.class.isAssignableFrom(annotation)) {
            return defaultExecutor.update(mapper, conn);
        } else if (Delete.class.isAssignableFrom(annotation)) {
            return defaultExecutor.delete(mapper, conn);
        } else if (Insert.class.isAssignableFrom(annotation)) {
            return defaultExecutor.insert(mapper, conn);
        }
        return null;
    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            Log.ERROR("关闭连接失败！",e);
        }

    }
}
