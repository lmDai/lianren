package com.lianren.android.improve.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.security.rp.RPSDK;
import com.google.gson.reflect.TypeToken;
import com.lianren.android.AppContext;
import com.lianren.android.LRApplication;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.improve.account.AccountHelper;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.base.activities.BaseActivity;
import com.lianren.android.improve.bean.AuthTokenBean;
import com.lianren.android.improve.bean.BreakBean;
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.MessageWrap;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.bean.ValidateBean;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.improve.notice.NoticeManager;
import com.lianren.android.improve.user.activities.UserBaseInfoActivity;
import com.lianren.android.improve.user.activities.UserPhotoActivity;
import com.lianren.android.improve.user.activities.VipActivity;
import com.lianren.android.util.ActivityCollector;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

import static com.lianren.android.improve.user.fragments.UserInfoFragment.ACTION_UP_DATE;

/**
 * @package: com.lianren.android.improve.main
 * @user:xhkj
 * @date:2019/12/27
 * @description:
 **/
public class BreakActivity extends BaseActivity {
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.btn_commit)
    Button btnCommit;
    @Bind(R.id.img_close)
    ImageView imgClose;
    private String str;
    private ResultBean<BreakBean> resultBean;
    private int requestType;
    private CommonHttpResponseHandler mHandler = new CommonHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            switch (requestType) {
                case ResultBean.VIP:
                    try {
                        Type type = new TypeToken<ResultBean<UsersInfoBean>>() {
                        }.getType();
                        ResultBean<UsersInfoBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                        if (resultBean.isSuccess()) {
                            VipActivity.show(BreakActivity.this, resultBean.data);
                            finish();
                        } else {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        onFailure(statusCode, headers, responseString, e);
                    }
                    break;
                case ResultBean.NO_HEAD:
                    try {
                        Type type = new TypeToken<ResultBean<UsersInfoBean>>() {
                        }.getType();
                        ResultBean<UsersInfoBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                        if (resultBean.isSuccess()) {
                            Intent intentHead = new Intent(BreakActivity.this, UserPhotoActivity.class);
                            intentHead.putExtra("user_info", resultBean.data);
                            startActivity(intentHead);
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        onFailure(statusCode, headers, responseString, e);
                    }
                    break;
                case ResultBean.REAL_NAME:
                    try {
                        Type type = new TypeToken<ResultBean<AuthTokenBean>>() {
                        }.getType();

                        ResultBean<AuthTokenBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                        if (resultBean.isSuccess()) {
                            authVerify(resultBean.data);
                        } else {
                            AppContext.showToast(R.string.tip_network_error);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        onFailure(statusCode, headers, responseString, e);
                    }
                    break;
                case ResultBean.USER_INFO:
                    try {
                        Type type = new TypeToken<ResultBean<UsersInfoBean>>() {
                        }.getType();
                        ResultBean<UsersInfoBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                        if (resultBean.isSuccess()) {
                            UserBaseInfoActivity.show(BreakActivity.this, resultBean.data, 1);
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
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        onFailure(statusCode, headers, responseString, e);
                    }
                    break;

            }
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            //do something.
            if (resultBean.code == ResultBean.USER_INFO) {
                ActivityCollector.removeAllActivity();
            } else {
                finish();
            }
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageWrap event) {
        if (event.code == Constants.CLOSE_BREAK) {
            NoticeManager.init(LRApplication.getInstance());
            finish();
        } else if (event.code == Constants.REFRESH_USER) {
            LRApi.usersInfo(AccountHelper.getUser().uuid, AccountHelper.getUser().id, new CommonHttpResponseHandler() {
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
                            Intent broadcast = new Intent(ACTION_UP_DATE);
                            broadcast.putExtra("user_info_bean", resultBean.data);
                            LocalBroadcastManager.getInstance(BreakActivity.this).sendBroadcast(broadcast);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        onFailure(statusCode, headers, responseString, e);
                    }
                }
            });
        }
    }


    public static void show(Activity activity) {
        Intent intent = new Intent(activity, BreakActivity.class);
        activity.startActivityForResult(intent, 0x01);
    }

    public static void show(Context context, String str) {
        Intent intent = new Intent(context, BreakActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("str", str);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_break;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            setTitle("");
            str = intent.getStringExtra("str");
            try {
                Type type = new TypeToken<ResultBean<BreakBean>>() {
                }.getType();
                ResultBean<BreakBean> resultBean = AppOperator.createGson().fromJson(str, type);
                this.resultBean = resultBean;
                imgClose.setVisibility(resultBean.code == ResultBean.USER_INFO ? View.GONE : View.VISIBLE);
                tvTitle.setText(resultBean.data.title);
                tvContent.setText(Html.fromHtml(resultBean.data.content));
                btnCommit.setText(resultBean.data.bottom.name);
            } catch (Exception e) {
                e.printStackTrace();
            }
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initData() {
        super.initData();
        setSwipeBackEnable(false);
        EventBus.getDefault().register(this);

        setTitle("");
        str = getIntent().getStringExtra("str");
        Log.i("single", str);
        try {
            Type type = new TypeToken<ResultBean<BreakBean>>() {
            }.getType();
            ResultBean<BreakBean> resultBean = AppOperator.createGson().fromJson(str, type);
            this.resultBean = resultBean;
            imgClose.setVisibility(resultBean.code == ResultBean.USER_INFO ? View.GONE : View.VISIBLE);
            tvTitle.setText(resultBean.data.title);
            tvContent.setText(Html.fromHtml(resultBean.data.content));
            btnCommit.setText(resultBean.data.bottom.name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    @OnClick({R.id.img_close, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                finish();
                break;
            case R.id.btn_commit:
                if (resultBean == null) return;
                requestType = resultBean.code;
                switch (resultBean.code) {
                    case ResultBean.VIP:
                        getUsersInfo(AccountHelper.getUser().uuid, AccountHelper.getUser().id);
                        break;
                    case ResultBean.NO_HEAD:
                        getUsersInfo(AccountHelper.getUser().uuid, AccountHelper.getUser().id);
                        break;
                    case ResultBean.REAL_NAME:
                        LRApi.userIdentitiesToken(mHandler);
                        break;
                    case ResultBean.USER_INFO:
                        getUsersInfo(AccountHelper.getUser().uuid, AccountHelper.getUser().id);
                        break;
                }
                break;
        }
    }

    private void authVerify(final AuthTokenBean authTokenBean) {
        RPSDK.start(authTokenBean.verifyToken.Token, this, new RPSDK.RPCompletedListener() {
            @Override
            public void onAuditResult(RPSDK.AUDIT audit, String code) {
                requestType = 999;
                LRApi.usersIdentiStatus(authTokenBean.ticket_id, mHandler);
                if (audit == RPSDK.AUDIT.AUDIT_PASS) {
                    // 认证通过。建议接入方调用实人认证服务端接口DescribeVerifyResult来获取最终的认证状态，并以此为准进行业务上的判断和处理
                    // do something
                } else if (audit == RPSDK.AUDIT.AUDIT_FAIL) {
                    // 认证不通过。建议接入方调用实人认证服务端接口DescribeVerifyResult来获取最终的认证状态，并以此为准进行业务上的判断和处理
                    // do something
                } else if (audit == RPSDK.AUDIT.AUDIT_NOT) {
                    // 未认证，具体原因可通过code来区分（code取值参见下方表格），通常是用户主动退出或者姓名身份证号实名校验不匹配等原因，导致未完成认证流程
                    // do something
                }
            }
        });
    }

    public void getUsersInfo(String user_uuid, int user_id) {
        LRApi.usersInfo(user_uuid, user_id, mHandler);
    }
}
