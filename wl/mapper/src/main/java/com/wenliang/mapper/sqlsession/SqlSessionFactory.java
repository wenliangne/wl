package com.wenliang.mapper.sqlsession;

/**
 * @author wenliang
 * @date 2019-06-25
 * 简介：创建SqlSession的工厂
 */
public interface SqlSessionFactory {
    /**
     * 用于打开一个新的SqlSession对象
     * @return
     */
    SqlSession openSession();
}
