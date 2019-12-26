package com.wenliang.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wenliang.security.authentication.SecurityService;
import com.wenliang.security.authentication.UserDetail;
import com.wenliang.security.macher.MatchUrlHandler;

/**
 * @author wenliang
 * @date 2019-12-24
 * 简介：
 */
public class SecurityFilter implements Filter {

    private SecurityService securityService;
    private Map<String, MatchUrlHandler> matchUrlHandlerMap;

    public void init(FilterConfig filterConfig) throws ServletException {
        SecurityRunner.run();
        securityService = SecurityContext.getDefaultSecurityService();
        matchUrlHandlerMap = SecurityContext.getMatchUrlHandlerMap();
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        SecurityConfig securityConfig = SecurityContext.getSecurityConfig();
        String servletPath = ((HttpServletRequest) servletRequest).getServletPath();
        if (!servletPath.equals(securityConfig.getProperty("security.loginPage"))&&(servletPath.endsWith(".html")||servletPath.endsWith(".htm")||servletPath.endsWith(".jsp"))) {
            request.getSession().setAttribute("storyPageName", servletPath);
        }
        if (authentication(request, response)) {
            return;
        }
        List<MatchUrlHandler> matchUrlHandlerList = new ArrayList<MatchUrlHandler>();
        boolean isLogin = isLogin(request, response);
        if (isLogin) {
            UserDetail userDetail = (UserDetail) request.getSession().getAttribute(securityConfig.getProperty("security.user"));
            List<String> role = userDetail.getRole();
            for (int i = 0; i < role.size(); i++) {
                MatchUrlHandler matchUrlHandler = matchUrlHandlerMap.get(role.get(i));
                if (matchUrlHandler != null) {
                    matchUrlHandlerList.add(matchUrlHandler);
                }
            }
        } else {
            matchUrlHandlerList.add(matchUrlHandlerMap.get("role_noLogin"));
        }
        if (isMatch(matchUrlHandlerList,servletPath)) {
            filterChain.doFilter(request, response);
        } else if (matchUrlHandlerList.size() == 1 && matchUrlHandlerList.get(0).getRole().equals("role_noLogin")) {
            String loginPage = securityConfig.getProperty("security.loginPage");
            if ("".equals(loginPage)) {
                response.getOutputStream().write(PageGenerator.getDefaultLoginPage().getBytes());
            } else {
                response.sendRedirect(request.getContextPath() + loginPage);
            }
        } else {
            String forbidLocationPage = securityConfig.getProperty("security.forbidLocationPage");
            if ("".equals(forbidLocationPage)) {
                response.getOutputStream().write(PageGenerator.getDefaultForbidPage().getBytes());
            } else {
                response.sendRedirect(request.getContextPath() + forbidLocationPage);
            }

        }

    }

    private boolean isLogin(HttpServletRequest request, HttpServletResponse response) {
        Object username = request.getSession().getAttribute("username");
        if (username == null) {
            return false;
        } else {
            return true;
        }
    }

    public void destroy() {
        System.out.println("执行了过滤器的销毁方法！");
    }

    private boolean isMatch(List<MatchUrlHandler> matchUrlHandlerList, String url) {
        for (MatchUrlHandler matchUrlHandler : matchUrlHandlerList) {
            if (matchUrlHandler.handle(url)) {
                return true;
            }
        }
        return false;
    }

    private boolean authentication(HttpServletRequest request, HttpServletResponse response) {
        SecurityConfig securityConfig = SecurityContext.getSecurityConfig();
        String servletPath = request.getServletPath();
        if (servletPath.equals(securityConfig.getProperty("security.logout"))) {
            securityService.logout(request, response);
            return true;
        } else if (servletPath.equals(securityConfig.getProperty("security.login"))) {
            securityService.login(request, response);
            return true;
        } else if (servletPath.equals(securityConfig.getProperty("security.isLogin"))) {
            securityService.isLogin(request, response);
            return true;
        } else if (servletPath.equals(securityConfig.getProperty("security.getUsername"))){
            securityService.getUsername(request, response);
            return true;
        } else if (servletPath.equals(securityConfig.getProperty("security.isTargetRole"))){
            securityService.isTargetRole(request, response);
            return true;
        } else if (servletPath.equals(securityConfig.getProperty("security.getRoles"))){
            securityService.getRoles(request, response);
            return true;
        }
        return false;
    }
}
