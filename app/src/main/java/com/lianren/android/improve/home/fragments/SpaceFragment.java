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
import com.lianren.android.improve.bean.ShopBean;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.explore.activities.ShopDetailActivity;
import com.lianren.android.improve.explore.activities.ShopGoodsActivity;
import com.lianren.android.improve.home.adapter.ShopListAdapter;
import com.lianren.android.improve.home.presenter.SpaceListContract;
import com.lianren.android.improve.home.presenter.SpaceListPresenter;
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
 * @description:邀约空间
 **/
public class SpaceFragment extends BaseFragment implements
        SpaceListContract.EmptyView, SpaceListContract.View, OnLoadMoreListener, OnRefreshListener {
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @Bind(R.id.emptyLayout)
    EmptyLayout emptyLayout;
    private SpaceListContract.Presenter mPresenter;
    private int page = 1;
    private ShopListAdapter mAdapter;
    private UsersInfoBean.BaseBean mReceiver;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_space;
    }

    public static SpaceFragment newInstance(UsersInfoBean.BaseBean mReceiver) {
        Bundle args = new Bundle();
        SpaceFragment fragment = new SpaceFragment();
        args.putSerializable("receiver", mReceiver);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mAdapter = new ShopListAdapter();
        recyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter.bindToRecyclerView(recyclerview);
    }

    @Override
    protected void initData() {
        super.initData();
        mReceiver = (UsersInfoBean.BaseBean) getArguments().getSerializable("receiver");
        mPresenter = new SpaceListPresenter(this, this);
        mPresenter.getShopList(page);
        emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emptyLayout.getErrorState() != EmptyLayout.NETWORK_LOADING) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    mPresenter.getShopList(page);
                }
            }
        });
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ShopBean item = mAdapter.getItem(position);
                ShopDetailActivity.show(mContext, item.id, mReceiver);
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                ShopBean item = mAdapter.getItem(position);
                ShopGoodsActivity.show(mContext, item.id, 1, mReceiver);
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
    public void showShopList(List<ShopBean> mList) {
        PageUtil.getSingleton().showPage(page, refreshLayout, mAdapter, mList);
    }

    @Override
    public void setPresenter(SpaceListContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {
        AppContext.showToast(getString(strId));
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        page++;
        mPresenter.getShopList(page);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        page = 1;
        mPresenter.getShopList(page);
    }
}

