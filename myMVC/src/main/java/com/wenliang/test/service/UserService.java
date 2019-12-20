package com.wenliang.test.service;

import com.wenliang.mapper.plugins.page.PageInfo;
import com.wenliang.test.domain.User;

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

    public String AspectThrowing();

    public void trans();

    public void transError();
}
