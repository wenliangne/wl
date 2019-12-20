package com.wenliang.mapper.cfg;


import java.util.ArrayList;
import java.util.List;

/**
 * @author wenliang
 * @date 2019-06-25
 * 简介：mapper的配置，用于保存sql语句和返回类型的全限定类名
 */
public class Mapper {

    private String sqlString;  // sql语句
    private String[] parameterTypes;
    private String[] sqlParameter;  // sql的参数名称列表
    private ThreadLocal<Object[]> localArgs = new ThreadLocal<>();
    private String resultType;  // 返回类型的全限定类名
    private Class annotation;  // sql语句的注解/xml的标签名



    /**
     * 通过原始sql和参数数组生成数据库需要的sql
     * @param sql
     * @param sqlParameter
     * @return
     */
    private String geneSQL(String sql, String[] sqlParameter) {
        for (int i = 0; i < sqlParameter.length; i++) {
            sql = sql.replace(sqlParameter[i], "?");
        }
        return sql;
    }

    /**
     * 获取原始sql中的参数并生成数组
     * @param sql
     * @return
     */
    private String[] geneSQLParameter(String sql) {
        List<String> paras = new ArrayList<>();
        int start = -1;
        int end = -1;
        while ((start = sql.indexOf("#{")) != -1) {
            end = sql.indexOf("}");
            String para = sql.substring(start, end+1);
            paras.add(para);
            sql = sql.substring(end+1);
        }
        return paras.toArray(new String[paras.size()]);
    }

    private void geneSQLParameter2(String[] parameters) {
        for (int i = 0; i < parameters.length; i++) {
            parameters[i]=parameters[i].replace("#{", "").replace("}", "");
        }
    }


    public Mapper() {
    }

    public Mapper(String sqlString, String[] parameterTypes, String resultType,Class annotation) {
        String[] parameters = geneSQLParameter(sqlString);
        this.sqlParameter = parameters;
        this.sqlString = geneSQL(sqlString, parameters);
        geneSQLParameter2(parameters);
        this.parameterTypes = parameterTypes;
        this.resultType = resultType;
        this.annotation = annotation;
    }

    public String getSqlString() {
        return sqlString;
    }

    public void setSqlString(String sqlString) {
        String[] parameters = geneSQLParameter(sqlString);
        this.sqlParameter = parameters;
        this.sqlString = geneSQL(sqlString, parameters);
        geneSQLParameter2(parameters);
    }

    public String[] getSqlParameter() {
        return sqlParameter;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(String[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public void setSqlParameter(String[] sqlParameter) {
        this.sqlParameter = sqlParameter;
    }

    public Class getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Class annotation) {
        this.annotation = annotation;
    }

    public ThreadLocal<Object[]> getLocalArgs() {
        return localArgs;
    }

    public void setLocalArgs(ThreadLocal<Object[]> localArgs) {
        this.localArgs = localArgs;
    }
}
