package com.lianren.android.improve.explore.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.MessageWrap;
import com.lianren.android.improve.bean.TicketOrderDetailBean;
import com.lianren.android.improve.explore.adapter.OrderListAdapter;
import com.lianren.android.improve.explore.presenter.OrderListContract;
import com.lianren.android.improve.explore.presenter.OrderListPresenter;
import com.lianren.android.ui.empty.EmptyLayout;
import com.lianren.android.util.PageUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;

/**
 * @package: com.lianren.android.improve.explore.activities
 * @user:xhkj
 * @date:2019/12/30
 * @description:订单列表
 **/
public class OrderListActivity extends BackActivity implements OrderListContract.View, OrderListContract.EmptyView, OnRefreshListener, OnLoadMoreListener {
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @Bind(R.id.emptyLayout)
    EmptyLayout emptyLayout;
    private OrderListContract.Presenter mPresenter;
    private int page = 1;
    private OrderListAdapter mAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_order_list;
    }

    public static void show(Context mContext) {
        Intent intent = new Intent();
        intent.setClass(mContext, OrderListActivity.class);
        mContext.startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageWrap event) {
        if (event.code == Constants.REFRESH_INVITE_STATUS) {
            refreshLayout.autoRefresh();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        mAdapter = new OrderListAdapter();
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.bindToRecyclerView(recyclerview);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter = new OrderListPresenter(this, this);
        mPresenter.getOrderList(page);
        emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emptyLayout.getErrorState() != EmptyLayout.NETWORK_LOADING) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    mPresenter.getOrderList(page);
                }
            }
        });
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                TicketOrderDetailBean item = mAdapter.getItem(position);
                if (TextUtils.equals(item.order.type, "201")) {//活动
                    EventDetailActivity.show(OrderListActivity.this, item.subject.id + "");
                } else if (TextUtils.equals(item.order.type, "202")) {//邀约
                    InviteStatusActivity.show(OrderListActivity.this, item.subject.id);
                }
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                TicketOrderDetailBean item = mAdapter.getItem(position);
                if (view.getId() == R.id.tv_status) {
                    if (item.order.status == 0) {
                        ConfirmTicketActivity.show(OrderListActivity.this, Integer.parseInt(mAdapter.getItem(position).order.id));
                    } else if (item.order.status == 1) {
                        OrderTicketActivity.show(OrderListActivity.this, mAdapter.getItem(position).order.id);
                    }
                }
            }
        });
    }

    @Override
    public void showOrderList(List<TicketOrderDetailBean> mList) {
        PageUtil.getSingleton().showPage(page, refreshLayout, mAdapter, mList);
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
    public void setPresenter(OrderListContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {
        AppContext.showToast(getString(strId));
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        page++;
        mPresenter.getOrderList(page);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        page = 1;
        mPresenter.getOrderList(page);
    }
}
