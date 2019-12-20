package com.wenliang.controller.servlet;

import javax.servlet.annotation.WebServlet;


/**
 * @author wenliang
 * @date 2019-06-26
 * 简介：配置用于处理jsp的servlet
 */
@WebServlet(name = "*.jsp")
public class JspServlet extends org.apache.jasper.servlet.JspServlet {

}
