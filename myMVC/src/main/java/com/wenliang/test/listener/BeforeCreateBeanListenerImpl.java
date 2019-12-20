package com.wenliang.test.listener;

import com.wenliang.context.annotations.ContextListener;
import com.wenliang.context.listener.interfaces.BeforeCreateBeanListener;
import com.wenliang.core.log.Log;

/**
 * @author wenliang
 * @date 2019-07-23
 * 简介：
 */

@ContextListener
public class BeforeCreateBeanListenerImpl implements BeforeCreateBeanListener {

    @Override
    public void doListener() {
        Log.INFO("执行了创建bean之前的的监听器！");
    }
}
