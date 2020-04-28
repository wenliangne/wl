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

import com.wenliang.core.log.Log;
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
    private SecurityConfig securityConfig;

    public void init(FilterConfig filterConfig) throws ServletException {
        SecurityRunner.run();
        securityService = SecurityContext.getSecurityService();
        matchUrlHandlerMap = SecurityContext.getMatchUrlHandlerMap();
        securityConfig = SecurityContext.getSecurityConfig();
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String servletPath = ((HttpServletRequest) servletRequest).getServletPath();
        request.setCharacterEncoding(securityConfig.getProperty("security.characterEncoding"));
        response.setContentType(securityConfig.getProperty("security.contentType"));
        //将最后访问的页面存入session
        doCache(request,servletPath);
        //处理认证相关的页面
        if (authentication(request, response)) {
            return;
        }
        //获取url处理器执行器
        List<MatchUrlHandler> matchUrlHandlerList = geneMatchUrlHandler(request);
        //权限验证及路径分发
        doDispatcher(matchUrlHandlerList,servletPath,request,response,filterChain);

    }

    /**
     * 根据url处理器处理结果进行授权及路径分发
     */
    private void doDispatcher(List<MatchUrlHandler> matchUrlHandlerList,String servletPath,HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
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

    /**
     * 根据当前用户角色生成url匹配处理器
     * @param request
     * @return
     */
    private List<MatchUrlHandler> geneMatchUrlHandler(HttpServletRequest request) {
        List<MatchUrlHandler> matchUrlHandlerList = new ArrayList<MatchUrlHandler>();
        boolean isLogin = isLogin(request);
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
        return matchUrlHandlerList;
    }
    /**
     * 判断是否登录
     * @param request
     * @return
     */
    private boolean isLogin(HttpServletRequest request) {
        Object username = request.getSession().getAttribute("username");
        if (username == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 将最后一次的访问页面存入session
     * @param request
     * @param servletPath
     */
    private void doCache(HttpServletRequest request,String servletPath) {
        if (!servletPath.equals(securityConfig.getProperty("security.loginPage"))&&(servletPath.endsWith(".html")||servletPath.endsWith(".htm")||servletPath.endsWith(".jsp"))) {
            request.getSession().setAttribute("storyPageName", servletPath);
        }
    }


    /**
     * 匹配url
     * @param matchUrlHandlerList
     * @param url
     * @return
     */
    private boolean isMatch(List<MatchUrlHandler> matchUrlHandlerList, String url) {
        if (url.equals(securityConfig.getProperty("security.forbidLocationPage"))
                ||url.equals(securityConfig.getProperty("security.loginPage"))
                ||url.equals(securityConfig.getProperty("security.loginSuccess"))
                ||url.equals(securityConfig.getProperty("security.logoutSuccess"))) {
            return true;
        }
        for (MatchUrlHandler matchUrlHandler : matchUrlHandlerList) {
            if (matchUrlHandler.handle(url)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 处理与认证相关的url请求
     * @param request
     * @param response
     * @return
     */
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

    public void destroy() {
        Log.INFO("执行了过滤器的销毁方法："+this.getClass().getName());
    }
}
