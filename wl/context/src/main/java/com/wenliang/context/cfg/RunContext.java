package com.wenliang.context.cfg;

import com.wenliang.context.container.interfaces.ApplicationContextFactory;
import com.wenliang.context.container.DefaultApplicationContextBuilder;
import com.wenliang.context.container.MergeDefaultURLBuilder;
import com.wenliang.core.io.Resources;
import com.wenliang.mapper.cfg.DefaultRepositoryApplicationContext;
import com.wenliang.mapper.cfg.RunMapper;
import com.wenliang.mapper.sqlsession.SqlSession;

import java.io.InputStream;

/**
 * @author wenliang
 * @date 2019-07-15
 * 简介：
 */
public class RunContext {

    private RunContext() {
    }

    public static void Run() {
        Run("application.xml");
    }

    public static void Run(String filePath) {

        InputStream inputStream = Resources.getResourceAsStream(filePath);
        DefaultApplicationContextBuilder builder = new DefaultApplicationContextBuilder();
        ContextConfiguration contextConfiguration = builder.load(inputStream);
        ApplicationContextFactory contextFactory = builder.builder();

        SqlSession sqlSession = RunMapper.Run(filePath);
        DefaultRepositoryApplicationContext.setSqlSession(sqlSession);

        MergeDefaultURLBuilder urlBuilder = new MergeDefaultURLBuilder();
        urlBuilder.load();
        urlBuilder.builder();

        contextFactory.initContext(contextConfiguration);
    }

}
