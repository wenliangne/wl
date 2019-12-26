package com.wenliang.security;

import com.wenliang.security.macher.UrlMatcherFactory;

/**
 * @author wenliang
 * @date 2019-12-24
 * 简介：
 */
public class SecurityRunner {
    public static void run() {
        UrlMatcherFactory.initUrlMatcher();
    }
}
