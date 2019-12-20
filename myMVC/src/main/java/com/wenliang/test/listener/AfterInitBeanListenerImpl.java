package com.wenliang.test.listener;

import com.wenliang.context.annotations.ContextListener;
import com.wenliang.context.cfg.DefaultBeanApplicationContext;
import com.wenliang.context.listener.interfaces.AfterInitBeanListener;
import com.wenliang.core.log.Log;

import java.util.Map;

/**
 * @author wenliang
 * @date 2019-07-23
 * 简介：
 */

@ContextListener
public class AfterInitBeanListenerImpl implements AfterInitBeanListener {
    @Override
    public void doListener() {
        Log.INFO("执行了初始化对象后的监听器");
        Map context = DefaultBeanApplicationContext.getContext();
        for (Object o : context.entrySet()) {
            System.out.println(o);
        }
    }
}
