package com.wenliang.test.dao;

import java.util.List;

import com.wenliang.mapper.annotations.Select;
import com.wenliang.test.domain.Role;

/**
 * @author wenliang
 * @date 2019-12-26
 * 简介：
 */
public interface RoleDao {
    @Select("select * from role")
    public List<Role> findALl();
}
