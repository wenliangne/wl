package com.wenliang.mapper.plugins.page;

/**
 * @author wenliang
 * @date 2019-07-18
 * 简介：
 */
public class PageHelper {


    private static ThreadLocal<PageInfo> localPageInfo=new ThreadLocal<>();


    public static void startPage(Integer pageNum,Integer pageSize){
        localPageInfo.set(new PageInfo(pageNum,pageSize));
    }

    public static PageInfo getPageInfo() {
        return localPageInfo.get();
    }

    public static ThreadLocal<PageInfo> getLocalPageInfo() {
        return localPageInfo;
    }

    public static void setLocalPageInfo(ThreadLocal<PageInfo> localPageInfo) {
        PageHelper.localPageInfo = localPageInfo;
    }
}
