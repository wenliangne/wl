package com.wenliang.controller.handler;

import com.wenliang.controller.handler.interfacces.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * @author wenliang
 * @date 2019-08-16
 * 简介：
 */
public class CharacterEncodingHandler implements Handler {

    public void setCharacterEncoding(HttpServletRequest request, HttpServletResponse response, String characterEncoding) throws UnsupportedEncodingException {
        if (request != null) {
            request.setCharacterEncoding(characterEncoding);
        }
        if (response != null) {
            response.setCharacterEncoding(characterEncoding);
        }
    }

    public void setContentType(HttpServletResponse response, String contentType) {
        if (response != null) {
            response.setContentType(contentType);
        }
    }

}
