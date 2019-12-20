package com.wenliang.controller.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wenliang.controller.annotation.ResponseBody;
import com.wenliang.controller.group.Model;
import com.wenliang.controller.group.ModelAndView;
import com.wenliang.controller.handler.interfacces.Handler;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author wenliang
 * @date 2019-08-16
 * 简介：
 */
public class ResultSetHandler implements Handler {
    /**
     * 处理结果集
     * @param rs
     */
    public String processingResultSet(Object rs,Object[] args,Method method,HttpServletRequest request) {
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                continue;
            }else if (Map.class.isAssignableFrom(args[i].getClass())) {
                Map<String, Object> map = (Map<String, Object>) args[i];
                for (String key : map.keySet()) {
                    request.setAttribute(key, map.get(key));
                }
            } else if (ModelAndView.class.isAssignableFrom(args[i].getClass())) {
                ModelAndView modelAndView = (ModelAndView) args[i];
                for (String key : modelAndView.keySet()) {
                    request.setAttribute(key, modelAndView.get(key));
                }
                return modelAndView.getPath();
            } else if (Model.class.isAssignableFrom(args[i].getClass())) {
                Model model = (Model) args[i];
                for (String key : model.keySet()) {
                    request.setAttribute(key, model.get(key));
                }
            }
        }
        if (rs == null) {
            return "null";
        }
        if (method.getAnnotation(ResponseBody.class) != null) {
            ObjectMapper om = new ObjectMapper();
            try {
                String str = om.writeValueAsString(rs);
                return str;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return rs.toString();
        }
        return "null";
    }

}
