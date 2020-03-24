package com.lianren.android.improve.home.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.improve.base.fragments.BaseFragment;
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.MatchingBean;
import com.lianren.android.improve.bean.MessageWrap;
import com.lianren.android.improve.home.adapter.MatchingsAdapter;
import com.lianren.android.improve.home.presenter.MatchingContract;
import com.lianren.android.improve.home.presenter.MatchingPresenter;
import com.lianren.android.improve.user.activities.UserInfoActivity;
import com.lianren.android.ui.empty.EmptyLayout;
import com.lianren.android.util.DialogHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;

/**
 * @package: com.lianren.android.improve.home.fragments
 * @user:xhkj
 * @date:2019/12/18
 * @description:匹配
 **/
public class MatchingFragment extends BaseFragment implements MatchingContract.View, MatchingContract.EmptyView {
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @Bind(R.id.emptyLayout)
    EmptyLayout emptyLayout;
    private MatchingContract.Presenter mPresenter;
    private MatchingsAdapter mAdapter;
    public static final String ACTION_NEXT_PAGE = "com.lianren.android.improve.home.fragments.next";

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_matching;
    }

    public static MatchingFragment newInstance() {
        Bundle args = new Bundle();
        MatchingFragment fragment = new MatchingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mAdapter = new MatchingsAdapter();
        recyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter.bindToRecyclerView(recyclerview);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageWrap event) {
        if (event.code == Constants.REFRESH_MATCH) {
            mPresenter.getMatchings();
        }
    }

    @Override
    protected void initData() {
        super.initData();
        EventBus.getDefault().register(this);
        mPresenter = new MatchingPresenter(this, this);
        requestLocation();
        emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emptyLayout.getErrorState() != EmptyLayout.NETWORK_LOADING) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    mPresenter.getMatchings();
                }
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.getMatchings();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(800);

            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MatchingBean.UserBean userBean = mAdapter.getItem(position);
                mAdapter.itemView(position);
                UserInfoActivity.show(mContext, userBean.remote.id, userBean.remote.uuid);
            }
        });
//        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
//                refreshLayout.getLayout().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent broadcast = new Intent(ACTION_NEXT_PAGE);
//                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(broadcast);
//                        refreshLayout.finishLoadMore();
//                    }
//                }, 2000);
//            }
//        });
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                MatchingBean.UserBean item = mAdapter.getItem(position);
                showDisLikeDialog(item, position);
                return false;
            }
        });
    }

    private void showDisLikeDialog(final MatchingBean.UserBean item, final int position) {
        DialogHelper.getConfirmDialog(mContext, "提示", "是否关闭显示?", "确认", "取消", false, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showLoadingDialog();
                mPresenter.pairsDisLike(item, position);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }

    @Override
    public void showMatchings(MatchingBean mList) {
        if (mList != null) {
            mAdapter.setNewData(mList.user);
        }
        refreshLayout.finishRefresh();
    }

    @Override
    public void showDeleteSuccess(MatchingBean.UserBean tags, int position) {
        dismissLoadingDialog();
        mAdapter.remove(position);
    }

    @Override
    public void showDeleteFaile(String message) {
        dismissLoadingDialog();
        AppContext.showToast(message);
    }

    @Override
    public void setPresenter(MatchingContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {
        AppContext.showToast(getString(strId));
        refreshLayout.finishRefresh(false);
    }

    public void requestLocation() {
        mPresenter.getMatchings();

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
}
