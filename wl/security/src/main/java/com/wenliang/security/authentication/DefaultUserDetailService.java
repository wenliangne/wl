package com.wenliang.security.authentication;

import java.util.ArrayList;

/**
 * @author wenliang
 * @date 2019-12-25
 * 简介：
 */
public class DefaultUserDetailService implements UserDetailsService {
    public UserDetail loadUserByUsername(String username) {
        UserDetail userDetail = new UserDetail();
        userDetail.setUsername(username);
        if (username.equals("sys")) {
            userDetail.setPassword("36bcbb801f5052739af8220c6ea51434");
            ArrayList<String> roleList = new ArrayList<String>();
            roleList.add("role_sys");
            userDetail.setRole(roleList);
        } else if (username.equals("admin")) {
            userDetail.setPassword("21232f297a57a5a743894a0e4a801fc3");
            ArrayList<String> roleList = new ArrayList<String>();
            roleList.add("role_admin");
            userDetail.setRole(roleList);
        } else if (username.equals("user")) {
            userDetail.setPassword("ee11cbb19052e40b07aac0ca060c23ee");
            ArrayList<String> roleList = new ArrayList<String>();
            roleList.add("role_user");
            userDetail.setRole(roleList);
        }
        return userDetail;
    }
}
