package com.wenliang.mapper.sqlsession.defaults;

import com.wenliang.core.log.Log;
import com.wenliang.core.util.EntityUtils;
import com.wenliang.core.util.StringUtils;
import com.wenliang.mapper.cfg.Mapper;
import com.wenliang.mapper.plugins.TransactionManager;
import com.wenliang.mapper.plugins.page.PageHelper;
import com.wenliang.mapper.plugins.page.PageInfo;
import com.wenliang.mapper.sqlsession.Executor;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


/**
 * @author wenliang
 * @date 2019-06-25
 * 简介：
 */
public class DefaultExecutor implements Executor {
    public <E> List<E> selectList(Mapper mapper, Connection conn) {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        PreparedStatement psmtCount = null;
        ResultSet resultSet = null;
        int curSize=0;
        boolean transactionManagerFlag = false;
        Connection connection = TransactionManager.localConnection.get();
        if (connection != null) {
            conn = connection;
            transactionManagerFlag = true;
        }
        try {
            // 1.取出mapper中的数据
            String queryString = mapper.getSqlString();
            String resultType = mapper.getResultType();
            Class domainClass = Class.forName(resultType);
            //分页插件处理
            ThreadLocal<PageInfo> localPageInfo = PageHelper.getLocalPageInfo();
            PageInfo pageInfo = localPageInfo.get();
            if (pageInfo != null) {
                int row=pageInfo.getPageNum() >= 1 ? (pageInfo.getPageNum()-1)*pageInfo.getPageSize() : 0;
                queryString = queryString + " limit " + row + "," + pageInfo.getPageSize();
            }
            // 2.获取PreparedStatement对象
            psmt = conn.prepareStatement(queryString);
            // 3.向psmt对象中添加sql参数
            addExecuteConditions(psmt,mapper);
            // 4.打印psmt对象
            Log.INFO("Sql: "+queryString);
            Log.INFO("Parameters: "+ Arrays.toString(mapper.getSqlParameter())+" "+Arrays.toString(mapper.getLocalArgs().get()));
            Log.INFO("PreparedStatement: "+psmt.toString());
            try {
                // 5.执行SQL语句，获取结果集
                rs = psmt.executeQuery();
                // 6.打印结果日志
                rs.last();
                curSize = rs.getRow();
                Log.INFO("Operation type: selectList    "+"result number: "+curSize);
                rs.beforeFirst();
            } catch (Exception e) {
                Log.ERROR("Operation type: selectList    "+"result Exception",e);
            }
            // 7.封装结果集
            List<E> list = EntityUtils.rsToEntityList(rs, domainClass);//定义返回值
            //分页插件处理
            if (pageInfo != null) {
                String sqlCount = geneSqlToQueryCount(mapper.getSqlString());
                psmtCount = conn.prepareStatement(sqlCount);
                resultSet = psmtCount.executeQuery();
                resultSet.next();
                int count = resultSet.getInt(1);
                pageInfo.setTotal(count);
                pageInfo.setSize(curSize);
                pageInfo.setList(list);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 8.释放资源
            release(null,psmt,rs);
            if (transactionManagerFlag) {
                release(null, psmtCount, resultSet);
            } else {
                release(conn,psmtCount,resultSet);
            }
        }
    }

    @Override
    public <E> E select(Mapper mapper, Connection conn) {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        boolean transactionManagerFlag = false;
        Connection connection = TransactionManager.localConnection.get();
        if (connection != null) {
            conn = connection;
            transactionManagerFlag = true;
        }
        try {
            // 1.取出mapper中的数据
            String sqlString = mapper.getSqlString();
            String resultType = mapper.getResultType();
            Class domainClass = Class.forName(resultType);
            // 2.获取psmt对象
            psmt = conn.prepareStatement(sqlString);
            // 3.向psmt对象中添加sql参数
            addExecuteConditions(psmt,mapper);
            // 4.打印psmt对象
            Log.INFO("Sql: "+sqlString);
            Log.INFO("Parameters: "+ Arrays.toString(mapper.getSqlParameter())+" "+Arrays.toString(mapper.getLocalArgs().get()));
            Log.INFO("PreparedStatement: "+psmt.toString());
            try {
                // 5.执行SQL语句
                rs = psmt.executeQuery();
                // 6.打印结果日志
                rs.last();
                Log.INFO("Operation type: select    "+"result number: "+rs.getRow());
                rs.beforeFirst();
            } catch (Exception e) {
                Log.ERROR("Operation type: select    " + "result: Exception" + rs.getRow(),e);
            }
            // 7.封装结果集
            // 7.1实例化要封装的实体类对象
            rs.next();
            Object domain = EntityUtils.rsToEntity(rs, domainClass);
            return (E)domain;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            // 8.释放资源
            if (transactionManagerFlag) {
                release(null, psmt, rs);
            } else {
                release(conn,psmt,rs);
            }
        }
    }

    @Override
    public boolean insert(Mapper mapper, Connection conn) {
        PreparedStatement psmt = null;
        boolean transactionManagerFlag = false;
        Connection connection = TransactionManager.localConnection.get();
        if (connection != null) {
            conn = connection;
            transactionManagerFlag = true;
        }
        try {
            String insertString = mapper.getSqlString();
            psmt = conn.prepareStatement(insertString);
            addExecuteConditions(psmt,mapper);
            Log.INFO("Sql: "+insertString);
            Log.INFO("Parameters: "+ Arrays.toString(mapper.getSqlParameter())+" "+Arrays.toString(mapper.getLocalArgs().get()));
            Log.INFO("PreparedStatement: "+psmt.toString());
            psmt.execute();
            Log.INFO("Operation type: insert    "+"result: true");
        } catch (Exception e) {
            Log.ERROR("Operation type: insert    "+"result: false",e);
            throw new RuntimeException(e);
        } finally {
            if (transactionManagerFlag) {
                release(null,psmt,null);
            } else {
                release(conn,psmt,null);
            }
        }
        return true;
    }

    @Override
    public int update(Mapper mapper, Connection conn) {
        PreparedStatement psmt = null;
        int rowsAffected;
        boolean transactionManagerFlag = false;
        Connection connection = TransactionManager.localConnection.get();
        if (connection != null) {
            conn = connection;
            transactionManagerFlag = true;
        }
        try {
            String updateString = mapper.getSqlString();
            psmt = conn.prepareStatement(updateString);
            addExecuteConditions(psmt,mapper);
            Log.INFO("Sql: "+updateString);
            Log.INFO("Parameters: "+ Arrays.toString(mapper.getSqlParameter())+" "+Arrays.toString(mapper.getLocalArgs().get()));
            Log.INFO("PreparedStatement: "+psmt.toString());
            rowsAffected = psmt.executeUpdate();
            Log.INFO("Operation type: update    "+"result: rows affected="+rowsAffected);
        } catch (Exception e) {
            Log.ERROR("Operation type: update    "+"result: false",e);
            throw new RuntimeException(e);
        } finally {
            if (transactionManagerFlag) {
                release(null, psmt, null);
            } else {
                release(conn,psmt,null);
            }
        }
        return rowsAffected;
    }

    @Override
    public int delete(Mapper mapper, Connection conn) {
        PreparedStatement psmt = null;
        int rowsAffected;
        boolean transactionManagerFlag = false;
        Connection connection = TransactionManager.localConnection.get();
        if (connection != null) {
            conn = connection;
            transactionManagerFlag = true;
        }
        try {
            String deleteString = mapper.getSqlString();
            psmt = conn.prepareStatement(deleteString);
            addExecuteConditions(psmt,mapper);
            Log.INFO("Sql: "+deleteString);
            Log.INFO("Parameters: "+ Arrays.toString(mapper.getSqlParameter())+" "+Arrays.toString(mapper.getLocalArgs().get()));
            Log.INFO("PreparedStatement: "+psmt.toString());
            rowsAffected = psmt.executeUpdate();
            Log.INFO("Operation type: delete    "+"result: row affected="+rowsAffected);
        } catch (Exception e) {
            Log.ERROR("Operation type: delete    "+"result: false",e);
            throw new RuntimeException(e);
        } finally {
            if (transactionManagerFlag) {
                release(null, psmt, null);
            } else {
                release(conn,psmt,null);
            }
        }
        return rowsAffected;
    }

    /**
     * 释放资源
     * @param pstm
     * @param rs
     */
    public void release(Connection conn, Statement pstm, ResultSet rs) {
        if(rs != null){
            try {
                rs.close();
            }catch(Exception e){
                Log.ERROR("关闭ResultSet失败！",e);
            }
        }
        if(pstm != null){
            try {
                pstm.close();
            }catch(Exception e){
                Log.ERROR("关闭Statement失败！",e);
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                Log.ERROR("关闭Connection失败！",e);
            }
        }
    }
    /**
     * 为PreparedStatement对象添加参数
     * @param psmt
     * @param mapper
     */
    private void addExecuteConditions(PreparedStatement psmt, Mapper mapper ) {
        String[] sqlParameter = mapper.getSqlParameter();
        String[] parameterTypes = mapper.getParameterTypes();
        Object[] args = mapper.getLocalArgs().get();
        Class domainClass = null;
        Object domain=null;
        try {
            domainClass = Class.forName(parameterTypes[0]);
            domain = args[0];
        } catch (Exception e) {
            Log.INFO("There is no object as a parameter, but the basic data type is used as the parameter!");
        }
        if (domainClass != null && isComplexObject(domainClass)) {
            String tempColumn = null;
            for (int i = 0; i < sqlParameter.length; i++) {
                try {
                    tempColumn = sqlParameter[i];
                    PropertyDescriptor pd = new PropertyDescriptor(tempColumn, domainClass);
                    Method readMethod = pd.getReadMethod();
                    Object domainProp = readMethod.invoke(domain);
                    psmt.setObject(i+1, domainProp);
                } catch (Exception e) {
                    try {
                        psmt.setObject(i+1,null);
                    } catch (Exception e1) {
                        Log.ERROR("Parameter "+sqlParameter[i]+" cannot is null!");
                    }
                }
            }
        } else if (domainClass != null && Map.class.isAssignableFrom(domainClass.getClass())) {
            String tempKey = null;
            Map tempMap = (Map) domain;
            for (int i = 0; i < sqlParameter.length; i++) {
                try {
                    tempKey = sqlParameter[i];
                    psmt.setObject(i+1,tempMap.get(tempKey));
                } catch (Exception e) {
                    try {
                        psmt.setObject(i+1,null);
                    } catch (Exception e1) {
                        Log.ERROR("Parameter "+sqlParameter[i]+" cannot is null!");
                    }
                }
            }
        } else if (domainClass != null && List.class.isAssignableFrom(domainClass.getClass())) {
            List tempList = (List) domain;
            for (int i = 0; i < sqlParameter.length; i++) {
                try {
                    psmt.setObject(i + 1, tempList.get(i));
                } catch (Exception e) {
                    try {
                        psmt.setObject(i+1,null);
                    } catch (Exception e1) {
                        Log.ERROR("Parameter "+sqlParameter[i]+" cannot is null!");
                    }
                }
            }
        } else {
            for (int i = 0; i < sqlParameter.length; i++) {
                try {
                    domain = args[i];
                    psmt.setObject(i + 1, domain);
                } catch (Exception e) {
                    try {
                        psmt.setObject(i + 1, null);
                        Log.ERROR("Parameter  is error!");
                    } catch (Exception e1) {
                        Log.ERROR("Parameter " + sqlParameter[i] + " cannot is null!");
                    }
                }
            }
        }



    }
    /**
     * 判断是否为复杂对象（及基本数据类型及字符串以外的对象）
     * @param parameterType
     * @return
     */
    private boolean isComplexObject(Class<?> parameterType) {
        if (byte.class.isAssignableFrom(parameterType) || Byte.class.isAssignableFrom(parameterType)
                ||int.class.isAssignableFrom(parameterType) || Integer.class.isAssignableFrom(parameterType)
                ||float.class.isAssignableFrom(parameterType) || Float.class.isAssignableFrom(parameterType)
                ||double.class.isAssignableFrom(parameterType) || Double.class.isAssignableFrom(parameterType)
                ||long.class.isAssignableFrom(parameterType) || Long.class.isAssignableFrom(parameterType)
                ||String.class.isAssignableFrom(parameterType)|| Date.class.isAssignableFrom(parameterType)
                ) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取查询总条数的sql
     * @param sql
     * @return
     */
    private String geneSqlToQueryCount(String sql) {
        String center = sql.toUpperCase().replace("SELECT", "").trim().split(" ")[0].split(",")[0];
        String end = sql.substring(sql.toUpperCase().indexOf("FROM"));
        return "select count("+center+")"+end;
    }





}
