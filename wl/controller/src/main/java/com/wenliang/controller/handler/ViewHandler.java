package com.wenliang.controller.handler;

import com.wenliang.controller.handler.interfacces.Handler;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author wenliang
 * @date 2019-08-16
 * 简介：
 */
public class ViewHandler implements Handler {

    /**
     * 进行路由分发
     * @param servletPath
     * @param contextPath
     * @param request
     * @param response
     */
    public void doDispatcher(String servletPath,String contextPath,HttpServletRequest request, HttpServletResponse response) {
        try {
            if (servletPath.toLowerCase().startsWith("redirect:")) {
                response.sendRedirect(request.getContextPath() + servletPath.substring(9));
            } else if (servletPath.toLowerCase().startsWith("forward:")) {
                request.getRequestDispatcher(servletPath.substring(8)).forward(request, response);
            } else {
                boolean re = setStaticResources(servletPath, contextPath, response);
                if (!re) {
                    setResponsePage(404, response, servletPath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据url设置返回的静态
     * @param servletPath
     * @param contextPath
     * @param response
     * @return
     * @throws Exception
     */
    private boolean setStaticResources(String servletPath,String contextPath,HttpServletResponse response) throws Exception{
        ServletOutputStream outputStream = response.getOutputStream();
        if (servletPath.startsWith("[")||servletPath.startsWith("{")||"null".equals(servletPath)) {
            response.setContentType("application/json; charset=utf-8");
            outputStream.write(servletPath.getBytes("utf-8"));
            outputStream.close();
        }
        if ("/".equals(servletPath)) {
            servletPath = "/index.html";
        }
        try {
            File file = new File((contextPath + servletPath).replaceAll("\\\\", "/"));
            if (!file.exists()) {
                return false;
            }
            FileInputStream in = new FileInputStream(file);
            byte[] buffer = new byte[1024*10];
            int len = -1;
            while ((len = in.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            in.close();
            outputStream.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 设置错误响应页面
     *
     * @param status
     * @param response
     * @param message
     * @throws IOException
     */
    public void setResponsePage(int status, HttpServletResponse response, String message) throws IOException {
        response.setStatus(status);
        ServletOutputStream outputStream = response.getOutputStream();

        outputStream.write(("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body>").getBytes("utf-8"));
        if (status == 500) {
            outputStream.write(("<center><h1>500：Server internal exception!</h1></center><br><hr><br>" + "message:" + message).getBytes("utf-8"));
        } else if (status == 404) {
            outputStream.write(("<center><h1>404：Resources not found!</h1></center><br><hr><br>" + "servletPath:" + message).getBytes("utf-8"));
        } else if (status == 200) {
            outputStream.write(("<center><h1>200:Success!</h1></center><br><hr><br>" + "message:" + message).getBytes("utf-8"));

        }
        outputStream.write(("</body>\n" +
                "</html>").getBytes("utf-8"));
        outputStream.close();
    }


}
