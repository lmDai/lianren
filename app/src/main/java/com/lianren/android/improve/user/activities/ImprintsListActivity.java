package com.lianren.android.improve.user.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.improve.account.constants.UserConstants;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.ImprintsBean;
import com.lianren.android.improve.bean.PickStatusBean;
import com.lianren.android.improve.explore.activities.ImageGalleryImprintActivity;
import com.lianren.android.improve.explore.activities.PublishImprintActivity;
import com.lianren.android.improve.explore.activities.SearchImprintActivity;
import com.lianren.android.improve.user.adapter.ImprintsListAdapter;
import com.lianren.android.improve.user.presenter.ImprintsListContract;
import com.lianren.android.improve.user.presenter.ImprintsListPresenter;
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
 * @date:2019/12/20
 * @description:印记列表
 **/
public class ImprintsListActivity extends BackActivity implements
        ImprintsListContract.EmptyView, ImprintsListContract.View, OnLoadMoreListener, OnRefreshListener {
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @Bind(R.id.emptyLayout)
    EmptyLayout emptyLayout;
    private ImprintsListContract.Presenter mPresenter;
    private int page = 1;
    private int user_id;
    private ImprintsListAdapter mAdapter;

    public static void show(Context mContext, int user_id) {
        Intent intent = new Intent();
        intent.putExtra(UserConstants.USER_ID, user_id);
        intent.setClass(mContext, ImprintsListActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_imprints_list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_publish, menu);
        return true;
    }

    private long mLastClickTime;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long nowTime = System.currentTimeMillis();
        if ((nowTime - mLastClickTime) < 500)
            return false;
        mLastClickTime = nowTime;
        if (item.getItemId() == R.id.menu_item_more) {
            PublishImprintActivity.show(this);
        }
        return false;
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
        user_id = getIntent().getIntExtra(UserConstants.USER_ID, -1);
        mPresenter = new ImprintsListPresenter(this, this);
        mPresenter.getImprintsList(user_id, page);
        emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emptyLayout.getErrorState() != EmptyLayout.NETWORK_LOADING) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    mPresenter.getImprintsList(user_id, page);
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
                    ImageGalleryImprintActivity.show(ImprintsListActivity.this, item);
                } else if (view.getId() == R.id.tv_content) {
                    ImprintsDetailActivity.show(ImprintsListActivity.this,item.user.id, item.id, "");
                } else if (view.getId() == R.id.ll_user) {
                    UserInfoActivity.show(ImprintsListActivity.this, item.user.id, item.user.uuid);
                }else if (view.getId()==R.id.ll_comment){
                    ImprintsDetailActivity.show(ImprintsListActivity.this, item.user.id,item.id, "",true);
                }
            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ImprintsBean item = mAdapter.getItem(position);
                ImprintsDetailActivity.show(ImprintsListActivity.this,item.user.id, item.id, "");
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
    public void showImprintsList(List<ImprintsBean> mList) {
        PageUtil.getSingleton().showPage(page, refreshLayout, mAdapter, mList);
    }

    @Override
    public void showPickResult(PickStatusBean bean, ImprintsBean imprintsBean, int position) {
        ImprintsBean item = mAdapter.getItem(position);
        if (item != null && imprintsBean.equals(item)) {
            mAdapter.notifyItem(bean, position);
        }
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
        mPresenter.getImprintsList(user_id, page);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        page = 1;
        mPresenter.getImprintsList(user_id, page);
    }
}
