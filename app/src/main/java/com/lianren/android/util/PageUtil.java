package com.lianren.android.util;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lianren.android.AppContext;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.List;

/**
 * @package: com.lianren.android.util
 * @user:xhkj
 * @date:2019/12/20
 * @description:
 **/
public class PageUtil {
    private volatile static PageUtil singleton;

    private PageUtil() {
    }

    public static PageUtil getSingleton() {
        if (singleton == null) {
            synchronized (PageUtil.class) {
                if (singleton == null) {
                    singleton = new PageUtil();
                }
            }
        }
        return singleton;
    }

    public void showPage(int page, SmartRefreshLayout refreshLayout, BaseQuickAdapter mAdapter, List mList) {
        if (page == 1) {
            refreshLayout.resetNoMoreData();
            mAdapter.setNewData(mList);
            if (mList != null || mList.size() == AppContext.PAGE_SIZE) {
                refreshLayout.finishRefresh();
            } else {
                refreshLayout.finishRefreshWithNoMoreData();
            }
        } else {
            if (mList != null && mList.size() > 0) {
                mAdapter.addData(mList);
                refreshLayout.finishLoadMore();
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        }
    }
}

