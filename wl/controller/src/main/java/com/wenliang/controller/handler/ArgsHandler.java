package com.wenliang.controller.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wenliang.controller.annotation.RequestBody;
import com.wenliang.controller.group.Model;
import com.wenliang.controller.group.ModelAndView;
import com.wenliang.controller.group.MultipartFile;
import com.wenliang.controller.handler.interfacces.Handler;
import com.wenliang.core.util.MethodUtils;
import org.apache.catalina.core.ApplicationPart;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author wenliang
 * @date 2019-08-16
 * 简介：
 */
public class ArgsHandler implements Handler {

    /**
     * 获取方法参数
     * @param method
     * @param request
     * @param response
     * @return
     */
    public Object[] getArgs(Method method,HttpServletRequest request,HttpServletResponse response) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        String[] parameterNames = MethodUtils.getParameterNames(method);
        ArrayList<Object> args = new ArrayList<>();
        for (int i = 0; i < parameterTypes.length; i++) {
            try {
                Class<?> parameterType = parameterTypes[i];
                String parameterName = parameterNames[i];
                if (ServletRequest.class.isAssignableFrom(parameterType)) {
                    args.add(request);
                } else if (ServletResponse.class.isAssignableFrom(parameterType)) {
                    args.add(response);
                } else if (MultipartFile.class.isAssignableFrom(parameterType)) {
                    MultipartFile multipartFile = getMultipartFile(request,parameterName);
                    args.add(multipartFile);
                } else if (Map.class.isAssignableFrom(parameterType)) {
                    args.add(formatMap(request.getParameterMap()));
                } else if (ModelAndView.class.isAssignableFrom(parameterType)) {
                    args.add(new ModelAndView(formatMap(request.getParameterMap())));
                } else if (Model.class.isAssignableFrom(parameterType)) {
                    args.add(new Model(formatMap(request.getParameterMap())));
                } else if (List.class.isAssignableFrom(parameterType)) {
                    args.add(Arrays.asList(request.getParameter(parameterName)));
                } else if (isComplexObject(parameterType)) {
                    if (!isContainsAnnotation(method,i,RequestBody.class)) {
                        Object complexObject = parameterType.newInstance();
                        copyProperties(request, complexObject,method,i);
                        args.add(complexObject);
                    } else {
                        String json = getStringBody(request);
                        ObjectMapper om = new ObjectMapper();
                        try {
                            Object complexObject = om.readValue(json, parameterType);
                            args.add(complexObject);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    String parameter = request.getParameter(parameterName);
                    if (String.class.isAssignableFrom(parameterType)) {
                        args.add(parameter);
                        continue;
                    }
                    if (parameter == null) {
                        parameter = "0";
                    }
                    if (int.class.isAssignableFrom(parameterTypes[i]) || Integer.class.isAssignableFrom(parameterTypes[i])) {
                        args.add(Integer.parseInt(parameter));
                    } else if (double.class.isAssignableFrom(parameterTypes[i]) || Double.class.isAssignableFrom(parameterTypes[i])) {
                        args.add(Double.parseDouble(parameter));
                    } else if (float.class.isAssignableFrom(parameterTypes[i]) || Float.class.isAssignableFrom(parameterTypes[i])) {
                        args.add(Float.parseFloat(parameter));
                    } else if (byte.class.isAssignableFrom(parameterTypes[i]) || Byte.class.isAssignableFrom(parameterTypes[i])) {
                        args.add(Byte.parseByte(parameter));
                    } else if (long.class.isAssignableFrom(parameterTypes[i]) || Long.class.isAssignableFrom(parameterTypes[i])) {
                        args.add(Long.parseLong(parameter));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                args.add(null);
            }
        }
        return args.toArray();
    }

    /**
     * 将request中的属性复制到对象中
     * @param request
     * @param complexObject
     */
    private void copyProperties(HttpServletRequest request,Object complexObject,Method method,int index) {
        Class<?> aClass = complexObject.getClass();
        Method setMethod,getMethod;
        Field field;
        if (!isContainsAnnotation(method,index,RequestBody.class)) {
            Map<String, String[]> map = request.getParameterMap();
            for (Object key : map.keySet()) {
                try {
                    field = aClass.getDeclaredField(key.toString());
                    setMethod = aClass.getDeclaredMethod(getSetMethodByKey(key.toString()), field.getType());
                    if (String.class.isAssignableFrom(field.getType())) {
                        setMethod.invoke(complexObject, (map.get(key))[0]);
                    } else if (byte.class.isAssignableFrom(field.getType()) || Byte.class.isAssignableFrom(field.getType())) {
                        setMethod.invoke(complexObject, Byte.parseByte((map.get(key))[0]));
                    } else if (int.class.isAssignableFrom(field.getType()) || Integer.class.isAssignableFrom(field.getType())) {
                        setMethod.invoke(complexObject, Integer.parseInt((map.get(key))[0]));
                    } else if (long.class.isAssignableFrom(field.getType()) || Long.class.isAssignableFrom(field.getType())) {
                        setMethod.invoke(complexObject, Long.parseLong((map.get(key))[0]));
                    } else if (float.class.isAssignableFrom(field.getType()) || Float.class.isAssignableFrom(field.getType())) {
                        setMethod.invoke(complexObject, Float.parseFloat((map.get(key))[0]));
                    } else if (double.class.isAssignableFrom(field.getType()) || Double.class.isAssignableFrom(field.getType())) {
                        setMethod.invoke(complexObject, Double.parseDouble((map.get(key))[0]));
                    } else if (isComplexObject(field.getType())) {
                        Object tempObject = field.getType().newInstance();
                        copyProperties(request, tempObject, method, index);
                    } else if (MultipartFile.class.isAssignableFrom(field.getType())) {
                        MultipartFile multipartFile = getMultipartFile(request, field.getName());
                        setMethod.invoke(complexObject, multipartFile);
                    } else if (List.class.isAssignableFrom(field.getType())) {
                        setMethod.invoke(complexObject, Arrays.asList(map.get(key)));
                    }
                } catch (Exception e) {
                }
            }
        } else {
            String json = getStringBody(request);
            ObjectMapper om = new ObjectMapper();
            try {
                Object tempComplexObject = om.readValue(json, aClass);
                Field[] declaredFields = tempComplexObject.getClass().getDeclaredFields();
                for (int i = 0; i < declaredFields.length; i++) {
                    field = declaredFields[i];
                    String fieldName =field.getName();
                    setMethod = aClass.getDeclaredMethod(getSetMethodByKey(fieldName), field.getType());
                    getMethod=aClass.getDeclaredMethod(getGetMethodByKey(fieldName));
                    setMethod.invoke(complexObject, getMethod.invoke(tempComplexObject));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 根据参数名获取MultipartFile对象
     * @param request
     * @param fileName
     * @return
     */
    private MultipartFile getMultipartFile(HttpServletRequest request,String fileName) {
        MultipartFile multipartFile = null;
        try {
            Part p = request.getPart(fileName);
            ApplicationPart ap = (ApplicationPart) p;
            InputStream is = ap.getInputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = is.read(buffer)) != -1) {
                if (multipartFile != null) {
                    multipartFile.addBytes(buffer, len);
                } else {
                    byte[] temp = new byte[len];
                    System.arraycopy(buffer, 0, temp, 0, len);
                    multipartFile  = new MultipartFile(ap.getSubmittedFileName(), ap.getContentType(),temp );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return multipartFile;
    }

    /**
     * 取原map中数组的第一个值生成新的map
     * @param map
     * @return
     */
    private Map formatMap(Map<String,String[]> map) {
        Map<String, String> tempMap = new HashMap<>();
        for (Object key : map.keySet()) {
            tempMap.put(key.toString(), map.get(key.toString())[0]);
        }
        return tempMap;
    }

    /**
     * 作为字符串获取body
     * @param request
     * @return
     */
    private String getStringBody(HttpServletRequest request) {
        StringBuffer sb = new StringBuffer();
        String tempSt = "";
        try {
            BufferedReader reader = request.getReader();
            do {
                sb.append(tempSt);
                tempSt = reader.readLine();
            } while (tempSt!=null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 通过属性名获取get方法名
     * @param key
     * @return
     */
    private String getGetMethodByKey(String key) {
        char[] ch = key.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new StringBuilder("get").append(new String(ch)).toString();
    }

    /**
     * 通过属性名获取set方法名
     * @param key
     * @return
     */
    private String getSetMethodByKey(String key) {
        char[] ch = key.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new StringBuilder("set").append(new String(ch)).toString();
    }

    /**
     * 判断方法的第index个参数是否包含某注解
     * @param method
     * @param index
     * @param annotation
     * @return
     */
    private boolean isContainsAnnotation(Method method,int index,Class<?> annotation) {
        Annotation[] parameterAnnotations = method.getParameterAnnotations()[index];
        for (int j = 0; j < parameterAnnotations.length; j++) {
            if (annotation.isAssignableFrom(parameterAnnotations[j].getClass())) {
                return true;
            }
        }
        return false;
    }
    /**
     * 判断是否为复杂对象
     * @param parameterType
     * @return
     */
    private boolean isComplexObject(Class<?> parameterType) {
        if (byte.class.isAssignableFrom(parameterType) || Byte.class.isAssignableFrom(parameterType)
                ||int.class.isAssignableFrom(parameterType) || Integer.class.isAssignableFrom(parameterType)
                ||float.class.isAssignableFrom(parameterType) || Float.class.isAssignableFrom(parameterType)
                ||double.class.isAssignableFrom(parameterType) || Double.class.isAssignableFrom(parameterType)
                ||long.class.isAssignableFrom(parameterType) || Long.class.isAssignableFrom(parameterType)
                ||String.class.isAssignableFrom(parameterType)
                ||ServletRequest.class.isAssignableFrom(parameterType)||ServletResponse.class.isAssignableFrom(parameterType)
                ||Map.class.isAssignableFrom(parameterType)||List.class.isAssignableFrom(parameterType)
                ||MultipartFile.class.isAssignableFrom(parameterType)
                ||ModelAndView.class.isAssignableFrom(parameterType)||Model.class.isAssignableFrom(parameterType)
                ) {
            return false;
        } else {
            return true;
        }
    }

}

