package com.nowcoder.community.entity;

/**
 * 模型
 * 首页-上一页-页码-下一页-莫邪
 */
public class Page {
    //当前页码
    private int current = 1;
    //显示一页显示多少条数据
    private int limit = 10;
    //总数量
    private int rows;

    //首页
    private String path;
    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current>=1){
            this.current = current;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows>=0){
            this.rows = rows;
        }

    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit>=1 && limit<=100){
            this.limit = limit;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    //获取当前页的起始行
    public int startPage(){
        return (current - 1) * limit;
    }
    //获取总页数
    public int getTotal(){
        if (rows%limit==0){
            return rows/limit;
        }else {
            return rows/limit+1;
        }
    }

    //当前开始页码
    public int getFrom(){
        int start = current-2;
        return start<1?1:start;
    }

    //当前结束页码
    public int getTo(){
        int end = current+2;
        return end>rows?rows:end;
    }

}