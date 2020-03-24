package com.lianren.android.improve.explore.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.OrderTicketBean;
import com.lianren.android.improve.explore.adapter.OrderTicketAdapter;
import com.lianren.android.improve.explore.presenter.OrderTicketContract;
import com.lianren.android.improve.explore.presenter.OrderTicketPresenter;
import com.lianren.android.util.TDevice;
import com.lianren.android.widget.RecycleViewDivider;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @package: com.lianren.android.improve.explore.activities
 * @user:xhkj
 * @date:2019/12/27
 * @description:订单票券
 **/
public class OrderTicketActivity extends BackActivity implements OrderTicketContract.View {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.recycler)
    RecyclerView recycler;
    @Bind(R.id.tv_nickname)
    TextView tvNickname;
    @Bind(R.id.tv_user_phone)
    TextView tvUserPhone;
    @Bind(R.id.tv_refund)
    TextView tvRefund;
    private OrderTicketContract.Presenter mPresenter;
    private String order_id;
    private OrderTicketAdapter mAdapter;

    public static void show(Context context, String order_id) {
        Intent intent = new Intent();
        intent.putExtra("order_id", order_id);
        intent.setClass(context, OrderTicketActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_order_ticket;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        recycler.setNestedScrollingEnabled(false);
        recycler.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL));
        recycler.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new OrderTicketAdapter();
        mAdapter.bindToRecyclerView(recycler);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.img_copy) {
                    TDevice.copyTextToBoard(mAdapter.getItem(position).no);
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        showLoadingDialog("加载中....");
        order_id = getIntent().getStringExtra("order_id");
        mPresenter = new OrderTicketPresenter(this);
        mPresenter.getOrderTicket(order_id);
    }

    @Override
    public void showOrderTicket(OrderTicketBean bean) {
        dismissLoadingDialog();
        tvName.setText(bean.subject.name);
        tvAddress.setText(bean.subject.address);
        tvTime.setText(bean.subject.time);
        tvNickname.setText(bean.user.nickname);
        tvUserPhone.setText(bean.user.telephone);
        mAdapter.setNewData(bean.tickets);
        tvRefund.setEnabled(bean.is_refund == 1);
        tvRefund.setTextColor(bean.is_refund == 1 ? ContextCompat.getColor(this, R.color.tab_selected_color) :
                ContextCompat.getColor(this, R.color.tab_user_normal));
    }

    @Override
    public void setPresenter(OrderTicketContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {
        AppContext.showToast(strId);
    }

    @OnClick(R.id.tv_refund)
    public void onViewClicked() {
        RefundTicketActivity.show(this, order_id);
    }
}

