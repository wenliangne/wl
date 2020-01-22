package com.wenliang.core.log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Properties;

/**
 * @author wenliang
 * @date 2019-06-25
 * 简介：用于日志打印的工具
 */
public class Log {

    private static String level="INFO";
    private static String printType="CONSOLE";
    private static String path = Log.class.getResource("/").getPath() + "WLlog.log";
    static {
        try {
            Properties p = new Properties();
            p.load(Log.class.getClassLoader().getResourceAsStream("application.properties"));
            String tempLevel = p.getProperty("log.level");
            level = tempLevel.toUpperCase();
            String tempConsole = p.getProperty("log.printType");
            printType = tempConsole.toUpperCase();
        } catch (Exception e) {
            INFO("使用默认的日志级别：INFO！");
            INFO("使用默认的打印位置：CONSOLE！");
        }
    }

    public static void INFO(String message) {
        INFO(message,null);
    }
    public static void INFO(String message,String detailMessage) {
        if ("INFO".equals(level)) {
            Date date = new Date();
            message = date.toString()+" / "+date.getTime()+"  [INFO :]  "+message;
            if (detailMessage != null) {
                message =message+ "\n\r"+detailMessage;
            }
            soutBlack(message);
        }
    }

    public static void WARN(String message) {
        WARN(message,null,null);
    }
    public static void WARN(String message,String detailMessage) {
        WARN(message,detailMessage,null);
    }
    public static void WARN(String message,Exception e) {
        WARN(message,null,e);
    }
    public static void WARN(String message,String detailMessage,Exception e) {
        if ("INFO".equals(level) || "WARN".equals(level)) {
            Date date = new Date();
            message = date.toString()+" / "+date.getTime()+"  [WARN :]  "+message;
            if (detailMessage != null) {
                message =message+ "\n\r"+detailMessage;
            }
            soutRed(message);
            if (e != null) {
                e.printStackTrace();
            }
        }
    }

    public static void ERROR(String message) {
        ERROR(message,null,null);
    }
    public static void ERROR(String message,String detailMessage) {
        ERROR(message,detailMessage,null);
    }
    public static void ERROR(String message,Exception e) {
        ERROR(message,null,e);
    }
    public static void ERROR(String message,String detailMessage,Exception e) {
        if ("INFO".equals(level) || "WARN".equals(level) || "ERROR".equals(level)) {
            Date date = new Date();
            message = date.toString()+" / "+date.getTime()+"  [ERROR:]  "+message;
            if (detailMessage != null) {
                message =message+ "\n\r"+detailMessage;
            }
            soutRed(message);
            if (e != null) {
                e.printStackTrace();
            }
        }
    }



    private static void soutBlack(String message) {
        if (printType.contains("CONSOLE")) {
            System.out.println(message);
        }
        if (printType.contains("FILE")) {
            soutToFile(message);
        }

    }

    private static void soutRed(String message) {
        if (printType.contains("CONSOLE")) {
            System.err.println(message);
        }
        if (printType.contains("FILE")) {
            soutToFile(message);
        }
    }

    private static void soutToFile(String message) {
        File file = new File(path);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file,true);
            fileOutputStream.write((message+"\r\n").getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
