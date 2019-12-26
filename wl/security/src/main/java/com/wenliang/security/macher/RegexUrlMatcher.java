package com.wenliang.security.macher;

import java.util.regex.Pattern;

/**
 * @author wenliang
 * @date 2019-12-24
 * 简介：
 */
public class RegexUrlMatcher implements UrlMatcher {
    private static String asterisk="[A-Za-z0-9]+\\.?[A-Za-z0-9]*";
    private static String twoAsterisk="[A-Za-z0-9/]+\\.?[A-Za-z0-9]*";
    private String regex;

    public RegexUrlMatcher(String standardUrl) {
        standardUrl = standardUrl.replace("**", twoAsterisk);
        regex = standardUrl.replace("*", asterisk);
    }
    public boolean matcher(String checked) {
        return Pattern.matches(regex, checked);
    }
}
