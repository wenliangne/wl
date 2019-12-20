package com.wenliang.test.servlet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;


import com.wenliang.quickstart.tomcat.Servlet;

import org.apache.catalina.core.ApplicationPart;

/**
 * @author wenliang
 * @date 2019-12-12
 * 简介：
 */
@Servlet("/html")
public class HtmlTomcatServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("html");
        Part picture = req.getPart("picture");
        FileOutputStream fos = new FileOutputStream("C:\\Users\\wenliang\\Desktop\\" + ((ApplicationPart) picture).getFilename());
        InputStream is = picture.getInputStream();
        int len = 0;
        byte[] buffer = new byte[1024 * 10];
        try {
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer,0,len);
            }
            picture.getInputStream();
        } catch (Exception e) {

        }finally {
            fos.close();
            is.close();
        }
        req.getRequestDispatcher(req.getContextPath()+"/pages/index.html").forward(req, resp);
    }
}
