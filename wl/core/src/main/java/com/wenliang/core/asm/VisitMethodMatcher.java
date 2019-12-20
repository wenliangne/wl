package com.wenliang.core.asm;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wenliang
 * @date 2019-08-08
 * 简介：
 */
public class VisitMethodMatcher {
    private String parameterMatcherString;
    private String methodName;
    private List<String> parameterNames=new ArrayList<String>();

    public VisitMethodMatcher(String methodName,String parameterMatcherString) {
        this.methodName = methodName;
        this.parameterMatcherString = parameterMatcherString;
        parseMatcherString();
    }
    private void parseMatcherString() {
        int start = parameterMatcherString.indexOf("(");
        int end = parameterMatcherString.indexOf(")");
        String tempMatcherString = parameterMatcherString.substring(start + 1, end);
        String[] split = tempMatcherString.split(";");
        String tempPa = null;
        for (int i = 0; i < split.length; i++) {
            if (!split[i].equals("")) {
                tempPa = split[i].substring(1).replaceAll("/", ".");
                parameterNames.add(tempPa);
            }
        }
    }

    public boolean isMatchMethod(Method method) {
        if (isMatchMethodName(method.getName()) && isMatchMethodParameters(Arrays.toString(method.getParameterTypes()))) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isMatchMethodName(String methodName) {
        if (this.methodName.equals(methodName)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isMatchMethodParameters(String parametersArrString) {
        parametersArrString = parametersArrString.replaceAll("class ", "");
        if (Arrays.toString(parameterNames.toArray()).equals(parametersArrString)) {
            return true;
        } else {
            return false;
        }
    }



}
