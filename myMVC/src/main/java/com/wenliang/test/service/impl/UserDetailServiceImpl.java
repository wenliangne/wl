package com.wenliang.test.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wenliang.context.cfg.DefaultBeanApplicationContext;
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
    private UserService userService= (UserService)DefaultBeanApplicationContext.get("userServiceImpl");
    private Map<String,String> roleMap = new HashMap<>();
    public UserDetailServiceImpl() {
        List<Role> roleList = userService.findAllRole();
        for (Role role : roleList) {
            roleMap.put(String.valueOf(role.getId()), role.getRolename());
        }
    }
    @Override
    public UserDetail loadUserByUsername(String username) {
        User user = userService.findByUsername(username);
        UserDetail userDetail = new UserDetail();
        userDetail.setUsername(username);
        userDetail.setPassword(user.getPassword());
        HashMap<String, Object> infoMap = new HashMap<>();
        infoMap.put("username",user.getUsername());
        infoMap.put("gender",user.getGender());
        infoMap.put("birthday",user.getBirthday());
        infoMap.put("email",user.getEmail());
        infoMap.put("status",user.getStatus());
        infoMap.put("id",user.getId());
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
