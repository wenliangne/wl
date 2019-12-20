package com.wenliang.context.cfg;

import com.wenliang.context.listener.interfaces.ListenerInterface;

/**
 * @author wenliang
 * @date 2019-07-23
 * 简介：
 */
public class ContextListenerBean {
    private ListenerInterface listener;
    private Class<?> listenerType;

    public ListenerInterface getListener() {
        return listener;
    }

    public void setListener(ListenerInterface listener) {
        this.listener = listener;
    }

    public Class<?> getListenerType() {
        return listenerType;
    }

    public void setListenerType(Class<?> listenerType) {
        this.listenerType = listenerType;
    }
}
