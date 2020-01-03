package com.wenliang.mapper.sqlsession;

import com.wenliang.mapper.cfg.Mapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * @author wenliang
 * @date 2019-06-25
 * 简介：执行器
 */
public interface Executor {
    public <E> List<E> selectList(Mapper mapper, Connection conn);

    public <E> E select(Mapper mapper, Connection conn);

    public <E> boolean insert(Mapper mapper, Connection conn);

    public <E> int update(Mapper mapper, Connection conn);

    public <E> int delete(Mapper mapper, Connection conn);

    public void release(Connection conn, Statement pstm, ResultSet rs);
}
