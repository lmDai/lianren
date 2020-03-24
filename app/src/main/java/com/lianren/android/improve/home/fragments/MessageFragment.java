package com.lianren.android.improve.home.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.improve.base.fragments.BaseFragment;
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.MessageBean;
import com.lianren.android.improve.bean.MessageWrap;
import com.lianren.android.improve.bean.NoticeCountBean;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.explore.activities.InviteListActivity;
import com.lianren.android.improve.explore.activities.OrderListActivity;
import com.lianren.android.improve.home.activities.PairListActivity;
import com.lianren.android.improve.home.activities.SystemMessageActivity;
import com.lianren.android.improve.home.activities.UserSendMessageActivity;
import com.lianren.android.improve.home.adapter.MessageAdapter;
import com.lianren.android.improve.home.presenter.MessageContract;
import com.lianren.android.improve.home.presenter.MessagePresenter;
import com.lianren.android.improve.notice.NoticeManager;
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
 * @package: com.lianren.android.improve.home.fragments
 * @user:xhkj
 * @date:2019/12/18
 * @description:
 **/
public class MessageFragment extends BaseFragment implements MessageContract.View, OnLoadMoreListener, OnRefreshListener, NoticeManager.NoticeNotify {
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private MessageAdapter mAdapter;

    private MessageContract.Presenter mPresenter;
    private TextView tvPairMsg, tvSystemMsg, tvPairCount, tvSystemCount, tvUnreadInvite;
    private int page = 1;
    private LinearLayout llInvite, llOrder;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageWrap event) {
        if (event.code == Constants.REFRESH_CHAT) {
            page = 1;
            getData();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_message;
    }

    public static MessageFragment newInstance() {

        Bundle args = new Bundle();

        MessageFragment fragment = new MessageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private View getHeaderView() {
        View view = mInflater.inflate(R.layout.adapter_header_message, null, false);
        tvPairMsg = view.findViewById(R.id.tv_pair_msg);
        tvUnreadInvite = view.findViewById(R.id.tv_unread_invite);
        llInvite = view.findViewById(R.id.ll_invite);
        llOrder = view.findViewById(R.id.ll_order_list);
        tvSystemMsg = view.findViewById(R.id.tv_system_msg);
        tvPairCount = view.findViewById(R.id.tv_pair_count);
        tvSystemCount = view.findViewById(R.id.tv_system_count);
        view.findViewById(R.id.ll_order_list)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OrderListActivity.show(mContext);
                    }
                });
        view.findViewById(R.id.ll_system_message)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SystemMessageActivity.show(mContext);
                    }
                });
        view.findViewById(R.id.ll_pair).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PairListActivity.show(mContext);
            }
        });
        view.findViewById(R.id.ll_invite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InviteListActivity.show(mContext);
            }
        });
        return view;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        NoticeManager.bindNotify(this);
        mAdapter = new MessageAdapter();
        recyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter.bindToRecyclerView(recyclerview);
        mAdapter.addHeaderView(getHeaderView());
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.ll_content) {
                    MessageBean item = mAdapter.getItem(position);
                    UsersInfoBean.BaseBean msg = new UsersInfoBean.BaseBean();
                    msg.nickname = item.user.nickname;
                    msg.avatar_url = item.user.avatar_url;
                    msg.id = item.user.id;
                    msg.uuid = item.user.uuid;
                    UserSendMessageActivity.show(mContext, msg);
                    if (item.is_new != 0)
                        mAdapter.notifyItem(position);
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        EventBus.getDefault().register(this);
        mPresenter = new MessagePresenter(this);
        getData();
    }

    private void getData() {
        mPresenter.getNoticeCount();
        mPresenter.getChatList(page);
    }

    @Override
    public void showNoticeCount(NoticeCountBean countBean) {
        if (countBean != null) {
            if (countBean.pair != null) {
                tvPairMsg.setText(countBean.pair.msg);
                tvPairCount.setVisibility(countBean.pair.count > 0 ? View.VISIBLE : View.GONE);
            }
            if (countBean.system != null) {
                tvSystemMsg.setText(countBean.system.msg);
                tvSystemCount.setVisibility(countBean.system.count > 0 ? View.VISIBLE : View.GONE);
            }
            if (countBean.dating != null) {
                llInvite.setVisibility(countBean.dating.total_count > 0 ? View.VISIBLE : View.GONE);
                tvUnreadInvite.setVisibility(countBean.dating.count > 0 ? View.VISIBLE : View.GONE);
            }
            if (countBean.order != null) {
                llOrder.setVisibility(countBean.order.count > 0 ? View.VISIBLE : View.GONE);
            }
        }
    }

    @Override
    public void showChatList(List<MessageBean> data) {
        PageUtil.getSingleton().showPage(page, refreshLayout, mAdapter, data);
    }

    @Override
    public void setPresenter(MessageContract.Presenter presenter) {

    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        page++;
        mPresenter.getChatList(page);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        page = 1;
        getData();
    }

    @Override
    public void showNetworkError(int strId) {
        AppContext.showToast(strId);
    }

    @Override
    public void onNoticeArrived(NoticeCountBean countBean) {
        if (countBean != null && tvPairCount != null && tvPairMsg != null && tvSystemMsg != null && tvSystemCount != null) {
            if (countBean.pair != null) {
                tvPairMsg.setText(countBean.pair.msg);
                tvPairCount.setVisibility(countBean.pair.count > 0 ? View.VISIBLE : View.GONE);
            }
            if (countBean.system != null) {
                tvSystemMsg.setText(countBean.system.msg);
                tvSystemCount.setVisibility(countBean.system.count > 0 ? View.VISIBLE : View.GONE);
            }
            if (countBean.dating != null && llInvite != null) {
                llInvite.setVisibility(countBean.dating.total_count > 0 ? View.VISIBLE : View.GONE);
                if (tvUnreadInvite != null)
                    tvUnreadInvite.setVisibility(countBean.dating.count > 0 ? View.VISIBLE : View.GONE);
            }
            if (countBean.order != null && llOrder != null) {
                llOrder.setVisibility(countBean.order.count > 0 ? View.VISIBLE : View.GONE);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NoticeManager.unBindNotify(this);
        EventBus.getDefault().unregister(this);
    }
}
