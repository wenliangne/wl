package com.wenliang.security.macher;

/**
 * @author wenliang
 * @date 2019-12-24
 * 简介：
 */
public class FixedUrlMatcher implements UrlMatcher {
    private String fixed;

    public FixedUrlMatcher(String standardUrl) {
        fixed = standardUrl;
    }
    public boolean matcher(String checked) {
        return fixed.equals(checked);
    }
}
