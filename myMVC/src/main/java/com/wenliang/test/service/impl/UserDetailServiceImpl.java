package com.wenliang.test.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wenliang.context.cfg.DefaultBeanApplicationContext;
import com.wenliang.core.util.EntityUtils;
import com.wenliang.security.authentication.UserDetail;
import com.wenliang.security.authentication.UserDetailsService;
import com.wenliang.test.domain.Role;
import com.wenliang.test.domain.User;
import com.wenliang.test.domain.UserRole;
import com.wenliang.test.service.UserService;

/**
 * @author wenliang
 * @date 2019-12-26
 * 简介：
 */
public class UserDetailServiceImpl  implements UserDetailsService{
    private UserService userService;
    private Map<String, String> roleMap;
    public UserDetailServiceImpl() {

    }
    @Override
    public UserDetail loadUserByUsername(String username) {
        if (roleMap == null) {
            roleMap = new HashMap<>();
            userService = (UserService) DefaultBeanApplicationContext.get("userServiceImpl");
            List<Role> roleList = userService.findAllRole();
            for (Role role : roleList) {
                roleMap.put(String.valueOf(role.getId()), role.getRolename());
            }
        }
        User user = userService.findByUsername(username);
        UserDetail userDetail = new UserDetail();
        userDetail.setUsername(username);
        userDetail.setPassword(user.getPassword());
        Map<String, Object> infoMap = EntityUtils.entityToMap(user);
        userDetail.setUserInfo(infoMap);
        List<UserRole> userRoleList = userService.findByUserId(user.getId());
        ArrayList<String> roleStringList = new ArrayList<>();
        for (int i = 0; i < userRoleList.size(); i++) {
            roleStringList.add("role_"+roleMap.get(String.valueOf(userRoleList.get(i).getRoleId())));
        }
        userDetail.setRole(roleStringList);
        return userDetail;
    }
}
