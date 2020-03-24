package com.lianren.android.improve.explore.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.base.BaseDialog;
import com.lianren.android.base.BaseDialogFragment;
import com.lianren.android.improve.account.AccountHelper;
import com.lianren.android.improve.account.constants.UserConstants;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.DatingDetailBean;
import com.lianren.android.improve.bean.MessageWrap;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.improve.explore.presenter.InviteStatusContract;
import com.lianren.android.improve.explore.presenter.InviteStatusPresenter;
import com.lianren.android.improve.feedback.FeedTypeActivity;
import com.lianren.android.improve.user.activities.UserInfoActivity;
import com.lianren.android.util.ImageLoader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @package: com.lianren.android.improve.explore.activities
 * @user:xhkj
 * @date:2019/12/19
 * @description:邀约状态
 **/
public class InviteStatusActivity extends BackActivity implements InviteStatusContract.View {
    @Bind(R.id.img_avater)
    CircleImageView imgAvater;
    @Bind(R.id.tv_nickname)
    TextView tvNickname;
    @Bind(R.id.tv_status)
    TextView tvStatus;
    @Bind(R.id.tv_count_time)
    TextView tvCountTime;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_location)
    TextView tvLocation;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.tv_ticket_name)
    TextView tvTicketName;
    @Bind(R.id.tv_left)
    TextView tvLeft;
    @Bind(R.id.btn_right)
    Button btnRight;
    @Bind(R.id.tv_ticket_time)
    TextView tvTicketTime;
    @Bind(R.id.tv_discount)
    TextView tvDiscount;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private int dating_id;
    private InviteStatusContract.Presenter mPresenter;
    private DatingDetailBean shopBean;

    public static void show(Context context, int dating_id) {
        Intent intent = new Intent();
        intent.putExtra("dating_id", dating_id);
        intent.setClass(context, InviteStatusActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected int getContentView() {
        return R.layout.activity_invite_status;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
    }

    @Override
    protected void initData() {
        super.initData();
        showLoadingDialog("加载中....");
        dating_id = getIntent().getIntExtra("dating_id", -1);
        mPresenter = new InviteStatusPresenter(this);
        mPresenter.getDatingDetail(dating_id);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.getDatingDetail(dating_id);
            }
        });
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageWrap event) {
        if (event.code == Constants.REFRESH_INVITE_STATUS) {
            mPresenter.getDatingDetail(dating_id);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_more:
                showDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showDialog() {
        new BaseDialogFragment.Builder(this)
                .setContentView(R.layout.view_event_detail_operator)
                .setVisibility(R.id.tv_share, View.GONE)
                .setVisibility(R.id.view_line, View.GONE)
                .setAnimStyle(BaseDialog.AnimStyle.BOTTOM)
                .setWidth(LinearLayout.LayoutParams.MATCH_PARENT)
                .setGravity(Gravity.BOTTOM)
                .setOnClickListener(R.id.tv_cancel_opt, new BaseDialog.OnClickListener() {
                    @Override
                    public void onClick(BaseDialog dialog, View view) {
                        dialog.dismiss();
                    }
                }).setOnClickListener(R.id.tv_feed_back_opt, new BaseDialog.OnClickListener() {
            @Override
            public void onClick(BaseDialog dialog, View view) {
                FeedTypeActivity.show(InviteStatusActivity.this, UserConstants.FEED_INVITE, dating_id + "");
                dialog.dismiss();
            }
        })
                .show();
    }

    @Override
    public void showDatingDetail(DatingDetailBean shopBean) {
        dismissLoadingDialog();
        refreshLayout.finishRefresh();
        this.shopBean = shopBean;
        if (!TextUtils.isEmpty(shopBean.subject.good_time)) {
            tvTicketTime.setVisibility(View.VISIBLE);
            tvTicketTime.setText("票券 " + shopBean.subject.good_time);
        } else {
            tvTicketTime.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(shopBean.subject.discount)) {
            tvDiscount.setVisibility(View.VISIBLE);
            tvDiscount.setText(shopBean.subject.discount);
        } else {
            tvDiscount.setVisibility(View.GONE);
        }

        if (shopBean.operators.size() == 2) {
            tvLeft.setText(shopBean.operators.get(0).name);
            btnRight.setText(shopBean.operators.get(1).name);
            btnRight.setVisibility(View.VISIBLE);
            tvLeft.setVisibility(View.VISIBLE);
        } else if (shopBean.operators.size() == 1) {
            tvLeft.setText(shopBean.operators.get(0).name);
            btnRight.setVisibility(View.GONE);
        } else if (shopBean.operators.size() == 0) {
            tvLeft.setVisibility(View.GONE);
            btnRight.setVisibility(View.GONE);
        }
        //邀约场所信息
        tvTitle.setText(shopBean.subject.name);
        tvLocation.setText(shopBean.subject.address);
        tvTime.setText("邀约 " + shopBean.subject.time);
        tvTicketName.setText(shopBean.subject.good_name + "   ¥" + shopBean.subject.price + " x 1 ");
        if (shopBean.present_user.id == AccountHelper.getUserId()) {//发起人
            ImageLoader.loadImage(getImageLoader(), imgAvater, shopBean.remote_user.avatar_url);
            tvNickname.setText(shopBean.remote_user.nickname);
        } else {
            ImageLoader.loadImage(getImageLoader(), imgAvater, shopBean.present_user.avatar_url);
            tvNickname.setText(shopBean.present_user.nickname);
        }
        String status = "";
        if (shopBean.present_user.id == AccountHelper.getUserId()) {//发起人
            switch (shopBean.present_status) {
                case 0:
                    status = "已发出邀约，待对方接受";
                    break;
                case 100:
                    status = "对方已接受，请您购票";
                    break;
                case 101:
                    status = "已购票";
                    if (shopBean.remote_status != 101) {
                        status = "您已购票，待对方购票";
                    }
                    break;
                case 102:
                    status = "已取消";
                    break;
                case 103:
                    status = "购票超时取消";
                    break;
                case 200:
                    status = "双方已购票，请按时参加约会";
                    break;
                case 201:
                    status = "已约见";
                    break;
            }
        } else {//受邀人
            switch (shopBean.remote_status) {
                case 0:
                    status = "待接受";
                    break;
                case 2:
                    status = "已拒绝";
                    break;
                case 100:
                    status = "待购票";
                    break;
                case 101:
                    status = "已购票";
                    if (shopBean.present_status != 101) {
                        status = "您已购票，待对方购票";
                    }
                    break;
                case 102:
                    status = "已取消";
                    break;
                case 103:
                    status = "购票超时取消";
                    break;
                case 200:
                    status = "双方已购票，请按时参加约会";
                    break;
                case 201:
                    status = "已约见";
                    break;
            }
        }
        tvStatus.setText(status + "");
        new CountDownTimer(shopBean.pay_time * 1000, 1000) {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTick(long millisUntilFinished) {
                tvCountTime.setText(Integer.parseInt(String.valueOf((millisUntilFinished / 1000) / 60)) + "'");
            }

            @Override
            public void onFinish() {
                tvCountTime.setText("");
            }
        }.start();

    }


    @Override
    public void showResult(ResultBean resultBean) {
        dismissLoadingDialog();
        if (resultBean.isSuccess()) {
            AppContext.showToast("成功");
            EventBus.getDefault().post(new MessageWrap(Constants.REFRESH_INVITE_STATUS_LIST, null));
            refreshLayout.autoRefresh();
        } else {
            AppContext.showToast(resultBean.error.message);
        }
    }


    @Override
    public void setPresenter(InviteStatusContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {
        dismissLoadingDialog();
        AppContext.showToast(strId);
        refreshLayout.finishRefresh();
    }

    private long mLastClickTime;

    @OnClick({R.id.tv_left, R.id.btn_right, R.id.img_avater, R.id.tv_nickname, R.id.ll_detail})
    public void onViewClicked(View view) {
        long nowTime = System.currentTimeMillis();
        if ((nowTime - mLastClickTime) < 500)
            return;
        mLastClickTime = nowTime;
        // deal(7.1.2 处理邀约请求) cancel(7.1.4 取消邀约) pay(5.2.2 订单支付) order_cancel(5.3.6 取消订单) meet(5.3.5 订单票券)
        switch (view.getId()) {
            case R.id.tv_left:
                operators(0, shopBean.operators.get(0));
                break;
            case R.id.btn_right:
                operators(1, shopBean.operators.get(1));
                break;
            case R.id.img_avater:
            case R.id.tv_nickname:
                if (shopBean.present_user.id == AccountHelper.getUserId()) {//发起人
                    UserInfoActivity.show(InviteStatusActivity.this, shopBean.remote_user.id, shopBean.remote_user.uuid);
                } else {
                    UserInfoActivity.show(InviteStatusActivity.this, shopBean.present_user.id, shopBean.present_user.uuid);
                }
                break;
            case R.id.ll_detail:
                //1活动 2空间
                if (shopBean.type == 1) {
                    EventDetailActivity.show(this, shopBean.subject.id + "");
                } else if (shopBean.type == 2) {
                    ShopDetailActivity.show(this, shopBean.subject.id, -2);
                }

                break;
        }
    }

    //用户操作类型
    private void operators(int position, DatingDetailBean.OperatorsBean operatorsBean) {
        int order_id;
        if (shopBean.present_user.id == AccountHelper.getUserId()) {//发起人
            order_id = shopBean.present_order_id;
        } else {
            order_id = shopBean.remote_order_id;
        }
        switch (operatorsBean.type) {
            case "deal"://处理邀约请求
                mPresenter.datingRequestDeal(dating_id, position == 0 ? 2 : 1);
                break;
            case "cancel"://取消邀约
                mPresenter.datingCancel(dating_id);
                break;
            case "pay"://订单支付
                ConfirmTicketActivity.show(InviteStatusActivity.this, order_id);
                break;
            case "order_cancel"://取消订单
                RefundTicketActivity.show(InviteStatusActivity.this, order_id + "");
                break;
            case "meet"://订单票券
                OrderTicketActivity.show(InviteStatusActivity.this, order_id + "");
                break;
        }
    }

}
