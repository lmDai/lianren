package com.lianren.android.improve.user.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.EventBean;
import com.lianren.android.improve.explore.activities.EventDetailActivity;
import com.lianren.android.improve.explore.presenter.CollectEventListContract;
import com.lianren.android.improve.explore.presenter.CollectEventListPresenter;
import com.lianren.android.improve.user.adapter.CollectEventListAdapter;
import com.lianren.android.ui.empty.EmptyLayout;
import com.lianren.android.util.DialogHelper;
import com.lianren.android.util.PageUtil;
import com.lianren.android.widget.RecycleViewDivider;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * @package: com.lianren.android.improve.explore.activities
 * @user:xhkj
 * @date:2019/12/19
 * @description:活动列表
 **/
public class CollectEventActivity extends BackActivity implements CollectEventListContract.View, CollectEventListContract.EmptyView, OnRefreshListener, OnLoadMoreListener {
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @Bind(R.id.emptyLayout)
    EmptyLayout emptyLayout;
    private int page = 1;
    private int type = 1;
    private CollectEventListContract.Presenter mPresenter;
    private CollectEventListAdapter mAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_event;
    }

    public static void show(Context mContext, int type) {
        Intent intent = new Intent();
        intent.putExtra("type", type);
        intent.setClass(mContext, CollectEventActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        mAdapter = new CollectEventListAdapter();
        recyclerview.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL));
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.bindToRecyclerView(recyclerview);
    }

    @Override
    protected void initData() {
        super.initData();
        type = getIntent().getIntExtra("type", 1);
        mPresenter = new CollectEventListPresenter(this, this);
        mPresenter.getActivityFind(type, page);
        emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emptyLayout.getErrorState() != EmptyLayout.NETWORK_LOADING) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    mPresenter.getActivityFind(type, page);
                }
            }
        });
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                EventBean item = mAdapter.getItem(position);
                EventDetailActivity.show(CollectEventActivity.this, item.id);
            }
        });
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, final int position) {
                List<String> operators = new ArrayList<>();
                operators.add("取消收藏");
                final String[] os = new String[operators.size()];
                operators.toArray(os);
                DialogHelper.getConfirmDialog(CollectEventActivity.this, "提示", "是否取消收藏?", "确认", "取消", false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.activityCollect(mAdapter.getItem(position), position);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
                return false;
            }
        });
    }

    @Override
    public void hideEmptyLayout() {
        if (emptyLayout != null)
            emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
    }

    @Override
    public void showErrorLayout(int errorType) {
        if (mAdapter != null && mAdapter.getData().size() > 0) {
            return;
        }
        if (emptyLayout != null)
            emptyLayout.setErrorType(errorType);
    }

    @Override
    public void setPresenter(CollectEventListContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {
        AppContext.showToast(getString(strId));
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        page++;
        mPresenter.getActivityFind(type, page);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        page = 1;
        mPresenter.getActivityFind(type, page);
    }

    @Override
    public void showEventList(List<EventBean> mList) {
        PageUtil.getSingleton().showPage(page, refreshLayout, mAdapter, mList);
    }

    @Override
    public void showCollect(EventBean tags, int position) {
        EventBean item = mAdapter.getItem(position);
        if (tags.equals(item)) {
            mAdapter.remove(position);
            if (mAdapter.getData().size() == 0) {
                showErrorLayout(EmptyLayout.NODATA);
            }
        }
    }
}

