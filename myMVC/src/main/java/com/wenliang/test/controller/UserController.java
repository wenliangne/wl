package com.wenliang.test.controller;

import com.wenliang.context.annotations.Autowired;
import com.wenliang.context.annotations.Resource;
import com.wenliang.context.annotations.Value;
import com.wenliang.controller.annotation.Controller;
import com.wenliang.controller.annotation.RequestMapping;
import com.wenliang.controller.annotation.ResponseBody;
import com.wenliang.mapper.plugins.page.PageInfo;
import com.wenliang.test.domain.User;
import com.wenliang.test.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.util.List;


/**
 * @author wenliang
 * @date 2019-07-16
 * 简介：
 */

@Controller
@RequestMapping("/user")
public class UserController {


    @Value("woria")
    private String aaa;
    @Value("666")
    private int bbb;
    @Value("777")
    private Integer ccc;
    @Value("888")
    private double ddd;
    @Value("#{name}")
    private String name;
    @Autowired
    private UserService userService;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private HttpServletRequest request;
    private HttpServletRequest request2;


    @RequestMapping("/findAll")
    public @ResponseBody
    List<User> findAll() {
        System.out.println(request.getServerPort());
        return userService.findAll();
    }

    @RequestMapping("/findPage")
    public @ResponseBody
    PageInfo findPage(Integer pageNum, Integer pageSize) {
        return userService.findPage(pageNum, pageSize);
    }

    @RequestMapping("/findByUsernameAndPassword2")
    public @ResponseBody
    User findByUsernameAndPassword2(String username, String password) {
        System.out.println("aaa:"+aaa);
        System.out.println("bbb:"+bbb);
        User user = userService.findByUsernameAndPassword2(username, password);
        System.out.println("ccc:"+ccc);
        System.out.println("ddd:"+ddd);
        System.out.println("name:"+name);
        System.out.println("request:" + request);
        System.out.println("request2:"+request2);
        return user;
    }

    @RequestMapping(("/AspectThrowing"))
    public  String  AspectThrowing() {
        String s = userService.AspectThrowing();
        return s;
    }

    @RequestMapping(("/trans"))
    public  String  trans() {
        userService.trans();
        return null;
    }
    @RequestMapping(("/transError"))
    public  String  transError() {
        userService.transError();
        return null;
    }
}
