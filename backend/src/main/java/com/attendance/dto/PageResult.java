package com.attendance.dto;

import java.util.List;

public class PageResult<T> {
    private long total;
    private List<T> rows;
    private int page;
    private int pageSize;

    public PageResult() {}

    public PageResult(long total, List<T> rows, int page, int pageSize) {
        this.total = total;
        this.rows = rows;
        this.page = page;
        this.pageSize = pageSize;
    }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public List<T> getRows() { return rows; }
    public void setRows(List<T> rows) { this.rows = rows; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
}
