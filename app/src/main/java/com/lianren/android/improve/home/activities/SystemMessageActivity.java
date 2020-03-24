package com.lianren.android.improve.home.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.NoticeSystemBean;
import com.lianren.android.improve.bean.SystemMessageBean;
import com.lianren.android.improve.explore.activities.EventDetailActivity;
import com.lianren.android.improve.home.adapter.SystemMessageAdapter;
import com.lianren.android.improve.home.presenter.MessageListContract;
import com.lianren.android.improve.home.presenter.MessageListPresenter;
import com.lianren.android.improve.main.WebActivity;
import com.lianren.android.improve.user.activities.UserInfoActivity;
import com.lianren.android.ui.empty.EmptyLayout;
import com.lianren.android.util.PageUtil;
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
 * @description:
 **/
public class SystemMessageActivity extends BackActivity implements MessageListContract.View, MessageListContract.EmptyView, OnRefreshListener, OnLoadMoreListener {
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @Bind(R.id.emptyLayout)
    EmptyLayout emptyLayout;
    private MessageListContract.Presenter mPresenter;
    private int page = 1;
    private SystemMessageAdapter mAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_system_message;
    }

    public static void show(Context mContext) {
        mContext.startActivity(new Intent(mContext, SystemMessageActivity.class));
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        mAdapter = new SystemMessageAdapter();
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.bindToRecyclerView(recyclerview);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter = new MessageListPresenter(this, this);
        mPresenter.getSystemMessage(page);
        emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emptyLayout.getErrorState() != EmptyLayout.NETWORK_LOADING) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    mPresenter.getSystemMessage(page);
                }
            }
        });
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SystemMessageBean item = mAdapter.getItem(position);
                mPresenter.noticeSystemView(item.id);
            }
        });
    }

    @Override
    public void showNoticeResult(NoticeSystemBean result) {
        if (result != null) {
            switch (result.type) {
                case 1://活动
                    EventDetailActivity.show(SystemMessageActivity.this, result.link);
                    break;
                case 2://文章
                    WebActivity.show(SystemMessageActivity.this, result.link);
                    break;
                case 3://用户消息
                    UserInfoActivity.show(SystemMessageActivity.this, Integer.parseInt(result.link), result.link);
                    break;
                case 4:
                    AppContext.showToast("无更多详情");
                    break;

            }
        }
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
    public void setPresenter(MessageListContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {
        AppContext.showToast(getString(strId));
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        page++;
        mPresenter.getSystemMessage(page);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        page = 1;
        mPresenter.getSystemMessage(page);
    }

    @Override
    public void showSystemMessage(List<SystemMessageBean> mList) {
        PageUtil.getSingleton().showPage(page, refreshLayout, mAdapter, mList);
    }
}

