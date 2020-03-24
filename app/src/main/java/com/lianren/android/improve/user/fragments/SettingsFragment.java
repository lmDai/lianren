package com.lianren.android.improve.user.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.security.rp.RPSDK;
import com.google.gson.reflect.TypeToken;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.base.BaseDialog;
import com.lianren.android.base.BaseDialogFragment;
import com.lianren.android.improve.account.AccountHelper;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.base.fragments.BaseFragment;
import com.lianren.android.improve.bean.AuthPrice;
import com.lianren.android.improve.bean.AuthTokenBean;
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.MessageWrap;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.bean.ValidateBean;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.improve.bean.pay.Alipay;
import com.lianren.android.improve.bean.pay.UserVipPay;
import com.lianren.android.improve.bean.pay.WXpay;
import com.lianren.android.improve.user.activities.CollectEventActivity;
import com.lianren.android.improve.user.activities.ContactUserActivity;
import com.lianren.android.improve.user.activities.HelpActivity;
import com.lianren.android.improve.user.activities.MoreSettingsActivity;
import com.lianren.android.improve.user.activities.UserInfoActivity;
import com.lianren.android.improve.user.activities.UserStatusActivity;
import com.lianren.android.improve.user.activities.VipActivity;
import com.lianren.android.improve.user.presenter.UsersInfoContract;
import com.lianren.android.improve.user.presenter.UsersInfoPresenter;
import com.lianren.android.util.AlipayResultListener;
import com.lianren.android.util.AlipayUtils;
import com.lianren.android.util.WXUtils;
import com.tencent.mm.opensdk.modelbase.BaseResp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

import static com.lianren.android.improve.user.fragments.UserInfoFragment.ACTION_UP_DATE;

/**
 * @package: com.lianren.android.improve.user.fragments
 * @user:xhkj
 * @date:2019/12/19
 * @description:
 **/
public class SettingsFragment extends BaseFragment implements UsersInfoContract.View {
    private static final int MESSAGE_UPDATEVIEW = 001;
    @Bind(R.id.tv_vip)
    TextView tvVip;
    @Bind(R.id.tv_ora)
    TextView tvOra;
    @Bind(R.id.tv_status)
    TextView tvStatus;
    @Bind(R.id.ll_ora)
    LinearLayout llOra;
    public static final int PAY_ALIPAY = 2;
    public static final int PAY_WXPAY = 3;
    public static int currentPay = PAY_WXPAY;
    private UsersInfoBean usersInfoBean;
    private UsersInfoContract.Presenter mPresenter;
    private int requestType;
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_UPDATEVIEW:
                    updateView((UsersInfoBean) msg.obj);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    private void updateView(UsersInfoBean usersInfoBean) {
        switch (usersInfoBean.advance.ora.status) {
            case "none":
                tvOra.setText("未认证");
                llOra.setVisibility(View.VISIBLE);
                break;
            case "wait":
                tvOra.setText("认证中");
                llOra.setVisibility(View.VISIBLE);
                break;
            case "pass":
                tvOra.setText("通过");
                llOra.setVisibility(View.GONE);
                break;
            case "reject":
                tvOra.setText("拒绝");
                llOra.setVisibility(View.VISIBLE);
                break;
        }
        switch (usersInfoBean.base.status) {
            case "using":
                tvStatus.setText("我单身，寻找恋人 ");
                break;
            case "pause":
                tvStatus.setText("暂停匹配");
                break;
            case "close":
                tvStatus.setText("关闭资料");
                break;
            case "logoff":
                tvStatus.setText("注销");
                break;
        }
        tvVip.setText(usersInfoBean.advance.vip.status == 0 ? "未开通" : "已开通");
    }

    private CommonHttpResponseHandler handler = new CommonHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            switch (requestType) {
                case 1:
                    try {
                        Type type = new TypeToken<ResultBean<AuthPrice>>() {
                        }.getType();

                        ResultBean<AuthPrice> resultBean = AppOperator.createGson().fromJson(responseString, type);
                        if (resultBean.isSuccess()) {
                            showAuthPayView(resultBean.data);
                        } else {
                            AppContext.showToast(R.string.tip_network_error);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        onFailure(statusCode, headers, responseString, e);
                    }
                    break;
                case 2:
                    try {
                        Type type = new TypeToken<ResultBean<UserVipPay>>() {
                        }.getType();

                        ResultBean<UserVipPay> resultBean = AppOperator.createGson().fromJson(responseString, type);
                        if (resultBean.isSuccess()) {
                            userVipPaySuccess(resultBean.data);
                        } else {
                            AppContext.showToast(R.string.tip_network_error);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        onFailure(statusCode, headers, responseString, e);
                    }
                    break;
                case 3:
                    try {
                        Type type = new TypeToken<ResultBean<AuthTokenBean>>() {
                        }.getType();

                        ResultBean<AuthTokenBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                        if (resultBean.isSuccess()) {
                            authVerify(resultBean.data);
                        } else {
                            AppContext.showToast(resultBean.error.message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        onFailure(statusCode, headers, responseString, e);
                    }
                    break;
                case 999:
                    try {
                        Type type = new TypeToken<ResultBean<ValidateBean>>() {
                        }.getType();
                        ResultBean<ValidateBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                        if (resultBean.isSuccess()) {
                            EventBus.getDefault().post(new MessageWrap(Constants.REFRESH_USER, null));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        onFailure(statusCode, headers, responseString, e);
                    }
                    break;
            }
        }
    };

    private void authVerify(final AuthTokenBean authTokenBean) {
        RPSDK.start(authTokenBean.verifyToken.Token, mContext, new RPSDK.RPCompletedListener() {
            @Override
            public void onAuditResult(RPSDK.AUDIT audit, String code) {
                requestType = 999;
                LRApi.usersIdentiStatus(authTokenBean.ticket_id, handler);
                if (audit == RPSDK.AUDIT.AUDIT_PASS) {
                    // 认证通过。建议接入方调用实人认证服务端接口DescribeVerifyResult来获取最终的认证状态，并以此为准进行业务上的判断和处理
                    // do something
                } else if(audit == RPSDK.AUDIT.AUDIT_FAIL) {
                    // 认证不通过。建议接入方调用实人认证服务端接口DescribeVerifyResult来获取最终的认证状态，并以此为准进行业务上的判断和处理
                    // do something
                } else if(audit == RPSDK.AUDIT.AUDIT_NOT) {
                    // 未认证，具体原因可通过code来区分（code取值参见下方表格），通常是用户主动退出或者姓名身份证号实名校验不匹配等原因，导致未完成认证流程
                    // do something
                }
            }
        });
//        RPSDK.start(authTokenBean.verifyToken.Token, mContext, new RPSDK.RPCompletedListener() {
//            @Override
//            public void onAuditResult(RPSDK.AUDIT audit, String s, String s1) {
//                requestType = 999;
//                LRApi.usersIdentiStatus(authTokenBean.ticket_id, handler);
//                if (audit == RPSDK.AUDIT.AUDIT_PASS) { //认证通过
////                    mAuthVerifyCallback.authVerifySuccess();
//                } else if (audit == RPSDK.AUDIT.AUDIT_FAIL) { //认证不通过
////                    mAuthVerifyCallback.authVerifyFail("认证失败，未通过");
//                } else if (audit == RPSDK.AUDIT.AUDIT_IN_AUDIT) { //认证中，通常不会出现，只有在认证审核系统内部出现超时，未在限定时间内返回认证结果时出现。此时提示用户系统处理中，稍后查看认证结果即可。
////                    mAuthVerifyCallback.authVerifyFail("认证超时，请稍后查看");
//                } else if (audit == RPSDK.AUDIT.AUDIT_NOT) { //未认证，用户取消
////                    mAuthVerifyCallback.authVerifyFail("认证失败，用户取消");
//                } else if (audit == RPSDK.AUDIT.AUDIT_EXCEPTION) { //系统异常
////                    mAuthVerifyCallback.authVerifyFail("认证失败，系统异常");
//                }
//            }
//        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageWrap event) {
        if (event.code == Constants.PAY_CODE) {
            BaseResp resp = (BaseResp) event.message;
            switch (resp.errCode) {
                case 0://支付成功
                    requestType = 3;
                    LRApi.userIdentitiesToken(handler);
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
        } else if (event.code == Constants.REFRESH_USER) {
            mPresenter.getUsersInfo(AccountHelper.getUser().uuid, AccountHelper.getUser().id);
        }
    }

    private void userVipPaySuccess(UserVipPay vipPay) {
        if (vipPay == null) {
            return;
        }
        // 对接支付宝，微信完成支付
        if (currentPay == PAY_WXPAY) {//微信
            WXpay wx = vipPay.wx;
            WXUtils.order2WXPay(mContext, wx);
        } else if (currentPay == PAY_ALIPAY) {//支付宝
            Alipay alipay = vipPay.alipay;
            if (alipay == null) {
                return;
            }
            AlipayUtils.order2Alipay(mContext, alipay.sign_str, new AlipayResultListener() {
                @Override
                public void onSuccess() {
                    requestType = 3;
                    LRApi.userIdentitiesToken(handler);
                }

                @Override
                public void onFail() {
                    //支付失败，由于底层已提示，这里不需要处理
                    AppContext.showToast("支付失败！请稍后再试");
                }
            });
        }

    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_settings;
    }

    public static SettingsFragment newInstance() {
        Bundle args = new Bundle();
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    protected LocalBroadcastManager mManager;
    private BroadcastReceiver mReceiver;


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void registerLocalReceiver() {
        if (mManager == null)
            mManager = LocalBroadcastManager.getInstance(mContext);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_UP_DATE);
        if (mReceiver == null)
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (ACTION_UP_DATE.equals(action)) {
                        usersInfoBean = (UsersInfoBean) intent.getSerializableExtra("user_info_bean");
                        UsersInfoBean.BaseBean base = usersInfoBean.base;
                        if (base == null) return;
                        Message msg = new Message();
                        msg.what = MESSAGE_UPDATEVIEW;
                        msg.obj = usersInfoBean;
                        mHandler.sendMessage(msg);
                    }
                }
            };
        mManager.registerReceiver(mReceiver, filter);
    }


    @Override
    protected void initData() {
        super.initData();
        EventBus.getDefault().register(this);
//        registerLocalReceiver();
        mPresenter = new UsersInfoPresenter(this);
        mPresenter.getUsersInfo(AccountHelper.getUser().uuid, AccountHelper.getUser().id);
    }

    @OnClick({R.id.ll_contact, R.id.ll_collect, R.id.ll_user_info, R.id.ll_vip, R.id.ll_ora, R.id.ll_status, R.id.ll_help, R.id.ll_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_contact://联系人
                ContactUserActivity.show(mContext);
                break;
            case R.id.ll_collect://收藏
                CollectEventActivity.show(mContext, 0);
                break;
            case R.id.ll_user_info://主页
                UserInfoActivity.show(mContext, usersInfoBean.base.id, usersInfoBean.base.uuid);
                break;
            case R.id.ll_vip://vip
                VipActivity.show(mContext, usersInfoBean);
                break;
            case R.id.ll_ora://身份认证
                if (TextUtils.equals(usersInfoBean.advance.ora.status, "none")
                        || TextUtils.equals(usersInfoBean.advance.ora.status, "reject")) {
//                    authToken();
                    requestType = 3;
                    LRApi.userIdentitiesToken(handler);
                }
                break;
            case R.id.ll_status://感情状态
                UserStatusActivity.show(mContext, usersInfoBean);
                break;
            case R.id.ll_help://帮助反馈
                HelpActivity.show(mContext, usersInfoBean);
                break;
            case R.id.ll_more://更多
                MoreSettingsActivity.show(mContext, usersInfoBean);
                break;
        }
    }

    private void authToken() {
        requestType = 1;
        LRApi.userAuthVerifyPrice(handler);
    }

    private void showAuthPayView(AuthPrice authPrice) {
        if (authPrice == null) {
            return;
        }
        String name = authPrice.name;
        final double price = authPrice.price;
        String desc = authPrice.describe;
        final int oid = authPrice.id;
        new BaseDialogFragment.Builder(getActivity())
                .setContentView(R.layout.pop_auth_pay_mothed)
                .setAnimStyle(BaseDialog.AnimStyle.BOTTOM)
                .setGravity(Gravity.CENTER)
                .setText(R.id.tv_name, name)
                .setText(R.id.tv_price, price + "元")
                .setText(R.id.tv_desc, desc)
                .setOnClickListener(R.id.ll_wechat_pay, new BaseDialog.OnClickListener() {
                    @Override
                    public void onClick(BaseDialog dialog, View view) {
                        currentPay = PAY_WXPAY;
                        requestType = 2;
                        LRApi.payVirtul(oid + "", "1", currentPay + "", price + "", handler);
                        dialog.dismiss();
                    }
                })
                .setOnClickListener(R.id.ll_ali_pay, new BaseDialog.OnClickListener() {
                    @Override
                    public void onClick(BaseDialog dialog, View view) {
                        currentPay = PAY_ALIPAY;
                        requestType = 2;
                        LRApi.payVirtul(oid + "", "1", currentPay + "", price + "", handler);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void showUsersInfo(UsersInfoBean mList) {
        this.usersInfoBean = mList;
        updateView(mList);
    }

    @Override
    public void showError(String message) {

    }

    @Override
    public void setPresenter(UsersInfoContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {

    }
}
