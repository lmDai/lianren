package com.lianren.android.improve.user.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.improve.account.AccountHelper;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.MessageWrap;
import com.lianren.android.improve.bean.UserVipBean;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.improve.bean.pay.Alipay;
import com.lianren.android.improve.bean.pay.UserVipPay;
import com.lianren.android.improve.bean.pay.WXpay;
import com.lianren.android.ui.dialog.VipInfoDialog;
import com.lianren.android.util.AlipayResultListener;
import com.lianren.android.util.AlipayUtils;
import com.lianren.android.util.TDevice;
import com.lianren.android.util.WXUtils;
import com.lianren.android.widget.BottomDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
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
 * @package: com.lianren.android.improve.user.activities
 * @user:xhkj
 * @date:2019/12/24
 * @description:开通vip
 **/
public class VipActivity extends BackActivity implements View.OnClickListener {
    @Bind(R.id.tv_user_state)
    TextView tvUserState;
    @Bind(R.id.tv_state_end_desc)
    TextView tvStateEndDesc;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @Bind(R.id.btn_vip)
    Button btnVip;
    private UsersInfoBean usersInfoBean;
    public static final int PAY_ALIPAY = 2;
    public static final int PAY_WXPAY = 3;
    public static int currentPay = PAY_WXPAY;
    private UserVipBean enableMemberItem;

    public static void show(Context mContext, UsersInfoBean usersInfoBean) {
        Intent intent = new Intent();
        intent.putExtra("user_info", usersInfoBean);
        intent.setClass(mContext, VipActivity.class);
        mContext.startActivity(intent);
    }


    @Override
    protected int getContentView() {
        return R.layout.activity_vip;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
    }

    private BottomDialog mSelectorDialog;

    private Dialog getSelectorDialog() {
        if (mSelectorDialog == null) {
            mSelectorDialog = new BottomDialog(this, true);
            @SuppressLint("InflateParams")
            View view = LayoutInflater.from(this).inflate(R.layout.view_pay_method, null, false);
            view.findViewById(R.id.layout_wxpay).setOnClickListener(this);
            view.findViewById(R.id.layout_alipay).setOnClickListener(this);
            view.findViewById(R.id.tv_cancel).setOnClickListener(this);
            mSelectorDialog.setContentView(view);
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.setBackgroundResource(R.color.transparent);
            }
        }
        return mSelectorDialog;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageWrap event) {
        if (event.code == Constants.PAY_CODE) {
            BaseResp resp = (BaseResp) event.message;
            switch (resp.errCode) {
                case 0://支付成功
                    getUsersInfo(AccountHelper.getUser().uuid, AccountHelper.getUser().id);
                    EventBus.getDefault().post(new MessageWrap(Constants.REFRESH_USER, null));
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

    @Override
    protected void initData() {
        super.initData();
        EventBus.getDefault().register(this);
        usersInfoBean = (UsersInfoBean) getIntent().getSerializableExtra("user_info");
        getUsersInfo(AccountHelper.getUser().uuid, AccountHelper.getUser().id);
        if (usersInfoBean.advance != null && usersInfoBean.advance.vip != null) {
            UsersInfoBean.AdvanceBean.VipBean userVip = usersInfoBean.advance.vip;
            int state = userVip.status;

            if (state == 1) {
                btnVip.setText("续费");
                tvUserState.setText("VIP");
                tvStateEndDesc.setText("到期时间：" + userVip.express_time);
            } else {
                tvUserState.setText("普通会员");
                btnVip.setText("开通");
                tvStateEndDesc.setText(userVip.express_time);
            }
        }
    }

    private void goodsListVip() {
        LRApi.goodsListVip(new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AppContext.showToast(R.string.tip_network_error);
            }

            @Override
            public void onCancel() {
                super.onCancel();
                if (!TDevice.hasInternet()) {
                    AppContext.showToast(R.string.tip_network_error);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<List<UserVipBean>>>() {
                    }.getType();

                    ResultBean<List<UserVipBean>> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        showVipInfoDialog(resultBean.data);
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

    private void showVipInfoDialog(List<UserVipBean> mList) {
        if (mList != null && mList.size() > 0) {
            for (int i = 0; i < mList.size(); i++) {
                mList.get(i).chcked = i == 0;
            }
        }
        new VipInfoDialog.Builder(this)
                .setList(mList)
                .setListener(new VipInfoDialog.OnListener() {
                    @Override
                    public void onSelected(Dialog dialog, UserVipBean text) {
                        enableMemberItem = text;
                        if (enableMemberItem.price == 0) {
                            createVipSuccess(enableMemberItem.id, 1 + "", "payment", enableMemberItem.price + "");
                        } else {
                            getSelectorDialog().show();
                        }
                    }

                    @Override
                    public void onCancel(Dialog dialog) {

                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_wxpay:
                if (mSelectorDialog.isShowing())
                    mSelectorDialog.cancel();
                currentPay = PAY_WXPAY;
                createVipSuccess(enableMemberItem.id, 1 + "", currentPay + "", enableMemberItem.price + "");
                break;
            case R.id.layout_alipay:
                if (mSelectorDialog.isShowing())
                    mSelectorDialog.cancel();
                currentPay = PAY_ALIPAY;
                createVipSuccess(enableMemberItem.id, 1 + "", currentPay + "", enableMemberItem.price + "");
                break;
            case R.id.tv_cancel:
                if (mSelectorDialog.isShowing())
                    mSelectorDialog.cancel();
                break;
        }
    }

    private void createVipSuccess(String good_id, String number, String payment, String receipt) {
        LRApi.payVirtul(good_id, number, payment, receipt, new CommonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showLoadingDialog("加载中...");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AppContext.showToast(R.string.tip_network_error);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissLoadingDialog();
            }

            @Override
            public void onCancel() {
                super.onCancel();
                if (!TDevice.hasInternet()) {
                    AppContext.showToast(R.string.tip_network_error);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<UserVipPay>>() {
                    }.getType();

                    ResultBean<UserVipPay> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        if (resultBean.data.order != null && resultBean.data.order.status == 1) {
                            EventBus.getDefault().post(new MessageWrap(Constants.REFRESH_USER, null));
                            getUsersInfo(AccountHelper.getUser().uuid, AccountHelper.getUser().id);
                        } else {
                            userVipPaySuccess(resultBean.data);
                        }
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

    public void userVipPaySuccess(UserVipPay vipPay) {
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
                    // 支付宝支付成功
                    EventBus.getDefault().post(new MessageWrap(Constants.REFRESH_USER, null));
                    getUsersInfo(AccountHelper.getUser().uuid, AccountHelper.getUser().id);
                }

                @Override
                public void onFail() {
                    //支付失败，由于底层已提示，这里不需要处理
                    AppContext.showToast("支付失败！请稍后再试");
                }
            });
        }
    }

    private void updateView(UsersInfoBean usersInfoBean) {
        if (usersInfoBean.advance != null && usersInfoBean.advance.vip != null) {
            UsersInfoBean.AdvanceBean.VipBean userVip = usersInfoBean.advance.vip;
            int state = userVip.status;
            if (state == 1) {
                tvUserState.setText("VIP");
                btnVip.setText("续费");
                tvStateEndDesc.setText("到期时间：" + userVip.express_time);
            } else {
                tvUserState.setText("普通会员");
                btnVip.setText("开通");
                tvStateEndDesc.setText(userVip.express_time);
            }
        }
    }

    public void getUsersInfo(String user_uuid, int user_id) {
        LRApi.usersInfo(user_uuid, user_id, new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<UsersInfoBean>>() {
                    }.getType();
                    ResultBean<UsersInfoBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        updateView(resultBean.data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }


    @OnClick(R.id.btn_vip)
    public void onViewClicked() {
        goodsListVip();
    }
}
