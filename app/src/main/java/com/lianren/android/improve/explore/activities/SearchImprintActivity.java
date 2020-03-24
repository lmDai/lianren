package com.lianren.android.improve.explore.activities;

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
import com.lianren.android.improve.bean.ImprintsBean;
import com.lianren.android.improve.bean.PickStatusBean;
import com.lianren.android.improve.explore.presenter.ImprintsListContract;
import com.lianren.android.improve.explore.presenter.ImprintsListPresenter;
import com.lianren.android.improve.user.activities.ImprintsDetailActivity;
import com.lianren.android.improve.user.activities.UserInfoActivity;
import com.lianren.android.improve.user.adapter.ImprintsListAdapter;
import com.lianren.android.ui.empty.EmptyLayout;
import com.lianren.android.util.PageUtil;
import com.lianren.android.widget.RecycleViewDivider;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.Serializable;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @package: com.lianren.android.improve.user.activities
 * @user:xhkj
 * @date:2019/12/20
 * @description:搜索印记
 **/
public class SearchImprintActivity extends BackActivity implements
        ImprintsListContract.EmptyView, ImprintsListContract.View, OnLoadMoreListener, OnRefreshListener {
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @Bind(R.id.emptyLayout)
    EmptyLayout emptyLayout;
    private ImprintsListContract.Presenter mPresenter;
    private int page = 1;
    private List<String> tag;
    private ImprintsListAdapter mAdapter;

    public static void show(Context mContext, List<String> tag) {
        Intent intent = new Intent();
        intent.putExtra("tag", (Serializable) tag);
        intent.setClass(mContext, SearchImprintActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_search_imprints;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        mAdapter = new ImprintsListAdapter();
        recyclerview.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL));
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.bindToRecyclerView(recyclerview);
    }

    @Override
    protected void initData() {
        super.initData();
        tag = (List<String>) getIntent().getSerializableExtra("tag");
        if (tag != null && tag.size() > 0)
            setTitle(tag.get(0));
        mPresenter = new ImprintsListPresenter(this, this);
        mPresenter.getImprintsList(tag, page);
        emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emptyLayout.getErrorState() != EmptyLayout.NETWORK_LOADING) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    mPresenter.getImprintsList(tag, page);
                }
            }
        });
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                ImprintsBean item = mAdapter.getItem(position);
                 if (view.getId() == R.id.ll_pick) {//点赞
                    mPresenter.notiPick(item, position);
                } else if (view.getId() == R.id.page_number_point) {
                    ImageGalleryImprintActivity.show(SearchImprintActivity.this, item);
                } else if (view.getId() == R.id.tv_content) {
                    ImprintsDetailActivity.show(SearchImprintActivity.this, item.user.id,item.id, "");
                }else if (view.getId()==R.id.ll_user){
                    UserInfoActivity.show(SearchImprintActivity.this,item.user.id,item.user.uuid);
                }else if (view.getId()==R.id.ll_comment){
                     ImprintsDetailActivity.show(SearchImprintActivity.this, item.user.id,item.id, "",true);
                 }
            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ImprintsBean item = mAdapter.getItem(position);
                ImprintsDetailActivity.show(SearchImprintActivity.this,item.user.id, item.id, "");
            }
        });
    }

    @Override
    public void hideEmptyLayout() {
        if (emptyLayout != null)
            emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
    }
    @Override
    public void showPickResult(PickStatusBean bean, ImprintsBean imprintsBean, int position) {
        ImprintsBean item = mAdapter.getItem(position);
        if (item != null && imprintsBean.equals(item)) {
            mAdapter.notifyItem(bean,position);
        }
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
    public void showImprintsList(List<ImprintsBean> mList) {
        PageUtil.getSingleton().showPage(page, refreshLayout, mAdapter, mList);
    }

    @Override
    public void setPresenter(ImprintsListContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {
        AppContext.showToast(getString(strId));
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        page++;
        mPresenter.getImprintsList(tag, page);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        page = 1;
        mPresenter.getImprintsList(tag, page);
    }


    @OnClick(R.id.ll_join)
    public void onViewClicked() {
        PublishImprintActivity.show(this, null, null, tag,1,"");
    }
}
