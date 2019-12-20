package com.wenliang.controller.handler;

import com.wenliang.controller.handler.interfacces.Handler;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wenliang
 * @date 2019-08-16
 * 简介：
 */
public class UrlHandler implements Handler {
    public String getServletPath(HttpServletRequest request) {
        return request.getServletPath();
    }
    public String getContextPath(HttpServletRequest request) {
        return request.getSession().getServletContext().getRealPath("");
    }
}
