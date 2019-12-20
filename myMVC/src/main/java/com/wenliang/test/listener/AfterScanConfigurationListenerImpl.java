package com.wenliang.test.listener;

import com.wenliang.context.annotations.ContextListener;
import com.wenliang.context.listener.interfaces.AfterScanConfigurationListener;
import com.wenliang.core.log.Log;

/**
 * @author wenliang
 * @date 2019-07-23
 * 简介：
 */

@ContextListener
public class AfterScanConfigurationListenerImpl implements AfterScanConfigurationListener {
    @Override
    public void doListener() {
        Log.INFO("执行了扫描配置文件之后的监听器！");
    }
}
