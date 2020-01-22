package com.wenliang.core.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wenliang
 * @date 2019-12-27
 * 简介：
 */
public class FileUtils {

    public static List<File> getFileList(String fileName,String suffix) {
        return getFileList(new File(fileName),suffix);
    }
    /**
     * 获取文件夹内指定后缀的所有文件
     * @param file
     * @return
     */
    public static List<File> getFileList(File file,String suffix) {
        List<File> reList = new ArrayList<File>();
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            int len = fileList.length;
            for (int i = 0; i < len; i++) {
                List<File> tempFileList = getFileList(fileList[i],suffix);
                reList.addAll(tempFileList);
            }
        } else if (file.getName().endsWith(suffix)){
            reList.add(file);
        }
        return reList;
    }


    public static List<String> getFileSimpleNameList(String fileName,String suffix) {
        return getFileSimpleNameList(new File(fileName),suffix);
    }
    /**
     * 获取文件夹中指定后缀的所有文件的简单文件名
     * @param file
     * @return
     */
    public static List<String> getFileSimpleNameList(File file,String suffix) {
        List<String> reList = new ArrayList<String>();
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            int len = fileList.length;
            for (int i = 0; i < len; i++) {
                List<String> tempFileList = getFileSimpleNameList(fileList[i],suffix);
                reList.addAll(tempFileList);
            }
        } else if (file.getName().endsWith(suffix)){
            reList.add(file.getName());
        }
        return reList;
    }


    public static List<String> getFileAbsoluteNameList(String fileName,String suffix) {
        return getFileAbsoluteNameList(new File(fileName),suffix);
    }
    /**
     * 获取文件夹中指定后缀的所有文件的全文件名
     * @param file
     * @return
     */
    public static List<String> getFileAbsoluteNameList(File file,String suffix) {
        List<String> reList = new ArrayList<String>();
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            int len = fileList.length;
            for (int i = 0; i < len; i++) {
                List<String> tempFileList = getFileAbsoluteNameList(fileList[i],suffix);
                reList.addAll(tempFileList);
            }
        } else if (file.getName().endsWith(suffix)){
            reList.add(file.getAbsolutePath());
        }
        return reList;
    }
}
