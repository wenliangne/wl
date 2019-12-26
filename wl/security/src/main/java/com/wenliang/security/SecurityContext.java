package com.wenliang.security;

import java.util.HashMap;
import java.util.Map;

import com.wenliang.security.authentication.DefaultSecurityService;
import com.wenliang.security.macher.MatchUrlHandler;

/**
 * @author wenliang
 * @date 2019-12-24
 * 简介：
 */
public class SecurityContext {
    private static SecurityConfig securityConfig=new SecurityConfig();
    private static Map<String,MatchUrlHandler> matchUrlHandlerMap = new HashMap();
    private static DefaultSecurityService defaultSecurityService = new DefaultSecurityService();


    public static SecurityConfig getSecurityConfig() {
        return securityConfig;
    }

    public static void setSecurityConfig(SecurityConfig securityConfig) {
        SecurityContext.securityConfig = securityConfig;
    }

    public static Map<String, MatchUrlHandler> getMatchUrlHandlerMap() {
        return matchUrlHandlerMap;
    }

    public static void setMatchUrlHandlerMap(Map<String, MatchUrlHandler> matchUrlHandlerMap) {
        SecurityContext.matchUrlHandlerMap = matchUrlHandlerMap;
    }

    public static DefaultSecurityService getDefaultSecurityService() {
        return defaultSecurityService;
    }

    public static void setDefaultSecurityService(DefaultSecurityService defaultSecurityService) {
        SecurityContext.defaultSecurityService = defaultSecurityService;
    }
}
