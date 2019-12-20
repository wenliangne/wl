package com.wenliang.context.container.interfaces;

import com.wenliang.context.cfg.ContextConfiguration;
import com.wenliang.core.container.BeanApplicationContext;

/**
 * @author wenliang
 * @date 2019-07-09
 * 简介：
 */
public interface ApplicationContextFactory {

    /**
     * 通过配置类创建容器
     * @param contextConfiguration
     * @return
     */
    public boolean initContext(ContextConfiguration contextConfiguration);


}
