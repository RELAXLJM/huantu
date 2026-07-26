package com.huantu.common;

import java.util.List;

/**
 * 分页返回结果封装
 */
public class PageResult<T> {

    private long total;       // 总记录数
    private int page;         // 当前页码
    private int pageSize;     // 每页大小
    private int totalPages;   // 总页数
    private List<T> list;     // 当前页数据

    public PageResult() {
    }

    public PageResult(long total, int page, int pageSize, List<T> list) {
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
        this.list = list;
    }

    // ==================== Getter / Setter ====================

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
