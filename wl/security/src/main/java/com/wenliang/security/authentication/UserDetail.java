package com.wenliang.security.authentication;

import java.util.List;
import java.util.Map;

/**
 * @author wenliang
 * @date 2019-12-24
 * 简介：
 */
public class UserDetail {

    private String username;
    private String password;
    private List<String> role;
    private Map<String, Object> userInfo;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRole() {
        return role;
    }

    public void setRole(List<String> role) {
        this.role = role;
    }

    public Map<String, Object> getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(Map<String, Object> userInfo) {
        this.userInfo = userInfo;
    }
}
