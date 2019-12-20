package com.wenliang.controller.servlet;

import com.wenliang.controller.cfg.defaults.DefaultControllerApplicationContext;
import com.wenliang.controller.group.ExecutorBean;
import com.wenliang.controller.handler.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;


/**
 * @author wenliang
 * @date 2019-06-15
 * 简介：
 */
public class DispatcherServlet extends HttpServlet {
    private CharacterEncodingHandler characterEncodingHandler = new CharacterEncodingHandler();
    private UrlHandler urlHandler = new UrlHandler();
    private ArgsHandler argsHandler = new ArgsHandler();
    private ResultSetHandler resultSetHandler = new ResultSetHandler();
    private ViewHandler viewHandler = new ViewHandler();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 设置字符编码
        characterEncodingHandler.setCharacterEncoding(request, null, "UTF-8");
        characterEncodingHandler.setContentType(response, "text/html;charset=UTF-8");

        // 获取项目路径和请求路径
        String servletPath = urlHandler.getServletPath(request);
        String contextPath = urlHandler.getContextPath(request);
        try {
            // 获取执行器
            ExecutorBean executorBean = (ExecutorBean) DefaultControllerApplicationContext.get(servletPath);
            if (executorBean != null) {
                // 获取执行器中的方法
                Method method = executorBean.getMethod();
                // 获取方法的参数
                Object[] args = argsHandler.getArgs(method, request, response);
                // 执行方法返回结果集
                Object rs = method.invoke(executorBean.getObject(), args);
                // 处理结果集
                servletPath = resultSetHandler.processingResultSet(rs,args,method,request);
            }
            // 进行路由分发
            viewHandler.doDispatcher(servletPath, contextPath,request, response);
        } catch (Exception e) {
            e.printStackTrace();
            viewHandler.setResponsePage(500, response, e.getMessage());
            return;
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.doPost(request,response);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
























}
