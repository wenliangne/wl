package com.wenliang.core.util;

import com.wenliang.core.container.DefaultBeanNameMap;
import com.wenliang.core.log.Log;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author wenliang
 * @date 2019-07-30
 * 简介：
 */
public class ClassUtils {
    private ClassUtils() {
    }

    /**
     * 判断是否为复杂对象（及基本数据类型及字符串以外的对象）
     * @param parameterType
     * @return
     */
    private boolean isComplexObject(Class<?> parameterType) {
        if (byte.class.isAssignableFrom(parameterType) || Byte.class.isAssignableFrom(parameterType)
                ||int.class.isAssignableFrom(parameterType) || Integer.class.isAssignableFrom(parameterType)
                ||float.class.isAssignableFrom(parameterType) || Float.class.isAssignableFrom(parameterType)
                ||double.class.isAssignableFrom(parameterType) || Double.class.isAssignableFrom(parameterType)
                ||long.class.isAssignableFrom(parameterType) || Long.class.isAssignableFrom(parameterType)
                ||String.class.isAssignableFrom(parameterType)|| Date.class.isAssignableFrom(parameterType)
                ) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 判断是否为数字和字符串
     * @param aClass
     * @return
     */
    public static boolean isStringAndNumber(Class<?> aClass) {
        if (int.class.isAssignableFrom(aClass) || Integer.class.isAssignableFrom(aClass)) {
            return true;
        } else if (float.class.isAssignableFrom(aClass) || Float.class.isAssignableFrom(aClass)){
            return true;
        } else if (double.class.isAssignableFrom(aClass) || Double.class.isAssignableFrom(aClass)) {
            return true;
        } else if (long.class.isAssignableFrom(aClass) || Long.class.isAssignableFrom(aClass)) {
            return true;
        }else if (byte.class.isAssignableFrom(aClass) || Byte.class.isAssignableFrom(aClass)) {
            return true;
        } else if (String.class.isAssignableFrom(aClass)) {
            return true;
        }
        return false;
    }

    /**
     * 将某个类的父类及所有实现的接口递归作为key，将value放入指定的map
     * @param aClass
     * @param value
     */
    public static void putValueToInterfaceAndSuper(Class<?> aClass, String value) {
        DefaultBeanNameMap.put(aClass.getName(),value);
        Class<?>[] interfaces = aClass.getInterfaces();
        if (interfaces != null) {
            for (Class<?> anInterface : interfaces) {
                DefaultBeanNameMap.put(anInterface.getName(),value);
                putValueToInterfaceAndSuper(anInterface,value);
            }
        }
        Class<?> superclass = aClass.getSuperclass();
        if (superclass != null) {
            DefaultBeanNameMap.put(superclass.getName(),value);
            putValueToInterfaceAndSuper(superclass, value);
        }

    }

    /**
     * 获取jar包中的所有class
     * @param packageName
     * @return
     * @throws IOException
     */
    public static Set<Class<?>> getClassFromJar(String packageName) throws IOException {
        Set<Class<?>> reClassSet = new HashSet<Class<?>>();
        //通过当前线程得到类加载器从而得到URL的枚举
        Enumeration<URL> urlEnumeration = Thread.currentThread().getContextClassLoader().getResources(packageName.replace(".", "/"));
        while (urlEnumeration.hasMoreElements()) {
            URL url = urlEnumeration.nextElement();
            String protocol = url.getProtocol();
            if ("jar".equalsIgnoreCase(protocol)) {
                reClassSet.addAll(scanJar(url,packageName));
            }
        }
        return reClassSet;
    }


    /**
     * 获取jar包和用户包下得class
     * @param packageName
     * @return
     * @throws IOException
     */
    public static Set<Class<?>> getClassFromJarAndPackage(String packageName) throws IOException {
        Set<Class<?>> reClassSet = new HashSet<Class<?>>();
        //通过当前线程得到类加载器从而得到URL的枚举
        Enumeration<URL> urlEnumeration = Thread.currentThread().getContextClassLoader().getResources(packageName.replace(".", "/"));
        while (urlEnumeration.hasMoreElements()) {
            URL url = urlEnumeration.nextElement();
            String protocol = url.getProtocol();
            if ("jar".equalsIgnoreCase(protocol)) {
                reClassSet.addAll(scanJar(url,packageName));
            } else if ("file".equalsIgnoreCase(protocol)) {
                reClassSet.addAll(ClassUtils.getClassFromPackage(packageName));
            }
        }
        return reClassSet;
    }

    /**
     * 获取指定jar包url下得所有class
     * @param url
     * @param packageName
     * @return
     * @throws IOException
     */
    private static Set<Class<?>> scanJar(URL url,String packageName) throws IOException {
        Set<Class<?>> reClassSet = new HashSet<Class<?>>();
        //转换为JarURLConnection
        JarURLConnection connection = (JarURLConnection) url.openConnection();
        if (connection != null) {
            JarFile jarFile = connection.getJarFile();
            if (jarFile != null) {
                //得到该jar文件下面的类实体
                Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
                while (jarEntryEnumeration.hasMoreElements()) {
                    JarEntry entry = jarEntryEnumeration.nextElement();
                    String jarEntryName = entry.getName();
                    //这里需要过滤不是class文件和不在basePack包名下的类
                    if (jarEntryName.contains(".class") && jarEntryName.replaceAll("/",".").startsWith(packageName)) {
                        String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replace("/", ".");
                        try {
                            Class cls = Class.forName(className);
                            reClassSet.add(cls);
                        } catch (Throwable e) {
                        }
                    }
                }
            }
        }
        return reClassSet;
    }

    /**
     * 获取用户包中得所有class
     * @param packageName
     * @return
     */
    public static Set<Class<?>> getClassFromPackage(String packageName) {
        Set<Class<?>> classSet = new HashSet<>();
        File file = new File(ClassUtils.class.getResource("/").getPath() + packageName.replace(".", "/"));
        String basePath = file.getAbsolutePath().replace(packageName.replace(".","\\"),"");
        int baseLen = "".equals(packageName)?basePath.length()+1:basePath.length();
        List<String> fileAbsoluteNameList = FileUtils.getFileAbsoluteNameList(file, ".class");
        for (String fileAbsoluteName : fileAbsoluteNameList) {
            String className = fileAbsoluteName.substring(baseLen, fileAbsoluteName.length() - 6).replace("\\", ".");
            Class<?> aClass = null;
            try {
                aClass = Class.forName(className);
                classSet.add(aClass);
            } catch (Exception e) {
                Log.WARN("类加载失败："+className);
            }
        }
        return classSet;
    }

    /**
     * 获取带有指定指定注解的class
     * @param packageName
     * @param annotation
     * @return
     * @throws IOException
     */
    public static Set<Class<?>> getClassWithAnnotation(String packageName, Class<? extends Annotation> annotation) throws IOException {
        Set<Class<?>> classSet = new HashSet<>();
        Set<Class<?>> classFromPackage = getClassFromJarAndPackage(packageName);
        for (Class<?> aClass : classFromPackage) {
            if (aClass.getAnnotation(annotation) != null) {
                classSet.add(aClass);
            }
        }
        return classSet;
    }

    /**
     * 获取带有指定指定注解的class
     * @param packageNames
     * @param annotation
     * @return
     * @throws IOException
     */
    public static Set<Class<?>> getClassWithAnnotation(String[] packageNames, Class<? extends Annotation> annotation) throws IOException {
        Set<Class<?>> classSet = new HashSet<>();
        for (int i = 0; i < packageNames.length; i++) {
            Set<Class<?>> classWithAnnotation = getClassWithAnnotation(packageNames[i], annotation);
            classSet.addAll(classWithAnnotation);
        }
        return classSet;
    }
}
