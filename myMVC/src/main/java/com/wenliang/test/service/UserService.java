package com.wenliang.test.service;

import com.wenliang.mapper.plugins.page.PageInfo;
import com.wenliang.test.domain.Role;
import com.wenliang.test.domain.User;
import com.wenliang.test.domain.UserRole;

import java.util.List;

/**
 * @author wenliang
 * @date 2019-07-16
 * 简介：
 */
public interface UserService {

    public List<User> findAll();

    public PageInfo findPage(Integer pageNum, Integer pageSize);

    public User findByUsernameAndPassword2(String username, String password);

    public User findByUsername(String username);

    public String AspectThrowing();

    public void trans();

    public void transError();

    public List<UserRole> findByUserId(int userId);

    public List<Role> findAllRole();
}
