package com.wenliang.context.proxy;

import com.wenliang.core.log.Log;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author wenliang
 * @date 2019-07-31
 * 简介：一个用于通过表达式匹配方法的类
 * 表达式写法：权限修饰符+空格+类名+.+方法名+方法参数
 * 例子：
 *      1. "* com.*.find*(*)"  不限定权限修饰符，不限定参数，com包下所有类中以find开头的方法。
 *      2. "public com..find*([class java.lang.String])" 限定权限修饰符为public，参数只有一个并且为String，com包及子包下所有类的以find开头的方法
 * 解释：
 *      1. 权限修饰符：如public，可用*匹配所以普权限类型
 *      2. 类名：全限定类名，以点号分级，每级可用*代表所有，点号后为空代表下面的多级
 *      3. 方法名：可使用*表示所有方法，使用find*代表以find开头的所有方法，使用*All代表以All结尾的所有方法
 *      4. 方法参数：使用()包裹，为空表示无参数，可使用*表示所有参数，如果有必要，将Arrays.toString(method.getParameterTypes())作为参数
 */
public class AspectMatcher {

    private String permissionsString;
    private String classNameString;
    private String methodString;
    private String parameterTypesString;

    public AspectMatcher(String matcher) {
        try {
            parseMatcher(matcher);
        } catch (Exception e) {
            Log.ERROR("解析表达式失败："+matcher);
        }
    }

    public String getMatchString() {
        return permissionsString + " " + classNameString + "." + methodString + "(" + parameterTypesString + ")";
    }

    public AspectMatcher(Class<?> aClass,String matcher) {
        try {
            this.permissionsString = "*";
            this.classNameString = aClass.getName();
            this.methodString = matcher.replace("function(","").replace(")","");
            this.parameterTypesString = "*";
        } catch (Exception e) {
            Log.ERROR("解析表达式失败："+matcher);
        }
    }

    public AspectMatcher(Class<?> aClass,Method method) {
        try {
            this.permissionsString = "*";
            this.classNameString = aClass.getName();
            this.methodString = method.getName();
            this.parameterTypesString = "*";
        } catch (Exception e) {
            Log.ERROR("解析表达式失败："+method.toString());
        }
    }

    public AspectMatcher(Class<?> aClass) {
        try {
            this.permissionsString = "*";
            this.classNameString = aClass.getName();
            this.methodString = "*";
            this.parameterTypesString = "*";
        } catch (Exception e) {
            Log.ERROR("解析表达式失败："+aClass.toString());
        }
    }

    /**
     * 解析表达式完成匹配器的初始化
     * @param matcher
     */
    private void parseMatcher(String matcher) {
        String[] split = matcher.split(" ");
        this.permissionsString = split[0];
        int last = split.length-1;
        int lastDotIndex = split[last].lastIndexOf(".");
        this.classNameString = split[last].substring(0, lastDotIndex);
        String methodNameAndParameter = split[last].substring(lastDotIndex+1);
        this.methodString = methodNameAndParameter.split("\\u0028")[0];//split函数需要用到转义字符(
        this.parameterTypesString = methodNameAndParameter.split("\\u0028")[1].replace(")", "");//转义字符(
        if ("".equals(this.parameterTypesString)) {
            this.parameterTypesString = "[]";
        }
    }

    /**
     * 判断一个类是否满足此匹配器
     * @param aClass
     * @return
     */
    public boolean isMatchClass(Class<?> aClass) {
        return isMatchClassNameString(aClass.getName());
    }

    /**
     * 判断一个方法是否满足此匹配器
     * @param method
     * @return
     */
    public boolean isMatchMethod(Method method) {
        String permissions = method.toString().split(" ")[0];
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();
        String parameterTypes = Arrays.toString(method.getParameterTypes());
        if (isMatchMethodString(methodName) && isMatchClassNameString(className) && isMatchPermissionsString(permissions) && isMatchParameterTypesString(parameterTypes)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断一个类中是否存在满足此匹配器的方法
     * @param aClass
     * @return
     */
    public boolean existMethodForClass(Class<?> aClass) {
        Method[] declaredMethods = aClass.getDeclaredMethods();
        for (int i = 0; i < declaredMethods.length; i++) {
            if (isMatchMethod(declaredMethods[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断方法权限修饰符是否满足此匹配器
     * @param str
     * @return
     */
    private boolean isMatchPermissionsString(String str) {
        if ("*".equals(this.permissionsString)) {
            return true;
        } else if (this.permissionsString.equals(str)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断方法所属的类名是否满足此匹配器
     * @param str
     * @return
     */
    private boolean isMatchClassNameString(String str) {
        if ("*.".equals(this.classNameString)) {
            return true;
        } else {
            String[] matcherClassNameSplit = classNameString.split("\\u002E");//转义字符.
            String[] strSplit = str.split("\\u002E");//转义字符.
            for (int i = 0; i < matcherClassNameSplit.length; i++) {
                if ("*".equals(matcherClassNameSplit[i])) {
                    continue;
                }
                if ("".equals(matcherClassNameSplit)) {
                    return true;
                }
                if (!matcherClassNameSplit[i].equals(strSplit[i])) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * 判断方法是否满足此匹配器
     * @param str
     * @return
     */
    private boolean isMatchMethodString(String str) {
        if ("*".equals(this.methodString)) {
            return true;
        } else if (this.methodString.equals(str)) {
            return true;
        } else if (this.methodString.endsWith("*")) {
            if (str.startsWith(this.methodString.replace("*", ""))) {
                return true;
            } else {
                return false;
            }
        } else if (this.methodString.startsWith("*")) {
            if (str.endsWith(this.methodString.replace("*", ""))) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 判断方法参数是否满足此匹配器
     * @param str
     * @return
     */
    private boolean isMatchParameterTypesString(String str) {
        if ("*".equals(this.parameterTypesString)) {
            return true;
        } else if (this.parameterTypesString.equals(str)) {
            return true;
        } else {
            return false;
        }
    }
}
