package com.wenliang.security.authentication;

/**
 * @author wenliang
 * @date 2019-12-25
 * 简介：
 */
public interface UserDetailsService {
    public UserDetail loadUserByUsername(String username);
}
