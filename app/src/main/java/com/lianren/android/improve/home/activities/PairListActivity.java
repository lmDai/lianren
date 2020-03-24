package com.lianren.android.improve.home.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.base.BaseRecyclerAdapter;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.PairListBean;
import com.lianren.android.improve.home.adapter.PairListAdapter;
import com.lianren.android.improve.home.presenter.PairListContract;
import com.lianren.android.improve.home.presenter.PairListPresenter;
import com.lianren.android.improve.user.activities.UserInfoActivity;
import com.lianren.android.ui.empty.EmptyLayout;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.Bind;

/**
 * @package: com.lianren.android.improve.home.activities
 * @user:xhkj
 * @date:2019/12/31
 * @description:用户匹配申请通知列表
 **/
public class PairListActivity extends BackActivity implements PairListContract.View, PairListContract.EmptyView,
        OnRefreshListener, OnLoadMoreListener {
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @Bind(R.id.emptyLayout)
    EmptyLayout emptyLayout;
    private PairListContract.Presenter mPresenter;
    private int page = 1;
    private PairListAdapter mAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_pair_list;
    }

    public static void show(Context mContext) {
        mContext.startActivity(new Intent(mContext, PairListActivity.class));
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        mAdapter = new PairListAdapter(this);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(mAdapter);
        mAdapter.setDeleteListener(new BaseRecyclerAdapter.OnViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (view.getParent() != null && view.getParent() instanceof SwipeMenuLayout) {
                    ((SwipeMenuLayout) view.getParent()).smoothClose();
                }
                PairListBean item = mAdapter.getItem(position);
                if (item == null) {
                    return;
                }
                mPresenter.dealPair(item, position, 2);
            }
        });
        mAdapter.setAcceptListener(new BaseRecyclerAdapter.OnViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (view.getParent() != null && view.getParent() instanceof SwipeMenuLayout) {
                    ((SwipeMenuLayout) view.getParent()).smoothClose();
                }
                PairListBean item = mAdapter.getItem(position);
                if (item == null) {
                    return;
                }
                if (item.status == 0)
                    mPresenter.dealPair(item, position, 1);
            }
        });
        mAdapter.setItemListener(new BaseRecyclerAdapter.OnViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                PairListBean item = mAdapter.getItem(position);
                UserInfoActivity.show(PairListActivity.this, Integer.parseInt(item.user.id), item.user.uuid);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter = new PairListPresenter(this, this);
        mPresenter.getPairList(page);
        emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emptyLayout.getErrorState() != EmptyLayout.NETWORK_LOADING) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    mPresenter.getPairList(page);
                }
            }
        });
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
    }

    @Override
    public void hideEmptyLayout() {
        if (emptyLayout != null)
            emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
    }

    @Override
    public void showErrorLayout(int errorType) {
        if (mAdapter != null && mAdapter.getCount() > 0) {
            return;
        }
        if (emptyLayout != null)
            emptyLayout.setErrorType(errorType);
    }

    @Override
    public void setPresenter(PairListContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {
        AppContext.showToast(getString(strId));
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        page++;
        mPresenter.getPairList(page);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        page = 1;
        mPresenter.getPairList(page);
    }

    @Override
    public void showPairs(List<PairListBean> mList) {
        if (page == 1) {
            refreshLayout.resetNoMoreData();
            mAdapter.resetItem(mList);
            if (mList != null || mList.size() == AppContext.PAGE_SIZE) {
                refreshLayout.finishRefresh();
            } else {
                refreshLayout.finishRefreshWithNoMoreData();
            }
        } else {
            if (mList != null && mList.size() > 0) {
                mAdapter.addAll(mList);
                refreshLayout.finishLoadMore();
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        }
    }

    @Override
    public void showDeleteSuccess(PairListBean tags, int position) {
        PairListBean item = mAdapter.getItem(position);
        if (item != null && tags.equals(item)) {
            mAdapter.removeItem(position);
        }
    }

    @Override
    public void showDeleteFailure(int strId) {
        AppContext.showToast(strId);
    }

    @Override
    public void showDeleteFailure(String strId) {
        AppContext.showToast(strId);
    }

    @Override
    public void showAcceptSuccess(PairListBean tags, int position) {
        PairListBean item = mAdapter.getItem(position);
        if (item != null && tags.equals(item)) {
            item.status = 1;
            mAdapter.notifyItemChanged(position);
        }
    }

}

