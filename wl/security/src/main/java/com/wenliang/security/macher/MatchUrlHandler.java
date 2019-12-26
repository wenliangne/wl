package com.wenliang.security.macher;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wenliang
 * @date 2019-12-25
 * 简介：
 */
public class MatchUrlHandler {

    private String role;
    private List<UrlMatcher> acceptUrlList = new ArrayList<UrlMatcher>();//开启放行器时的url地址匹配器


    public boolean handle(String url) {
        for (UrlMatcher urlMatcher : acceptUrlList) {
            if (urlMatcher.matcher(url)) {
                return true;
            }
        }
        return false;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<UrlMatcher> getAcceptUrlList() {
        return acceptUrlList;
    }

    public void setAcceptUrlList(List<UrlMatcher> acceptUrlList) {
        this.acceptUrlList = acceptUrlList;
    }
}
