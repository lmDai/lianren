package com.lianren.android.improve.explore.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.EventTicketBean;
import com.lianren.android.improve.bean.TicketOrderBean;
import com.lianren.android.improve.bean.TicketOrderDetailBean;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.improve.explore.adapter.EventTicketAdapter;
import com.lianren.android.improve.explore.presenter.EventPurchseTicketContract;
import com.lianren.android.improve.explore.presenter.EventPurchseTicketPresenter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @package: com.lianren.android.improve.explore.activities
 * @user:xhkj
 * @date:2019/12/19
 * @description:活动购票
 **/
public class EventPurchseTicketActivity extends TicketBaseActivity implements EventPurchseTicketContract.View {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.recycler_ticket)
    RecyclerView recyclerTicket;
    @Bind(R.id.tv_select_time)
    TextView tvSelectTime;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @Bind(R.id.txt_total)
    TextView txtTotal;
    @Bind(R.id.btn_status)
    Button btnStatus;
    private EventPurchseTicketContract.Presenter mPresenter;
    private String activity_id;
    private int source;
    private EventTicketAdapter mAdapter;

    public static void show(Context context, String activity_id, int source) {
        Intent intent = new Intent();
        intent.putExtra("activity_id", activity_id);
        intent.putExtra("source", source);
        intent.setClass(context, EventPurchseTicketActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_event_purchse_ticket;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        mAdapter = new EventTicketAdapter();
        recyclerTicket.setLayoutManager(new LinearLayoutManager(this));
        recyclerTicket.setNestedScrollingEnabled(false);
        mAdapter.bindToRecyclerView(recyclerTicket);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mAdapter.setChecked(position);
                setSelectGoods(mAdapter.getItem(position));
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                //limit_num 购买限制数量 -1不限制 大于0限制
                EventTicketBean.GoodsBean item = mAdapter.getItem(position);
                if (view.getId() == R.id.img_delete) {
                    if (item.purchaseNum <= 1) {
                        return;
                    }
                    mAdapter.notifyItem(position, --item.purchaseNum);
                } else if (view.getId() == R.id.img_add) {
                    if (item.limit_num == -1) {
                        if (item.purchaseNum >= item.leave_num) {
                            AppContext.showToast("剩余数量不足");
                            return;
                        }
                    }
                    if (item.limit_num > 0)
                        if (item.purchaseNum == item.limit_num) {
                            AppContext.showToast("购买数量已达上线");
                            return;
                        }
                    mAdapter.notifyItem(position, ++item.purchaseNum);
                }
                setSelectGoods(mAdapter.getChecked());
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        showLoadingDialog("加载中....");
        activity_id = getIntent().getStringExtra("activity_id");
        source = getIntent().getIntExtra("source", -1);
        mPresenter = new EventPurchseTicketPresenter(this);
        mPresenter.getActivityGood(activity_id, source);
    }

    @Override
    public void showEventGood(EventTicketBean detailBean) {
        dismissLoadingDialog();
        tvName.setText(detailBean.name);
        tvAddress.setText(detailBean.address);
        for (int i = 0; i < detailBean.goods.size(); i++) {
            detailBean.goods.get(i).checked = i == 0;
            detailBean.goods.get(i).purchaseNum = 1;
        }
        mAdapter.setNewData(detailBean.goods);
        setSelectGoods(detailBean.goods.get(0));
    }

    @Override
    public void showResult(ResultBean<TicketOrderDetailBean> orderDetailBean) {
        dismissLoadingDialog();
        if (orderDetailBean.isSuccess()) {
            ConfirmTicketActivity.show(this, orderDetailBean.data);
        } else {
            AppContext.showToast(orderDetailBean.error.message);
        }
    }

    private void setSelectGoods(EventTicketBean.GoodsBean goodsBean) {
        txtTotal.setText("¥" + goodsBean.price * goodsBean.purchaseNum);
        tvSelectTime.setText("票券时间" + goodsBean.time);
    }

    @Override
    public void setPresenter(EventPurchseTicketContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {
        dismissLoadingDialog();
        AppContext.showToast(strId);
    }

    private long mLastClickTime;

    @OnClick(R.id.btn_status)
    public void onViewClicked() {//提交订单
        // 用来解决快速点击多个按钮弹出多个界面的情况
        long nowTime = System.currentTimeMillis();
        if ((nowTime - mLastClickTime) < 500)
            return;
        mLastClickTime = nowTime;
        createOrder();
    }

    private void createOrder() {
        if (mAdapter.getChecked() == null) return;
        showLoadingDialog("正在生成订单...");
        List<TicketOrderBean> ticketOrderBeans = new ArrayList<>();
        ticketOrderBeans.add(new TicketOrderBean(mAdapter.getChecked().id, mAdapter.getChecked().purchaseNum));
        mPresenter.createOrder(201, activity_id, ticketOrderBeans);
    }
}
