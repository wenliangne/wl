package com.wenliang.security;

/**
 * @author wenliang
 * @date 2019-12-26
 * 简介：
 */
public class PageGenerator {

    public static String getDefaultLoginPage() {
        String body = "<center><h1>默认的登陆界面</h1></center>\n" +
                "<center>\n" +
                "<form action=\"" +
                SecurityContext.getSecurityConfig().getProperty("security.login") +
                "\" method=\"post\">\n" +
                "    <input type=\"text\" name=\"" +
                SecurityContext.getSecurityConfig().getProperty("security.username") +
                "\"><br>\n" +
                "    <input type=\"password\" name=\"" +
                SecurityContext.getSecurityConfig().getProperty("security.password") +
                "\"><br>\n" +
                "    <input type=\"submit\">\n" +
                "</form>\n" +
                "</center>\n";
        return getHtml(body);
    }
    public static String getDefaultForbidPage() {
        return getHtml("<h1>当前用户权限不足</h1><br>\n");
    }

    public static String getDefaultLoginSuccessPage() {
        return getHtml("<h1>登陆成功！</h1><br>\n");
    }

    public static String getDefaultLoginFailurePage() {
        return getHtml(                "<h1>验证失败！请检查用户名或密码！</h1><br>\n" +
                "<input type=\"button\" name=\"Submit\" value=\"返回重新登录\" onclick=\"javascript:history.back(-1);\">\n");
    }

    public static String getDefaultLogoutPage() {
        return getHtml("<h1>注销成功！</h1><br>\n");
    }

    public static String getHtml(String body) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                body +
                "</body>\n" +
                "</html>";
    }

}
