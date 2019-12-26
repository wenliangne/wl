package com.wenliang.security.authentication;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.wenliang.security.PageGenerator;
import com.wenliang.security.SecurityConfig;
import com.wenliang.security.SecurityContext;
import com.wenliang.security.encrypt.Encrypt;

/**
 * @author wenliang
 * @date 2019-12-25
 * 简介：
 */
public class DefaultSecurityService implements SecurityService {
    Encrypt encrypt;
    public DefaultSecurityService() {
        try {
            Class<?> encryptClass = Class.forName(SecurityContext.getSecurityConfig().getProperty("security.encryptClassName"));
            encrypt = (Encrypt) encryptClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void login(HttpServletRequest request, HttpServletResponse response){
        SecurityConfig securityConfig = SecurityContext.getSecurityConfig();
        HttpSession session = request.getSession();
        Class<?> userDetailServiceClass = null;
        UserDetailsService userDetailsService = null;
        UserDetail userDetail = null;
        String username = request.getParameter(securityConfig.getProperty("security.username"));
        String password = request.getParameter(securityConfig.getProperty("security.password"));
        try {
            userDetailServiceClass = Class.forName(securityConfig.getProperty("security.userDetailsService"));
            userDetailsService = (UserDetailsService) userDetailServiceClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        userDetail = userDetailsService.loadUserByUsername(username);
        try {
            if (encrypt.encrypt(password).equals(userDetail.getPassword())) {
                session.setAttribute(securityConfig.getProperty("security.user"), userDetail);
                session.setAttribute("username", userDetail.getUsername());
                String storyPageName = (String)session.getAttribute("storyPageName");
                String loginSuccess = securityConfig.getProperty("security.loginSuccess");
                if ("".equals(loginSuccess)) {
                    if (storyPageName == null || "".equals(storyPageName)) {
                        response.getOutputStream().write(PageGenerator.getDefaultLoginSuccessPage().getBytes());
                    } else {
                        response.sendRedirect(storyPageName);
                    }
                } else {
                    response.sendRedirect(request.getContextPath()+loginSuccess);
                }
            } else {
                response.setStatus(200);
                response.getOutputStream().write(PageGenerator.getDefaultLoginFailurePage().getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityConfig securityConfig = SecurityContext.getSecurityConfig();
        HttpSession session = request.getSession();
        session.removeAttribute(securityConfig.getProperty("security.user"));
        session.removeAttribute("username");
        String storyPageName = (String)session.getAttribute("storyPageName");
        String logoutSuccess = securityConfig.getProperty("security.logoutSuccess");
        try {
            if ("".equals(logoutSuccess)) {
                if (storyPageName == null || "".equals(storyPageName)) {
                    response.getOutputStream().write(PageGenerator.getDefaultLogoutPage().getBytes());
                } else {
                    response.sendRedirect(storyPageName);
                }
                response.getOutputStream().write("注销成功！".getBytes());
            } else {
                response.sendRedirect(request.getContextPath() + logoutSuccess);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isLogin(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        try {
            if (session.getAttribute("username") == null ) {
                response.getOutputStream().write("false".getBytes());
                return false;
            } else {
                response.getOutputStream().write("true".getBytes());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void getUsername(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        try {
            ServletOutputStream out = response.getOutputStream();
            Object username = session.getAttribute("username");
            if (username == null) {
                out.write("null".getBytes());
            } else {
                out.write(((String)username ).getBytes());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
