package com.cxytiandi.kitty.common.page;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页信息
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-02-16 15:03
 */
public class Page<E> implements Serializable {

    /**
     * 起始记录数
     */
    private int start;

    /**
     * 最大记录数
     */
    private int limit;

    /**
     * 总记录数
     */
    private long totalRecords;

    /**
     * 当前页
     */
    private int currentPage;

    /**
     * 总页数
     */
    private int totalPages;

    /**
     * 查询数据对象
     */
    private List<E> list;

    public static int page2Start(int page, int limit){
        return page * limit - limit;
    }

    public static int start2Page(int start, int limit){
        if(limit == 0) {
            return 0;
        }
        return (int)(start + limit) / limit;
    }

    public static int calcPages(long totalRecords, int limit) {
        return (int) totalRecords / limit
                + (totalRecords % limit > 0 ? 1 : 0);
    }

    public Page(){

    }

    public Page(int start, int limit){
        this.start = start;
        this.limit = limit;
        this.currentPage = start2Page(start, limit);
    }

    public Page(int start, int limit, List<E> list, long totalRecords){
        this.start = start;
        this.limit = limit;
        this.list = list;
        this.totalRecords = totalRecords;
        this.currentPage = start2Page(start, limit);
        if (this.limit == 0) {
            this.totalPages = 0;
        } else {
            this.totalPages = (int) this.totalRecords / this.limit
                    + (this.totalRecords % this.limit > 0 ? 1 : 0);
        }

    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setList(List<E> list) {
        this.list = list;
    }

    @SuppressWarnings("unchecked")
    public List<E> getList() {
        if(list == null){
            return Collections.EMPTY_LIST;
        }
        return Collections.unmodifiableList(list);
    }
}