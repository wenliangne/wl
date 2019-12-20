package com.wenliang.context.container.interfaces;

import com.wenliang.context.cfg.ContextConfiguration;

import java.io.InputStream;

/**
 * @author wenliang
 * @date 2019-07-09
 * 简介：
 */
public interface ApplicationContextBuilder {

    /**
     * 通过输入流加载配置
     * @param inputStream
     * @return
     */
    public ContextConfiguration load(InputStream inputStream);

    /**
     * 创建一个工厂对象
     * @return
     */
    public ApplicationContextFactory builder();
}
