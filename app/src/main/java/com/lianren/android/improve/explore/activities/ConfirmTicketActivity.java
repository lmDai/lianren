package com.lianren.android.improve.explore.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.EventApi;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.MessageWrap;
import com.lianren.android.improve.bean.TicketOrderDetailBean;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.improve.bean.pay.Alipay;
import com.lianren.android.improve.bean.pay.UserVipPay;
import com.lianren.android.improve.bean.pay.WXpay;
import com.lianren.android.util.AlipayResultListener;
import com.lianren.android.util.AlipayUtils;
import com.lianren.android.util.WXUtils;
import com.tencent.mm.opensdk.modelbase.BaseResp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * @package: com.lianren.android.improve.explore.activities
 * @user:xhkj
 * @date:2019/12/27
 * @description:订单确认
 **/
public class ConfirmTicketActivity extends TicketBaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_user_name)
    TextView tvUserName;
    @Bind(R.id.tv_user_phone)
    TextView tvUserPhone;
    @Bind(R.id.ll_user_info)
    LinearLayout llUserInfo;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_location)
    TextView tvLocation;
    @Bind(R.id.tv_ticket_name)
    TextView tvTicketName;
    @Bind(R.id.tv_ticket_time)
    TextView tvTicketTime;
    @Bind(R.id.tv_actual_money)
    TextView tvActualMoney;
    @Bind(R.id.tv_single_price)
    TextView tvSinglePrice;
    @Bind(R.id.tv_total_price)
    TextView tvTotalPrice;
    @Bind(R.id.tv_discount_info)
    TextView tvDiscountInfo;
    @Bind(R.id.ll_wechat_pay)
    LinearLayout llWechatPay;
    @Bind(R.id.ll_ali_pay)
    LinearLayout llAliPay;
    @Bind(R.id.tv_pay_exp)
    TextView tvPayExp;
    @Bind(R.id.tv_refund_exp)
    TextView tvRefundExp;
    @Bind(R.id.ll_pay)
    LinearLayout llPay;
    @Bind(R.id.btn_commit)
    Button btnCommit;
    private TicketOrderDetailBean orderDetailBean;
    public static final int PAY_ALIPAY = 2;
    public static final int PAY_WXPAY = 3;
    public static int currentPay = PAY_WXPAY;
    private int order_id;

    public static void show(Context context, TicketOrderDetailBean orderDetailBean) {
        Intent intent = new Intent();
        intent.putExtra("detail", orderDetailBean);
        intent.setClass(context, ConfirmTicketActivity.class);
        context.startActivity(intent);
    }

    public static void show(Context context, int order_id) {
        Intent intent = new Intent();
        intent.putExtra("order_id", order_id);
        intent.setClass(context, ConfirmTicketActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_confirm_ticket;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        tvTotalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }

    @Override
    protected void initData() {
        super.initData();
        order_id = getIntent().getIntExtra("order_id", -1);
        if (order_id != -1) {
            orderDetail();
        }
        orderDetailBean = (TicketOrderDetailBean) getIntent().getSerializableExtra("detail");
        if (orderDetailBean == null) return;
        showLoadingDialog("加载中....");
        TicketOrderDetailBean.PartyOrderUserInfo user = orderDetailBean.user;
        if (user != null) {
            tvUserName.setText(user.nickname);
            tvUserPhone.setText(user.telephone);
        }
        llPay.setVisibility(orderDetailBean.order.actual_amount == 0 ? View.GONE : View.VISIBLE);
        btnCommit.setVisibility(orderDetailBean.order.actual_amount == 0 ? View.VISIBLE : View.GONE);
        List<TicketOrderDetailBean.PartyOrderTicketInfo> good = orderDetailBean.good;
        if (good != null && good.size() > 0) {
            tvTicketName.setText(good.get(0).name);
            tvActualMoney.setText("¥" + orderDetailBean.order.actual_amount);
            tvSinglePrice.setText("单价:¥" + good.get(0).price + "x" + good.get(0).number);
            tvTotalPrice.setText("总价:￥" + orderDetailBean.order.amount);
        }
        TicketOrderDetailBean.PartyBasicInfo subject = orderDetailBean.subject;
        if (subject != null) {
            tvTitle.setText(subject.name);
            tvLocation.setText(subject.address);
            tvTicketTime.setText("有效期 " + subject.time);
            tvPayExp.setText(subject.pay_exp);
            tvRefundExp.setText(subject.refund_exp);
        }
        if (orderDetailBean.prefer != null && orderDetailBean.prefer.size() > 0)
            tvDiscountInfo.setText(orderDetailBean.prefer.get(0).info);
        dismissLoadingDialog();
    }

    @OnClick({R.id.ll_user_info, R.id.ll_wechat_pay, R.id.ll_ali_pay, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_user_info://修改用户信息
                ContactUpdateActivity.show(this, orderDetailBean.order.id, orderDetailBean.user.telephone, orderDetailBean.user.nickname, 2);
                break;
            case R.id.ll_wechat_pay://微信支付
                currentPay = PAY_WXPAY;
                payOrder(currentPay + "");
                break;
            case R.id.ll_ali_pay://支付宝支付
                currentPay = PAY_ALIPAY;
                payOrder(currentPay + "");
                break;
            case R.id.btn_commit:
                AppContext.showToast("购票完成");
                OrderListActivity.show(this);
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 2) {
                String phone = data.getStringExtra("phone");
                String nickName = data.getStringExtra("nickName");
                tvUserName.setText(nickName);
                tvUserPhone.setText(phone);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageWrap event) {
        if (event.code == Constants.PAY_CODE) {
            BaseResp resp = (BaseResp) event.message;
            switch (resp.errCode) {
                case 0://支付成功
                    sendLocalReceiver();
                    EventBus.getDefault().post(new MessageWrap(Constants.REFRESH_INVITE_STATUS, null));
                    AppContext.showToast("购票完成");
                    OrderListActivity.show(this);
                    break;
                case -1://支付失败
                    AppContext.showToast("支付失败！请稍后再试");
                    break;
                case -2://取消支付

                    break;
                default://支付失败
                    AppContext.showToast("支付失败！请稍后再试");
                    break;
            }
        }
    }

    private void orderDetail() {
        EventApi.orderDetail(order_id, new CommonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showLoadingDialog();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissLoadingDialog();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<TicketOrderDetailBean>>() {
                    }.getType();

                    ResultBean<TicketOrderDetailBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        showView(resultBean.data);
                    } else {
                        AppContext.showToast(resultBean.error.message);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    private void showView(TicketOrderDetailBean orderDetailBean) {
        this.orderDetailBean = orderDetailBean;
        TicketOrderDetailBean.PartyOrderUserInfo user = orderDetailBean.user;
        if (user != null) {
            tvUserName.setText(user.nickname);
            tvUserPhone.setText(user.telephone);
        }
        llPay.setVisibility(orderDetailBean.order.actual_amount == 0 ? View.GONE : View.VISIBLE);
        btnCommit.setVisibility(orderDetailBean.order.actual_amount == 0 ? View.VISIBLE : View.GONE);
        List<TicketOrderDetailBean.PartyOrderTicketInfo> good = orderDetailBean.good;
        if (good != null && good.size() > 0) {
            tvTicketName.setText(good.get(0).name);
            tvActualMoney.setText("¥" + orderDetailBean.order.actual_amount);
            tvSinglePrice.setText("单价:¥" + good.get(0).price + "x" + good.get(0).number);
            tvTotalPrice.setText("总价:￥" + orderDetailBean.order.amount);
        }
        TicketOrderDetailBean.PartyBasicInfo subject = orderDetailBean.subject;
        if (subject != null) {
            tvTitle.setText(subject.name);
            tvLocation.setText(subject.address);
            tvTicketTime.setText("有效期 " + subject.time);
            tvPayExp.setText(subject.pay_exp);
            tvRefundExp.setText(subject.refund_exp);
        }
        if (orderDetailBean.prefer != null && orderDetailBean.prefer.size() > 0)
            tvDiscountInfo.setText(orderDetailBean.prefer.get(0).info);
    }

    private void payOrder(String payment) {
        EventApi.payOrder(payment, orderDetailBean.order.id, new CommonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showLoadingDialog();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissLoadingDialog();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<UserVipPay>>() {
                    }.getType();

                    ResultBean<UserVipPay> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        userVipPaySuccess(resultBean.data);
                    } else {
                        AppContext.showToast(resultBean.error.message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    private void userVipPaySuccess(UserVipPay vipPay) {
        if (vipPay == null) {
            return;
        }
        // 对接支付宝，微信完成支付
        if (currentPay == PAY_WXPAY) {//微信
            WXpay wx = vipPay.wx;
            WXUtils.order2WXPay(this, wx);
        } else if (currentPay == PAY_ALIPAY) {//支付宝
            Alipay alipay = vipPay.alipay;
            if (alipay == null) {
                return;
            }
            AlipayUtils.order2Alipay(this, alipay.sign_str, new AlipayResultListener() {
                @Override
                public void onSuccess() {
                    sendLocalReceiver();
                    EventBus.getDefault().post(new MessageWrap(Constants.REFRESH_INVITE_STATUS, null));
                    AppContext.showToast("购票完成");
                    OrderListActivity.show(ConfirmTicketActivity.this);
                }

                @Override
                public void onFail() {
                    //支付失败，由于底层已提示，这里不需要处理
                    AppContext.showToast("支付失败！请稍后再试");
                }
            });
        }

    }
}

