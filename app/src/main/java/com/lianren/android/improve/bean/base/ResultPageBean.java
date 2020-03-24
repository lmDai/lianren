package com.lianren.android.improve.bean.base;

import java.util.List;

/**
 * @package: com.lianren.android.improve.bean.base
 * @user:xhkj
 * @date:2019/12/20
 * @description:
 **/
public class ResultPageBean<T> {
    private static final int RESULT_SUCCESS = 0;
    public DataBean<T> data;
    public int code;
    public ErrorBean error;
    private String nextPageToken;
    private String prevPageToken;

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public String getPrevPageToken() {
        return prevPageToken;
    }

    public void setPrevPageToken(String prevPageToken) {
        this.prevPageToken = prevPageToken;
    }

    public static class DataBean<T> {
        public int page;
        public int total_num;
        public int total_page;
        public int first_index;
        public List<T> items;

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getTotal_num() {
            return total_num;
        }

        public void setTotal_num(int total_num) {
            this.total_num = total_num;
        }

        public int getTotal_page() {
            return total_page;
        }

        public void setTotal_page(int total_page) {
            this.total_page = total_page;
        }

        public int getFirst_index() {
            return first_index;
        }

        public void setFirst_index(int first_index) {
            this.first_index = first_index;
        }

        public List<T> getItems() {
            return items;
        }

        public void setItems(List<T> items) {
            this.items = items;
        }
    }

    public boolean isSuccess() {
        return code == RESULT_SUCCESS && data != null;
    }
}
