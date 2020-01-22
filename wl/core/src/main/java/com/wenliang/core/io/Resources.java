package com.wenliang.core.io;



import com.wenliang.core.log.Log;

import java.io.InputStream;

/**
 * @author wenliang
 * @date 2019-06-25
 * 简介：用于获取一个字节流
 */
public class Resources {

    /**
     * 根据传入的文件路径，获取一个字节输入流
     * @param filePath
     * @return
     */
    public static InputStream getResourceAsStream(String filePath){
        InputStream resourceAsStream = Resources.class.getClassLoader().getResourceAsStream(filePath);
        if (resourceAsStream == null) {
            Log.ERROR("File not found！");
            throw new RuntimeException("加载配置文件失败！");
        } else {
            Log.INFO("Gets the byte stream of the configuration file!("+filePath+")");
        }
        return resourceAsStream;
    }
}
