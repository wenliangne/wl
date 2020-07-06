package com.wenliang.security.authentication;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.management.relation.Role;
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
    SecurityConfig securityConfig;
    UserDetailsService userDetailsService;
    public DefaultSecurityService() {
        try {
            securityConfig = SecurityContext.getSecurityConfig();
            Class<?> encryptClass = Class.forName(securityConfig.getProperty("security.encryptClassName"));
            encrypt = (Encrypt) encryptClass.newInstance();
            Class<?> userDetailServiceClass = Class.forName(securityConfig.getProperty("security.userDetailsService"));
            userDetailsService = (UserDetailsService) userDetailServiceClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void login(HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession();
        UserDetail userDetail = null;
        String username = request.getParameter(securityConfig.getProperty("security.username"));
        String password = request.getParameter(securityConfig.getProperty("security.password"));
        userDetail = userDetailsService.loadUserByUsername(username);
        userDetail.setRole(correctionRole(userDetail.getRole()));
        try {
            if (encrypt.encrypt(password).equals(userDetail.getPassword())) {
                session.setAttribute(securityConfig.getProperty("security.sessionUserKey"), userDetail);
                session.setAttribute(securityConfig.getProperty("security.sessionUsernameKey"), userDetail.getUsername());
                String storyPageName = (String)session.getAttribute("storyPageName");
                String loginSuccess = securityConfig.getProperty("security.loginSuccess");
                List<String> role = userDetail.getRole();
                if (role.size() > 0) {
                    String roleName = role.get(0);
                    String tempLoginSuccess = securityConfig.getProperty("security.loginSuccess." + roleName);
                    if (tempLoginSuccess != null && !"".equals(tempLoginSuccess)) {
                        loginSuccess = tempLoginSuccess;
                    }
                }
                if (!"".equals(loginSuccess)) {
                    response.sendRedirect(request.getContextPath()+loginSuccess);
                } else if (storyPageName != null && !"".equals(storyPageName)) {
                    response.sendRedirect(storyPageName);
                } else {
                    response.getOutputStream().write(PageGenerator.getDefaultLoginSuccessPage().getBytes());
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
        HttpSession session = request.getSession();
        session.removeAttribute(securityConfig.getProperty("security.sessionUserKey"));
        session.removeAttribute(securityConfig.getProperty("security.sessionUsernameKey"));
        String storyPageName = (String)session.getAttribute("storyPageName");
        String logoutSuccess = securityConfig.getProperty("security.logoutSuccess");
        try {
            if (!"".equals(logoutSuccess)) {
                response.sendRedirect(request.getContextPath()+logoutSuccess);
            } else if (storyPageName != null && !"".equals(storyPageName)) {
                response.sendRedirect(storyPageName);
            } else {
                response.getOutputStream().write(PageGenerator.getDefaultLogoutPage().getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void isLogin(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        try {
            if (session.getAttribute(securityConfig.getProperty("security.sessionUsernameKey")) == null ) {
                response.getOutputStream().write("false".getBytes());
            } else {
                response.getOutputStream().write("true".getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getUsername(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        try {
            ServletOutputStream out = response.getOutputStream();
            Object username = session.getAttribute(securityConfig.getProperty("security.sessionUsernameKey"));
            if (username == null) {
                out.write("null".getBytes());
            } else {
                out.write(((String)username ).getBytes());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getUserInfo(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        try {
            response.setContentType("application/json;charset=UTF8");
            PrintWriter writer = response.getWriter();
            Object user = session.getAttribute(securityConfig.getProperty("security.sessionUserKey"));
            if (user == null) {
                writer.write("");
            } else {
                writer.write(mapToJsonStr((Map<String,String>) user));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将map转换为json字符串
     * @param map
     * @return
     */
    private String mapToJsonStr(Map<String,String> map) {
        StringBuffer sb = new StringBuffer("{");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append("\"").append(entry.getKey()).append("\"").append(":");
            sb.append("\"").append(entry.getValue()).append("\"");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }

    public void isTargetRole(HttpServletRequest request, HttpServletResponse response) {
        String role = request.getParameter("role");
        HttpSession session = request.getSession();
        UserDetail userDetail = (UserDetail) session.getAttribute(securityConfig.getProperty("security.sessionUserKey"));
        try {
            ServletOutputStream out = response.getOutputStream();
            if (userDetail == null) {
                out.write("false".getBytes());
                return;
            }
            List<String> roleList = userDetail.getRole();
            if (roleList == null) {
                out.write("false".getBytes());
                return;
            }
            if (role != null && roleList.contains(role)) {
                out.write("true".getBytes());
            } else {
                out.write("false".getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getRoles(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        UserDetail userDetail = (UserDetail) session.getAttribute(securityConfig.getProperty("security.sessionUserKey"));

        try {
            ServletOutputStream out = response.getOutputStream();
            if (userDetail == null) {
                out.write("null".getBytes());
                return;
            }
            List<String> roleList = userDetail.getRole();
            if (roleList == null) {
                out.write("null".getBytes());
                return;
            }
            String temp = "";
            for (int i = 0; i < roleList.size(); i++) {
                temp += (","+roleList.get(i));
            }
            temp = temp.substring(1);
            out.write(temp.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修正角色格式
     * @param roleList
     * @return
     */
    private List<String> correctionRole(List<String> roleList) {
        List<String> reList = new ArrayList<String>();
        for (String s : roleList) {
            if (s.startsWith("role_")) {
                reList.add(s);
            } else {
                reList.add("role_" + s);
            }
        }
        return reList;
    }

}
