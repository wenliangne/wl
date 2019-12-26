package com.wenliang.security.macher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wenliang.security.SecurityConfig;
import com.wenliang.security.SecurityContext;

/**
 * @author wenliang
 * @date 2019-12-24
 * 简介：
 */
public class UrlMatcherFactory {

    public static void initUrlMatcher() {
        SecurityConfig securityConfig = SecurityContext.getSecurityConfig();
        Map<String, MatchUrlHandler> matchUrlHandlerMap = SecurityContext.getMatchUrlHandlerMap();
        for (Object key : securityConfig.getProperties().keySet()) {
            String keyStr = key.toString();
            if (keyStr.startsWith("security.accept.")) {
                List<UrlMatcher> acceptList = create(keyStr);
                MatchUrlHandler matchUrlHandler = new MatchUrlHandler();
                matchUrlHandler.setRole(keyStr.substring(16));
                matchUrlHandler.setAcceptUrlList(acceptList);
                matchUrlHandlerMap.put(keyStr.substring(16), matchUrlHandler);
            }
        }
    }
    public static List<UrlMatcher> create(String type) {
        List<UrlMatcher> matcherList = new ArrayList();
        String urlStr = SecurityContext.getSecurityConfig().getProperty(type);
        String[] urlArr = urlStr.split(",");
        int len = urlArr.length;
        for (int i = 0; i < len; i++) {
            UrlMatcher urlMatcher;
            if (urlArr[i].indexOf("*") > -1) {
                urlMatcher = new RegexUrlMatcher(urlArr[i]);
            } else {
                urlMatcher = new FixedUrlMatcher(urlArr[i]);
            }
            matcherList.add(urlMatcher);
        }
        return matcherList;
    }
}
