package com.wenliang.context.listener.interfaces;

/**
 * @author wenliang
 * @date 2019-07-23
 * 简介：使用此系统提供的监听器只需要创建一个类继承对应的监听器接口，
 * 然后再在类上加上此系统提供的@Listener注解即可，也可在application.xml中使用listener标签进行配置。
 */
public interface ListenerInterface {
    public void doListener();
}
