package com.lianren.android.improve.user.activities;

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
import com.lianren.android.improve.bean.ContactUserBean;
import com.lianren.android.improve.user.ContactLikeAdapter;
import com.lianren.android.improve.user.presenter.ContactLikeContract;
import com.lianren.android.improve.user.presenter.ContactLikePresenter;
import com.lianren.android.ui.empty.EmptyLayout;
import com.lianren.android.util.PageUtil;
import com.lianren.android.widget.RecycleViewDivider;
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
 * @description:喜欢/被喜欢列表
 **/
public class ContactLikeActivity extends BackActivity implements ContactLikeContract.View, ContactLikeContract.EmptyView,
        OnRefreshListener, OnLoadMoreListener {
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @Bind(R.id.emptyLayout)
    EmptyLayout emptyLayout;
    private ContactLikeContract.Presenter mPresenter;
    private int page = 1;
    private ContactLikeAdapter mAdapter;
    private int type;

    @Override
    protected int getContentView() {
        return R.layout.activity_contact_like;
    }

    public static void show(Context mContext, int type) {
        Intent intent = new Intent();
        intent.putExtra("type", type);
        intent.setClass(mContext, ContactLikeActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        mAdapter = new ContactLikeAdapter();
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL));
        recyclerview.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ContactUserBean item = mAdapter.getItem(position);
                UserInfoActivity.show(ContactLikeActivity.this, item.id, item.uuid);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        type = getIntent().getIntExtra("type", 0);
        if (type == 0) {
            setTitle("喜欢");
        } else {
            setTitle("被喜欢");
        }
        mPresenter = new ContactLikePresenter(this, this);
        mPresenter.getContactUser(page, type);
        emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emptyLayout.getErrorState() != EmptyLayout.NETWORK_LOADING) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    mPresenter.getContactUser(page, type);
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
        if (mAdapter != null && mAdapter.getItemCount() > 0) {
            return;
        }
        if (emptyLayout != null)
            emptyLayout.setErrorType(errorType);
    }

    @Override
    public void setPresenter(ContactLikeContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {
        AppContext.showToast(getString(strId));
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        page++;
        mPresenter.getContactUser(page, type);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        page = 1;
        mPresenter.getContactUser(page, type);
    }

    @Override
    public void showContactUser(List<ContactUserBean> mList) {
        PageUtil.getSingleton().showPage(page, refreshLayout, mAdapter, mList);
    }
}

