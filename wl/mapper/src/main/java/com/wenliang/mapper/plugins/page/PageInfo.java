package com.wenliang.mapper.plugins.page;

import java.util.List;

/**
 * @author wenliang
 * @date 2019-07-18
 * 简介：
 */
public class PageInfo<T>{
    public PageInfo(Integer pageNum,Integer pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    //结果集
    private List<T> list;
    //当前页
    private int pageNum;
    //每页的数量
    private int pageSize;
    //当前页的数量
    private int size;
    //总记录数
    private int total;
    //总页数
    private int pages;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
        this.pages = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
