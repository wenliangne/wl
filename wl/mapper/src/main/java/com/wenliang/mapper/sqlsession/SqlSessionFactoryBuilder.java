package com.wenliang.mapper.sqlsession;

import java.io.InputStream;

/**
 * @author wenliang
 * @date 2019-07-10
 * 简介：
 */
public interface SqlSessionFactoryBuilder {

    public SqlSessionFactory build(InputStream inputStream);
}
