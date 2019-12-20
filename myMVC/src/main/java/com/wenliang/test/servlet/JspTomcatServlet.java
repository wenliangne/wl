package com.wenliang.test.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wenliang.quickstart.tomcat.Servlet;
import com.wenliang.test.domain.User;


/**
 * @author wenliang
 * @date 2019-12-13
 * 简介：
 */
@Servlet("/jsp")
public class JspTomcatServlet extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("jsp");
        User user = new User();
        user.setId(1);
        user.setUsername("www");
        user.setEmail("www啊");
        req.getSession().setAttribute("user", user);
        req.getRequestDispatcher(req.getContextPath()+"/pages/index.jsp").forward(req, resp);
    }
}
