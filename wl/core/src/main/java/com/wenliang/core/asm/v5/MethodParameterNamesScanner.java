package com.wenliang.core.asm.v5;



import com.wenliang.core.io.Resources;
import org.kohsuke.asm5.ClassReader;
import org.kohsuke.asm5.Opcodes;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wenliang
 * @date 2019-08-08
 * 简介：
 */



public class MethodParameterNamesScanner {
    public final static Object lock = new Object();
    public static Map<String, List<String>> ParameterNamesMap = new HashMap<String, List<String>>();
    public static List<String> tempParameterNames=new ArrayList<String>();
    public static Method tempMethod;
    public static int index;

    public static String[] getParameterNames(Method method) {
        List<String> paList = ParameterNamesMap.get(method.toString());
        if (paList != null) {
            return paList.toArray(new String[paList.size()]);
        }

        String[] paArr;
        ClassReader classReader;
        InputStream in=null;
        paList = new ArrayList<String>();
        synchronized (lock) {
            tempMethod = method;
            index = method.getParameterTypes().length;
            if (!Modifier.isStatic(tempMethod.getModifiers())) {
                index++;
            }
            String className = tempMethod.getDeclaringClass().getName();
            try {
                in = Resources.getResourceAsStream(className.replace(".", "/") + ".class");
                classReader = new ClassReader(in);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }finally {
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
            classReader.accept(new DefaultClassVisitor(Opcodes.ASM5), 0);
            paList.addAll(tempParameterNames);
            ParameterNamesMap.put(method.toString(), paList);
            paArr = tempParameterNames.toArray(new String[tempParameterNames.size()]);
            tempParameterNames.clear();
            tempMethod = null;
            index = 0;
        }
        return paArr;
    }

}
