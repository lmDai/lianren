package com.lianren.android.improve.home.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.improve.base.fragments.BaseFragment;
import com.lianren.android.improve.bean.EventBean;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.explore.activities.EventDetailActivity;
import com.lianren.android.improve.explore.activities.EventGoodsActivity;
import com.lianren.android.improve.home.adapter.EventListAdapter;
import com.lianren.android.improve.home.presenter.EventListContract;
import com.lianren.android.improve.home.presenter.EventListPresenter;
import com.lianren.android.ui.empty.EmptyLayout;
import com.lianren.android.util.PageUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.Bind;

/**
 * @package: com.lianren.android.improve.home.fragments
 * @user:xhkj
 * @date:2019/12/31
 * @description:
 **/
public class EventFragment extends BaseFragment implements
        EventListContract.EmptyView, EventListContract.View, OnLoadMoreListener, OnRefreshListener {
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @Bind(R.id.emptyLayout)
    EmptyLayout emptyLayout;
    private EventListContract.Presenter mPresenter;
    private int page = 1;
    private EventListAdapter mAdapter;
    private UsersInfoBean.BaseBean mReceiver;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_space;
    }

    public static EventFragment newInstance(UsersInfoBean.BaseBean mReceiver) {
        Bundle args = new Bundle();
        EventFragment fragment = new EventFragment();
        args.putSerializable("receiver", mReceiver);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mAdapter = new EventListAdapter();
        recyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter.bindToRecyclerView(recyclerview);
    }

    @Override
    protected void initData() {
        super.initData();
        mReceiver = (UsersInfoBean.BaseBean) getArguments().getSerializable("receiver");
        mPresenter = new EventListPresenter(this, this);
        mPresenter.getEventList(page);
        emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emptyLayout.getErrorState() != EmptyLayout.NETWORK_LOADING) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    mPresenter.getEventList(page);
                }
            }
        });
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                EventBean item = mAdapter.getItem(position);
                EventDetailActivity.show(mContext, item.id, mReceiver);
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                EventBean item = mAdapter.getItem(position);
                EventGoodsActivity.show(mContext, Integer.parseInt(item.id), 1, mReceiver);
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
    public void showEventList(List<EventBean> mList) {
        PageUtil.getSingleton().showPage(page, refreshLayout, mAdapter, mList);
    }

    @Override
    public void setPresenter(EventListContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {
        AppContext.showToast(getString(strId));
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        page++;
        mPresenter.getEventList(page);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        page = 1;
        mPresenter.getEventList(page);
    }
}

