package com.lianren.android.improve.account.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.SharedPreferencesCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cz.msebera.android.httpclient.Header;

import static com.lianren.android.improve.account.activities.NewLoginActivity.HOLD_USERNAME_KEY;

/**
 * @package: com.lianren.android.improve.account.base
 * @user:xhkj
 * @date:2019/12/18
 * @description:验证码登录
 **/
public class InputCodeActivity extends AccountBaseActivity implements View.OnFocusChangeListener {


    @Bind(R.id.iv_login_logo)
    ImageView ivLoginLogo;
    @Bind(R.id.et_login_pwd)
    EditText etLoginPwd;
    @Bind(R.id.ll_login_pwd)
    LinearLayout llLoginPwd;
    @Bind(R.id.tv_code_login)
    TextView tvCodeLogin;
    @Bind(R.id.bt_login_submit)
    Button btLoginSubmit;
    @Bind(R.id.tv_feed_back)
    TextView tvFeedBack;
    @Bind(R.id.lay_login_container)
    LinearLayout layLoginContainer;
    @Bind(R.id.togglePwd)
    CheckBox togglePwd;
    private String tempUsername;
    private int type;
    @Bind(R.id.tv_info)
    TextView tvInfo;
    private String code;//验证码

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
        if (!TextUtils.isEmpty(tempUsername)) {
            SharedPreferences sp = getSharedPreferences(UserConstants.HOLD_ACCOUNT, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(HOLD_USERNAME_KEY, tempUsername);
            SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
        }
        MainActivity.show(this);
    }

    public static void show(Context mContext, String phone, int type) {
        Intent intent = new Intent();
        intent.setClass(mContext, InputCodeActivity.class);
        intent.putExtra("phone", phone);
        intent.putExtra("type", type);
        mContext.startActivity(intent);
    }

    public static void show(Context mContext, String phone, String code, int type) {
        Intent intent = new Intent();
        intent.setClass(mContext, InputCodeActivity.class);
        intent.putExtra("phone", phone);
        intent.putExtra("type", type);
        intent.putExtra("code", code);
        mContext.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_input_code;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        setSwipeBackEnable(false);
        setTitle("");
        togglePwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //如果选中，显示密码
                    etLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //否则隐藏密码
                    etLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        etLoginPwd.setOnFocusChangeListener(this);
        etLoginPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressWarnings("deprecation")
            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                if (length > 0) {
                    llLoginPwd.setBackgroundResource(R.drawable.bg_login_input_ok);
                }
                String pwd = etLoginPwd.getText().toString().trim();
                btLoginSubmit.setEnabled(!TextUtils.isEmpty(pwd) && pwd.length() >= 6);
            }
        });

    }

    @Override
    protected void initData() {
        super.initData();//必须要,用来注册本地广播
        tempUsername = getIntent().getStringExtra("phone");
        type = getIntent().getIntExtra("type", -1);
        code = getIntent().getStringExtra("code");
        if (type != 1) {
            etLoginPwd.setHint("输入新密码");
            tvInfo.setText("设置新密码");
            tvCodeLogin.setVisibility(View.INVISIBLE);
        }
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

    @SuppressWarnings("ConstantConditions")
    private void loginRequest() {
        String tempPwd = etLoginPwd.getText().toString().trim();
        if (!TextUtils.isEmpty(tempPwd) && !TextUtils.isEmpty(tempUsername)) {
            //登录成功,请求数据进入用户个人中心页面
            if (tempPwd.length() < 6 || tempPwd.length() > 20) {
                showToastForKeyBord("密码必须在6-20位");
            } else {
                if (TDevice.hasInternet()) {
                    requestLogin(tempUsername, tempPwd);
                } else {
                    showToastForKeyBord(R.string.footer_type_net_error);
                }
            }
        } else {
            showToastForKeyBord("请输入密码");
        }
    }

    private void requestLogin(String tempUsername, String tempPwd) {
        LRApi.login(JPushInterface.getRegistrationID(this),tempUsername, tempPwd, new CommonHttpResponseHandler() {
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

    private void requestRegister() {
        String tempPwd = etLoginPwd.getText().toString().trim();
        if (!TextUtils.isEmpty(tempPwd) && !TextUtils.isEmpty(tempUsername)) {
            //登录成功,请求数据进入用户个人中心页面
            if (tempPwd.length() < 6 || tempPwd.length() > 20) {
                showToastForKeyBord("密码必须在6-20位");
            } else {
                if (TDevice.hasInternet()) {
                    LoginApi.register(JPushInterface.getRegistrationID(this),tempUsername, etLoginPwd.getText().toString(), code, new CommonHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            super.onStart();
                            showFocusWaitDialog();
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
                            requestFailureHint(throwable);
                        }

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
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                onFailure(statusCode, headers, responseString, e);
                            }
                        }
                    });
                } else {
                    showToastForKeyBord(R.string.footer_type_net_error);
                }
            }
        } else {
            showToastForKeyBord("请输入密码");
        }
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
                        SimplexToast.show(InputCodeActivity.this, "网络错误");
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

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();
        if (id == R.id.et_login_username) {
            if (hasFocus) {
                llLoginPwd.setActivated(false);
            }
        } else {
            if (hasFocus) {
                llLoginPwd.setActivated(true);
            }
        }
    }

    @OnClick({R.id.bt_login_submit, R.id.tv_feed_back,
            R.id.lay_login_container, R.id.tv_code_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_code_login://验证码登录
                requestLogin();
                break;
            case R.id.bt_login_submit:
                if (type != 1) {
                    requestRegister();
                } else {
                    loginRequest();
                }
                break;
            case R.id.tv_feed_back:
                FeedTypeActivity.show(InputCodeActivity.this, UserConstants.FEED_APP, "", tempUsername);
                break;
            case R.id.lay_login_container:
                try {
                    hideKeyBoard(getCurrentFocus().getWindowToken());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void requestLogin() {
        LoginApi.codeSend(tempUsername, "login", new CommonHttpResponseHandler() {
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
                showToastForKeyBord(R.string.request_error_hint);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Type type = new TypeToken<ResultBean>() {
                }.getType();
                ResultBean resultBean = AppOperator.createGson().fromJson(responseString, type);
                if (resultBean.isSuccess()) {
                    CodeLoginActivity.show(InputCodeActivity.this, tempUsername, 2);
                } else {
                    AppContext.showToast(resultBean.error.message);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
