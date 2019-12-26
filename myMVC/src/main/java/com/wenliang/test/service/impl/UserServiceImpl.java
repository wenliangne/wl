package com.wenliang.test.service.impl;

import com.wenliang.context.annotations.Autowired;
import com.wenliang.context.annotations.Service;
import com.wenliang.context.annotations.Transaction;
import com.wenliang.mapper.plugins.page.PageHelper;
import com.wenliang.mapper.plugins.page.PageInfo;
import com.wenliang.test.dao.RoleDao;
import com.wenliang.test.dao.UserDao;
import com.wenliang.test.dao.UserRoleDao;
import com.wenliang.test.domain.Role;
import com.wenliang.test.domain.User;
import com.wenliang.test.domain.UserRole;
import com.wenliang.test.service.UserService;

import java.util.List;

/**
 * @author wenliang
 * @date 2019-07-16
 * 简介：
 */
@Service
@Transaction
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    UserRoleDao userRoleDao;

    @Autowired
    RoleDao roleDao;

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public PageInfo findPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<User> userLiast = userDao.findAll();
        return PageHelper.getPageInfo();
    }

    @Override
    public User findByUsernameAndPassword2(String username, String password) {
        return userDao.findByUsernameAndPassword2(username,password);
    }

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public String AspectThrowing() {
        int i = 1 / 0;
        return null;
    }

    @Override
    public void trans() {
        System.out.println("执行了service的方法trans");
        User jly = userDao.findByUsernameAndPassword2("jly", "123");
        User wl = userDao.findByUsernameAndPassword2("wl", "wwwwwwwww");
        jly.setGender(jly.getGender()-1);
        wl.setGender(wl.getGender()+1);
        userDao.update(jly);
        userDao.update(wl);
    }

    @Override
    public void transError() {
        System.out.println("执行了service的方法transError");
        User jly = userDao.findByUsernameAndPassword2("jly", "123");
        User wl = userDao.findByUsernameAndPassword2("wl", "wwwwwwwww");
        jly.setGender(jly.getGender()-1);
        wl.setGender(wl.getGender()+1);
        userDao.update(jly);
        int i = 1 / 0;
        userDao.update(wl);
    }

    @Override
    public List<UserRole> findByUserId(int userId) {
        return userRoleDao.findByUserId(userId);
    }

    @Override
    public List<Role> findAllRole() {
        return roleDao.findALl();
    }
}
