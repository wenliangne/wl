package com.wenliang.test.listener;

import com.wenliang.context.annotations.ContextListener;
import com.wenliang.context.listener.interfaces.AfterCreateBeanListener;
import com.wenliang.core.log.Log;

/**
 * @author wenliang
 * @date 2019-07-23
 * 简介：
 */

@ContextListener
public class AfterCreateBeanListenerImpl implements AfterCreateBeanListener {
    @Override
    public void doListener() {
        Log.INFO("执行了创建bean之后的的监听器！");
    }
}
