package com.wenliang.test.listener;

import com.wenliang.context.annotations.ContextListener;
import com.wenliang.context.listener.interfaces.BeforeScanConfigurationListener;
import com.wenliang.core.log.Log;

/**
 * @author wenliang
 * @date 2019-07-23
 * 简介：
 */

@ContextListener
public class BeforeScanConfigurationListenerImpl implements BeforeScanConfigurationListener {
    @Override
    public void doListener() {
        Log.INFO("执行了扫描配置文件之前的监听器！");
    }
}
