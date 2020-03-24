package com.lianren.android.improve.account.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v4.content.SharedPreferencesCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.api.ApiHttpClient;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.api.remote.LoginApi;
import com.lianren.android.improve.account.AccountHelper;
import com.lianren.android.improve.account.base.AccountBaseActivity;
import com.lianren.android.improve.account.constants.UserConstants;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.bean.LoginBean;
import com.lianren.android.improve.bean.User;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.improve.feedback.FeedTypeActivity;
import com.lianren.android.improve.main.MainActivity;
import com.lianren.android.improve.main.update.LRSharedPreference;
import com.lianren.android.util.DialogHelper;
import com.lianren.android.util.TDevice;
import com.lianren.android.widget.SimplexToast;
import com.lianren.android.widget.VerifyCodeView;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cz.msebera.android.httpclient.Header;

/**
 * @package: com.lianren.android.improve.account.base
 * @user:xhkj
 * @date:2019/12/18
 * @description:验证码登录
 **/
public class CodeLoginActivity extends AccountBaseActivity {
    public static final String HOLD_USERNAME_KEY = "holdUsernameKey";
    @Bind(R.id.iv_login_logo)
    ImageView ivLoginLogo;
    @Bind(R.id.verify_code_view)
    VerifyCodeView verifyCodeView;
    @Bind(R.id.tv_phone)
    TextView tvPhone;
    @Bind(R.id.bt_login_submit)
    Button btLoginSubmit;
    @Bind(R.id.tv_feed_back)
    TextView tvFeedBack;
    @Bind(R.id.tv_info)
    TextView tvInfo;
    private int mRequestType = 1;
    private int inputType;
    private CountDownTimer mTimer;
    private CommonHttpResponseHandler mHandler = new CommonHttpResponseHandler() {
        @Override
        public void onStart() {
            super.onStart();
            showWaitDialog(R.string.progress_submit);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            hideWaitDialog();
        }

        @Override
        public void onCancel() {
            super.onCancel();
            hideWaitDialog();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            if (mRequestType == 1) {
                if (mTimer != null) {
                    mTimer.onFinish();
                    mTimer.cancel();
                }
            }
            requestFailureHint(throwable);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {

            try {
                switch (mRequestType) {
                    //第一步请求发送验证码
                    case 1:
                        Type type = new TypeToken<ResultBean>() {
                        }.getType();
                        ResultBean resultBean = AppOperator.createGson().fromJson(responseString, type);
                        if (!resultBean.isSuccess())
                            AppContext.showToast(resultBean.error.message);
                        break;
                    case 2:
                        Type typeCodes = new TypeToken<ResultBean>() {
                        }.getType();
                        ResultBean resultCodes = AppOperator.createGson().fromJson(responseString, typeCodes);
                        if (resultCodes.isSuccess()) {
                            InputCodeActivity.show(CodeLoginActivity.this, phone, verifyCodeView.getEditContent(), 2);
                        } else {
                            AppContext.showToast(resultCodes.error.message);
                        }
                        break;
                    default:
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseString, e);
            }

        }
    };
    private String phone;

    public static void show(Context mContext, String phone, int inputType) {
        Intent intent = new Intent();
        intent.setClass(mContext, CodeLoginActivity.class);
        intent.putExtra("phone", phone);
        intent.putExtra("inputType", inputType);
        mContext.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_code_login;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        setSwipeBackEnable(false);
        verifyCodeView.setInputCompleteListener(new VerifyCodeView.InputCompleteListener() {
            @Override
            public void inputComplete() {
                if (inputType == 2) {//验证码登录
                    loginRequest();
                } else if (inputType == 1) {//注册
                    mRequestType = 2;
                    LoginApi.codesVerify(phone, "register", verifyCodeView.getEditContent(), mHandler);
                }
            }

            @Override
            public void invalidContent() {

            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void loginRequest() {
        //登录成功,请求数据进入用户个人中心页面
        if (TDevice.hasInternet()) {
            requestLogin(phone, verifyCodeView.getEditContent());
        } else {
            showToastForKeyBord(R.string.footer_type_net_error);
        }
    }

    private void requestLogin(String tempUsername, String tempPwd) {
        LRApi.loginCode(JPushInterface.getRegistrationID(this),tempUsername, tempPwd, new CommonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showFocusWaitDialog();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                requestFailureHint(throwable);
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<LoginBean>>() {
                    }.getType();

                    ResultBean<LoginBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        User user = resultBean.data.user;
                        if (TextUtils.equals("close", user.status)) {
                            if (AccountHelper.login(user))
                                showDialog(user, resultBean.data.token);
                        } else if (AccountHelper.login(user)) {
                            ApiHttpClient.setCookieHeader(resultBean.data.token);
                            LRSharedPreference.getInstance().putToken(resultBean.data.token);
                            logSucceed();
                        } else {
                            showToastForKeyBord("登录异常");
                        }
                    } else {
                        String message = resultBean.error.message;
                        showToastForKeyBord(message);
                        btLoginSubmit.setText(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                hideWaitDialog();
            }

            @Override
            public void onCancel() {
                super.onCancel();
                hideWaitDialog();
            }
        });
    }

    public void showDialog(final User user, final String token) {
        DialogHelper.getConfirmDialog(this, "提示", "该账号已关闭，是否需要开启?", "开启", "保持不变", false, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateUserInfo(user, token);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }

    private void updateUserInfo(final User user, final String token) {
        LRApi.upDateUserInfo("using", UserConstants.STATUS,
                new CommonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        SimplexToast.show(CodeLoginActivity.this, "网络错误");
                    }

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
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            Type type = new TypeToken<ResultBean>() {
                            }.getType();
                            ResultBean bean = new Gson().fromJson(responseString, type);
                            if (bean.isSuccess()) {
                                AccountHelper.getUser().status = "using";
                                if (AccountHelper.login(user)) {
                                    ApiHttpClient.setCookieHeader(token);
                                    LRSharedPreference.getInstance().putToken(token);
                                    logSucceed();
                                }
                            } else {
                                AppContext.showToast(bean.error.message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void logSucceed() {
        View view;
        if ((view = getCurrentFocus()) != null) {
            hideKeyBoard(view.getWindowToken());
        }
        AppContext.showToast(R.string.login_success_hint);
        setResult(RESULT_OK);
        sendLocalReceiver();
        holdAccount();
    }

    /**
     * hold account information
     */
    private void holdAccount() {
        if (!TextUtils.isEmpty(phone)) {
            SharedPreferences sp = getSharedPreferences(UserConstants.HOLD_ACCOUNT, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(HOLD_USERNAME_KEY, phone);
            SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
        }
        MainActivity.show(this);
    }

    @Override
    protected void initData() {
        super.initData();//必须要,用来注册本地广播
        phone = getIntent().getStringExtra("phone");
        inputType = getIntent().getIntExtra("inputType", -1);
        tvInfo.setVisibility(inputType == 1 ? View.VISIBLE : View.GONE);

        tvPhone.setText("已发送验证码至" + phone);
        mTimer = new CountDownTimer(60 * 1000, 1000) {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTick(long millisUntilFinished) {
                btLoginSubmit.setText("重新发送(" + (millisUntilFinished / 1000) + "s)");
                btLoginSubmit.setEnabled(false);
            }

            @Override
            public void onFinish() {
                btLoginSubmit.setText("获取验证码");
                btLoginSubmit.setEnabled(true);
            }
        }.start();
    }


    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            hideKeyBoard(getCurrentFocus().getWindowToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @OnClick({R.id.bt_login_submit, R.id.tv_feed_back,
            R.id.lay_login_container})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_login_submit://登录
                requestSmsCode();
                break;
            case R.id.tv_feed_back://反馈
                FeedTypeActivity.show(CodeLoginActivity.this, UserConstants.FEED_APP, "", phone);
                break;
            case R.id.lay_login_container:
                try {
                    hideKeyBoard(getCurrentFocus().getWindowToken());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }


    private void requestSmsCode() {
        mRequestType = 1;
        mTimer = new CountDownTimer(60 * 1000, 1000) {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTick(long millisUntilFinished) {
                btLoginSubmit.setText("重新发送(" + (millisUntilFinished / 1000) + "s)");
                btLoginSubmit.setEnabled(false);
            }

            @Override
            public void onFinish() {
                btLoginSubmit.setText("获取验证码");
                btLoginSubmit.setEnabled(true);
            }
        }.start();
        if (inputType == 1) {
            LoginApi.codeSend(phone, "register", mHandler);
        } else if (inputType == 2) {
            LoginApi.codeSend(phone, "login", mHandler);
        }
    }
}
