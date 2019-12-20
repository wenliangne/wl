package com.wenliang.test.controller;


import com.wenliang.context.annotations.Resource;
import com.wenliang.controller.annotation.Controller;
import com.wenliang.controller.annotation.RequestBody;
import com.wenliang.controller.annotation.RequestMapping;
import com.wenliang.controller.annotation.ResponseBody;
import com.wenliang.controller.cfg.defaults.DefaultControllerApplicationContext;
import com.wenliang.controller.group.MultipartFile;
import com.wenliang.test.domain.User;
import com.wenliang.test.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping("/test1")
public class TestController {

    @Resource("userServiceImpl")
    private UserService userService;

    @RequestMapping("/testAaa")
    public void testAaa(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(DefaultControllerApplicationContext.get("/test1/testAaa"));
        System.out.println("testAaa方法被访问到了！");
    }

    @RequestMapping("/testBbb")
    public void testBbb(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("testBbb方法被访问到了！");
        throw new RuntimeException("这是我故意抛出的一个异常！");
    }
    @RequestMapping("/testCcc")
    public @ResponseBody
    User testCcc(User user) {
        System.out.println("testCcc方法被访问到了！");
        System.out.println(user);
        return user;
    }

    @RequestMapping("/testUpload")
    public void testUpload(MultipartFile upload) {
        System.out.println(upload.getName());
        upload.transfer("C:\\Users\\wenliang\\Desktop\\testUpload-"+upload.getName());
        System.out.println("testUpload方法被访问到了！");
    }

    @RequestMapping("/testUpload2")
    public void testUpload2(MultipartFile picture) {
        System.out.println(picture.getName());
        picture.transfer("C:\\Users\\wenliang\\Desktop\\testUpload2-"+picture.getName());
        System.out.println("testUpload2方法被访问到了！");
    }
    @RequestMapping("/testForward404")
    public String testForward404() {
        System.out.println("testForward404");
        return"forward:/asd.html";
    }
    @RequestMapping("/testForward")
    public String testForward() {
        System.out.println("testForward");
        return"forward:/bbb.html";
    }
    @RequestMapping("/testRedirect")
    public String testRedirect() {
        System.out.println("testRedirect");
        return "redirect:/bbb.html";
    }
    @RequestMapping("/testResponseJson")
    public @ResponseBody User testResponseJson() {
        System.out.println("testResponseJson");
        User user = new User();
        user.setId(123);
        user.setUsername("wenwen");
        user.setPassword("问你呢");
        user.setBirthday(new Date());
        return user;
    }
    @RequestMapping("/testRequestJson")
    public @ResponseBody User testRequestJson(@RequestBody User user) {
        System.out.println("testRequestJson");
        System.out.println(user);
        return user;
    }

    @RequestMapping("/testMap")
    public void testMap(Map map) {
        Object aa = map.get("name");
        System.out.println(aa);
    }
    @RequestMapping("/testMapSetValue")
    public String testMapSetValue(Map map) {
        System.out.println("testMapSetValue");
        User user = new User();
        user.setId(12);
        user.setUsername("wenwen");
        user.setPassword("问你呢");
        user.setBirthday(new Date());
        map.put("user", user);
        map.put("username", "wenwenusername");
        return "forward:/aaa.jsp";
    }


}
