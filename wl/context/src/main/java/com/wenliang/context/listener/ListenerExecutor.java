package com.wenliang.context.listener;

import com.wenliang.context.cfg.ContextListenerBean;
import com.wenliang.context.cfg.DefaultBeanApplicationContext;

import java.util.List;

/**
 * @author wenliang
 * @date 2019-08-02
 * 简介：
 */
public class ListenerExecutor {
    public void excutr(Class<?> aClass) {
        List<ContextListenerBean> contextListenerBean = DefaultBeanApplicationContext.getContextListenerBean();
        if (contextListenerBean == null) {
            return;
        }
        for (ContextListenerBean listenerBean : contextListenerBean) {
            if (aClass.isAssignableFrom(listenerBean.getListenerType())) {
                listenerBean.getListener().doListener();
            }
        }
    }
}
