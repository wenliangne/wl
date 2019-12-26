package com.wenliang.test.dao;

import java.util.List;

import com.wenliang.mapper.annotations.Select;
import com.wenliang.test.domain.UserRole;

/**
 * @author wenliang
 * @date 2019-12-26
 * 简介：
 */
public interface UserRoleDao {
    @Select("select * from user_role where userId=#{userId}")
    public List<UserRole> findByUserId(int userId);
}
