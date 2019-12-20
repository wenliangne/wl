package com.wenliang.test.controller;



import com.wenliang.context.annotations.Autowired;
import com.wenliang.controller.annotation.Controller;
import com.wenliang.controller.annotation.RequestMapping;
import com.wenliang.test.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/test2")
public class TestController2 {

    @Autowired
    private UserService userService;

    @RequestMapping("/testAaa2")
    public void testAaa2(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("testAaa2方法被访问到了！");
    }
    @RequestMapping("/testBbb2")
    public void testBbb2(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("testBbb2方法被访问到了！");
    }
}
