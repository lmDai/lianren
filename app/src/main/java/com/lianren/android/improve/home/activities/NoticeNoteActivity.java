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
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.MessageWrap;
import com.lianren.android.improve.bean.NoticeNoteBean;
import com.lianren.android.improve.home.adapter.NoticeNoteAdapter;
import com.lianren.android.improve.home.presenter.NoticeNoteListContract;
import com.lianren.android.improve.home.presenter.NoticeNoteListPresenter;
import com.lianren.android.improve.user.activities.ImprintsDetailActivity;
import com.lianren.android.ui.empty.EmptyLayout;
import com.lianren.android.util.PageUtil;
import com.lianren.android.widget.RecycleViewDivider;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;

/**
 * @package: com.lianren.android.improve.home.activities
 * @user:xhkj
 * @date:2019/12/31
 * @description:
 **/
public class NoticeNoteActivity extends BackActivity implements NoticeNoteListContract.View, NoticeNoteListContract.EmptyView, OnRefreshListener, OnLoadMoreListener {
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @Bind(R.id.emptyLayout)
    EmptyLayout emptyLayout;
    private NoticeNoteListContract.Presenter mPresenter;
    private int page = 1;
    private NoticeNoteAdapter mAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_notice_note;
    }

    public static void show(Context mContext) {
        mContext.startActivity(new Intent(mContext, NoticeNoteActivity.class));
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        mAdapter = new NoticeNoteAdapter();
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL));
        mAdapter.bindToRecyclerView(recyclerview);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter = new NoticeNoteListPresenter(this, this);
        mPresenter.getNoticeNote(page);
        emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emptyLayout.getErrorState() != EmptyLayout.NETWORK_LOADING) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    mPresenter.getNoticeNote(page);
                }
            }
        });
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                NoticeNoteBean item = mAdapter.getItem(position);
                ImprintsDetailActivity.show(NoticeNoteActivity.this, item.user.id,item.note.id, item.comment_id);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new MessageWrap(Constants.REFRESH_NOTE_COUNT,null));
    }
    //    @Override
//    public void showNoticeResult(NoticeSystemBean result) {
//        if (result != null) {
//            switch (result.type) {
//                case 1://活动
//                    EventDetailActivity.show(NoticeNoteActivity.this, result.link);
//                    break;
//                case 2://文章
//                    WebActivity.show(NoticeNoteActivity.this, result.link);
//                    break;
//                case 3://用户消息
//                    UserInfoActivity.show(NoticeNoteActivity.this, Integer.parseInt(result.link), result.link);
//                    break;
//                case 4:
//                    break;
//
//            }
//        }
//    }

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
    public void setPresenter(NoticeNoteListContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {
        AppContext.showToast(getString(strId));
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        page++;
        mPresenter.getNoticeNote(page);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        page = 1;
        mPresenter.getNoticeNote(page);
    }

    @Override
    public void showSystemMessage(List<NoticeNoteBean> mList) {
        PageUtil.getSingleton().showPage(page, refreshLayout, mAdapter, mList);
    }
}

