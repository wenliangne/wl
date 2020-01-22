package com.wenliang.core.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wenliang.core.log.Log;

/**
 * @author wenliang
 * @date 2020-01-02
 * 简介：
 */
public class EntityUtils {

    /**
     * 将结果集封装为对象
     * @param rs
     * @param entityClass
     */
    public static <T> T rsToEntity(ResultSet rs,Class entityClass) {
        T entity = null;
        try {
            entity = (T)entityClass.newInstance();
            // 1.获取结果集元数据
            ResultSetMetaData metaData = rs.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                try {
                    // 2.获取结果集第i列的列名
                    String columnName = metaData.getColumnName(i);
                    // 3.获取结果集名为columnName的值
                    Object columnValue = rs.getObject(columnName);
                    // 4.创建属性描述器
                    PropertyDescriptor pd = new PropertyDescriptor(StringUtils.ConvertUnderscoreToUppercase(columnName), entityClass);
                    // 5.获取类的写方法
                    Method writeMethod = pd.getWriteMethod();
                    // 6.执行对象的写方法
                    writeMethod.invoke(entity, columnValue);
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    /**
     * 将结果集封装为对象
     * @param rs
     * @param entityClass
     */
    public static <T> List<T> rsToEntityList(ResultSet rs, Class entityClass){
        List<T> list = new ArrayList<>();
        try {
            while (rs.next()) {
                T o = rsToEntity(rs, entityClass);
                list.add(o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 将map转换为实体类
     * @param map
     * @param entityClass
     * @param <T>
     * @return
     */
    public static <T> T mapToEntity(Map<String,Object> map, Class<?> entityClass) {
        T entity = null;
        try {
            entity = (T)entityClass.newInstance();
        } catch (Exception e) {
            Log.ERROR("创建对象失败：" + entityClass.getName(), e);
            return null;
        }
        for (String key : map.keySet()) {
            try {
                PropertyDescriptor pd = new PropertyDescriptor(StringUtils.ConvertUnderscoreToUppercase(key), entityClass);
                // 5.获取类的写方法
                Method writeMethod = pd.getWriteMethod();
                // 6.执行对象的写方法
                writeMethod.invoke(entity, map.get(key));
            } catch (Exception e) {
            }
        }
        return entity;
    }

    /**
     * 将实体类转换为map
     * @param <T>
     * @return
     */
    public static <T> Map<String, Object> entityToMap(T entiry) {

        Map<String, Object> map = new HashMap<>();
        Method[] declaredMethods = entiry.getClass().getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            String methodName = declaredMethod.getName();
            try {
                if (methodName.startsWith("get")) {
                    String key = StringUtils.convertFirstCharToLowerCase(methodName.substring(3));
                    Object value = declaredMethod.invoke(entiry);
                    map.put(key, value);
                }
            } catch (Exception e) {

            }
        }
        return map;
    }

    /**
     * 将数据封装到map中
     * @param rs
     * @return
     * @throws SQLException
     */
    public static Map<String, Object> rsToMap(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            String key = metaData.getColumnName(i);
            map.put(StringUtils.ConvertUnderscoreToUppercase(key), rs.getObject(key));
        }
        return map;
    }
    /**
     * 将多条数据数据封装到map中再装入集合
     * @param rs
     * @return
     * @throws SQLException
     */
    public static List<Map<String, Object>> rsToMapList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        while (rs.next()) {
            try {
                Map<String, Object> map = rsToMap(rs);
                list.add(map);
            } catch (Exception e) {
            }
        }
        return list;
    }


}
