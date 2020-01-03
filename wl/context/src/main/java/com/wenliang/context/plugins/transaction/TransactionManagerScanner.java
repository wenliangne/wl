package com.wenliang.context.plugins.transaction;

import com.wenliang.context.annotations.*;
import com.wenliang.context.cfg.DefaultBeanApplicationContext;
import com.wenliang.context.proxy.ExecutorCommonAspect;
import com.wenliang.core.log.Log;
import com.wenliang.core.util.ClassUtils;
import com.wenliang.mapper.plugins.TransactionManager;

import java.lang.reflect.Method;
import java.util.*;

import org.dom4j.Element;

/**
 * @author wenliang
 * @date 2019-08-01
 * 简介：
 */
public class TransactionManagerScanner {

    public void scan(Element root) {
    }
    public void scan(String[] packageNames) {
        Set<Class<?>> componentSet = ClassUtils.getClassWithAnnotation(packageNames,Component.class);
        Set<Class<?>> serviceSet = ClassUtils.getClassWithAnnotation(packageNames,Service.class);
        List<ExecutorCommonAspect> scanComponentSet = scan(componentSet);
        DefaultBeanApplicationContext.getExecutorCommonAspectList().addAll(scanComponentSet);
        List<ExecutorCommonAspect> scanServiceSet = scan(serviceSet);
        DefaultBeanApplicationContext.getExecutorCommonAspectList().addAll(scanServiceSet);

    }

    public List<ExecutorCommonAspect> scan(Set<Class<?>> classSet) {
        if (classSet == null) {
            return null;
        }
        TransactionManager defaultTransactionManager = new DefaultTransactionManager();
        List<ExecutorCommonAspect> executorCommonAspectList = new ArrayList<>();
        for (Class<?> aClass : classSet) {
            try {
                Transaction transactionClass = aClass.getAnnotation(Transaction.class);
                if (transactionClass != null) {
                    String value = transactionClass.value();
                    if (value.equals("")) {
                        ExecutorCommonAspect setAutoCommitFalse = createExecutorCommonAspect(defaultTransactionManager, "setAutoCommitFalse", AspectBefore.class);
                        setAutoCommitFalse.setMatchString(aClass);
                        executorCommonAspectList.add(setAutoCommitFalse);
                        ExecutorCommonAspect commit = createExecutorCommonAspect(defaultTransactionManager, "commit", AspectAfter.class);
                        commit.setMatchString(aClass);
                        executorCommonAspectList.add(commit);
                        ExecutorCommonAspect rollback = createExecutorCommonAspect(defaultTransactionManager, "rollback", AspectThrowing.class);
                        rollback.setMatchString(aClass);
                        executorCommonAspectList.add(rollback);
                        ExecutorCommonAspect release = createExecutorCommonAspect(defaultTransactionManager, "release", AspectFinally.class);
                        release.setMatchString(aClass);
                        executorCommonAspectList.add(release);
                    } else {
                        ExecutorCommonAspect setAutoCommitFalse = createExecutorCommonAspect(defaultTransactionManager, "setAutoCommitFalse", AspectBefore.class);
                        setAutoCommitFalse.setMatchString(aClass, value);
                        executorCommonAspectList.add(setAutoCommitFalse);
                        ExecutorCommonAspect commit = createExecutorCommonAspect(defaultTransactionManager, "commit", AspectAfter.class);
                        commit.setMatchString(aClass, value);
                        executorCommonAspectList.add(commit);
                        ExecutorCommonAspect rollback = createExecutorCommonAspect(defaultTransactionManager, "rollback", AspectThrowing.class);
                        rollback.setMatchString(aClass, value);
                        executorCommonAspectList.add(rollback);
                        ExecutorCommonAspect release = createExecutorCommonAspect(defaultTransactionManager, "release", AspectFinally.class);
                        release.setMatchString(aClass, value);
                        executorCommonAspectList.add(release);
                    }
                } else {
                    Method[] declaredMethods = aClass.getDeclaredMethods();
                    for (int i = 0; i < declaredMethods.length; i++) {
                        if (declaredMethods[i].isAnnotationPresent(Transaction.class)) {
                            ExecutorCommonAspect setAutoCommitFalse = createExecutorCommonAspect(defaultTransactionManager, "setAutoCommitFalse", AspectBefore.class);
                            setAutoCommitFalse.setMatchString(aClass, declaredMethods[i]);
                            executorCommonAspectList.add(setAutoCommitFalse);
                            ExecutorCommonAspect commit = createExecutorCommonAspect(defaultTransactionManager, "commit", AspectAfter.class);
                            commit.setMatchString(aClass, declaredMethods[i]);
                            executorCommonAspectList.add(commit);
                            ExecutorCommonAspect rollback = createExecutorCommonAspect(defaultTransactionManager, "rollback", AspectThrowing.class);
                            rollback.setMatchString(aClass, declaredMethods[i]);
                            executorCommonAspectList.add(rollback);
                            ExecutorCommonAspect release = createExecutorCommonAspect(defaultTransactionManager, "release", AspectFinally.class);
                            release.setMatchString(aClass, declaredMethods[i]);
                            executorCommonAspectList.add(release);
                        }

                    }
                }
            } catch (Exception e) {
                Log.ERROR("创建事务管理失败："+aClass.getName());
            }

        }
        return executorCommonAspectList;
    }

    private ExecutorCommonAspect createExecutorCommonAspect(Object bean,String methodName,Class<?> annotationType ) throws NoSuchMethodException {
        ExecutorCommonAspect executorCommonAspect = new ExecutorCommonAspect();
        executorCommonAspect.setBean(bean);
        Method method = bean.getClass().getDeclaredMethod(methodName);
        executorCommonAspect.setMethod(method);
        executorCommonAspect.setAnnotationType(annotationType);
        return executorCommonAspect;
    }

}
