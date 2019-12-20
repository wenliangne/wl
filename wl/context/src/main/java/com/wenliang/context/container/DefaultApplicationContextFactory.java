package com.wenliang.context.container;

import com.wenliang.context.cfg.*;
import com.wenliang.context.container.interfaces.ApplicationContextFactory;
import com.wenliang.context.listener.interfaces.AfterCreateBeanListener;
import com.wenliang.context.listener.interfaces.AfterInitBeanListener;
import com.wenliang.context.listener.interfaces.BeforeCreateBeanListener;
import com.wenliang.context.listener.ListenerExecutor;


/**
 * @author wenliang
 * @date 2019-07-09
 * 简介：
 */
public class DefaultApplicationContextFactory implements ApplicationContextFactory {
    @Override
    public boolean initContext(ContextConfiguration contextConfiguration) {
        ListenerExecutor listenerExecutor = new ListenerExecutor();
        //执行创建对象之前的监听器
        listenerExecutor.excutr(BeforeCreateBeanListener.class);
        //创建对象
        BeanFactory beanFactory = new BeanFactory();
        beanFactory.createBean(contextConfiguration);
        //执行创建对象后的监听器
        listenerExecutor.excutr(AfterCreateBeanListener.class);
        //初始化对象
        beanFactory.initBean(contextConfiguration);
        //执行初始化对象后的监听器
        listenerExecutor.excutr(AfterInitBeanListener.class);
        return true;
    }


}