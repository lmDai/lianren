package com.lianren.android.improve.user.activities;

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
import com.lianren.android.improve.bean.ContactUserBean;
import com.lianren.android.improve.user.adapter.ShiledUserAdapter;
import com.lianren.android.improve.user.presenter.ShiledListContract;
import com.lianren.android.improve.user.presenter.ShiledListPresenter;
import com.lianren.android.ui.empty.EmptyLayout;
import com.lianren.android.widget.RecycleViewDivider;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.Bind;

/**
 * @package: com.lianren.android.improve.user.activities
 * @user:xhkj
 * @date:2019/12/24
 * @description:屏蔽名单
 **/
public class ShiledActivity extends BackActivity implements ShiledListContract.View, ShiledListContract.EmptyView,
        OnRefreshListener, OnLoadMoreListener {
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @Bind(R.id.emptyLayout)
    EmptyLayout emptyLayout;
    private ShiledListContract.Presenter mPresenter;
    private int page = 1;
    private ShiledUserAdapter mAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_user_shiled;
    }

    public static void show(Context mContext) {
        mContext.startActivity(new Intent(mContext, ShiledActivity.class));
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        mAdapter = new ShiledUserAdapter(this);

        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL));
        recyclerview.setAdapter(mAdapter);
        mAdapter.setDeleteListener(new BaseRecyclerAdapter.OnViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (view.getParent() != null && view.getParent() instanceof SwipeMenuLayout) {
                    ((SwipeMenuLayout) view.getParent()).smoothClose();
                }
                ContactUserBean item = mAdapter.getItem(position);
                if (item == null) {
                    return;
                }
                mPresenter.dealBlackRevert(item, position);
            }
        });
        mAdapter.setItemListener(new BaseRecyclerAdapter.OnViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                ContactUserBean item = mAdapter.getItem(position);
                UserInfoActivity.show(ShiledActivity.this, item.id, item.uuid);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter = new ShiledListPresenter(this, this);
        mPresenter.contactBlackList(page);
        emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emptyLayout.getErrorState() != EmptyLayout.NETWORK_LOADING) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    mPresenter.contactBlackList(page);
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
    public void setPresenter(ShiledListContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {
        AppContext.showToast(getString(strId));
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        page++;
        mPresenter.contactBlackList(page);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        page = 1;
        mPresenter.contactBlackList(page);
    }

    @Override
    public void showContactUser(List<ContactUserBean> mList) {
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
    public void showDeleteSuccess(ContactUserBean tags, int position) {

        ContactUserBean item = mAdapter.getItem(position);
        if (item != null && tags.equals(item)) {
            mAdapter.removeItem(position);
            if (mAdapter.getCount() == 0) {
                showErrorLayout(EmptyLayout.NODATA);
            }
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

}

