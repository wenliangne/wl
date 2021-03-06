package com.wenliang.security.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wenliang
 * @date 2019-12-24
 * 简介：
 */
public interface SecurityService {

    public void login(HttpServletRequest request, HttpServletResponse response);

    public void logout(HttpServletRequest request, HttpServletResponse response);

    public void isLogin(HttpServletRequest request, HttpServletResponse response);

    public void isTargetRole(HttpServletRequest request, HttpServletResponse response);

    public void getRoles(HttpServletRequest request, HttpServletResponse response);

    public void getUsername(HttpServletRequest request, HttpServletResponse response);

    public void getUserInfo(HttpServletRequest request, HttpServletResponse response);

}
