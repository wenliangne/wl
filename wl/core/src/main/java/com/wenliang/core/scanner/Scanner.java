package com.wenliang.core.scanner;

/**
 * @author wenliang
 * @date 2020-07-06
 * 简介：
 */
public interface Scanner {

    void doScan(String packageName) throws Exception;

    void doScan(String[] packageNames) throws Exception;
}
