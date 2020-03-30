package com.lianren.android.improve.account.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LoginApi;
import com.lianren.android.improve.account.AccountHelper;
import com.lianren.android.improve.account.base.AccountBaseActivity;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.util.RichTextParser;
import com.lianren.android.util.TDevice;
import com.lianren.android.util.UIHelper;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * @package: com.lianren.android.improve.account.base
 * @user:xhkj
 * @date:2019/12/18
 * @description:注册
 **/
public class ForgetPasswordActivity extends AccountBaseActivity implements View.OnFocusChangeListener {
    public static final String HOLD_USERNAME_KEY = "holdUsernameKey";
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_login_logo)
    ImageView ivLoginLogo;
    @Bind(R.id.iv_login_username_icon)
    ImageView ivLoginUsernameIcon;
    @Bind(R.id.et_login_username)
    EditText etLoginUsername;
    @Bind(R.id.iv_login_username_del)
    ImageView ivLoginUsernameDel;
    @Bind(R.id.ll_login_username)
    LinearLayout llLoginUsername;
    @Bind(R.id.et_code)
    EditText etCode;
    @Bind(R.id.tv_register_sms_call)
    TextView tvRegisterSmsCall;
    @Bind(R.id.ll_login_code)
    LinearLayout llLoginCode;
    @Bind(R.id.iv_login_pwd_icon)
    ImageView ivLoginPwdIcon;
    @Bind(R.id.et_login_pwd)
    EditText etLoginPwd;
    @Bind(R.id.iv_login_pwd_del)
    ImageView ivLoginPwdDel;
    @Bind(R.id.ll_login_pwd)
    LinearLayout llLoginPwd;
    @Bind(R.id.bt_login_submit)
    Button btLoginSubmit;
    @Bind(R.id.lay_login_container)
    LinearLayout layLoginContainer;


    private boolean mMachPhoneNum;
    private CountDownTimer mTimer;
    private int mRequestType = 1;//1. 请求发送验证码  2.注册
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
                        if (resultBean.isSuccess()) {
                            tvRegisterSmsCall.setAlpha(0.4f);
                            tvRegisterSmsCall.setEnabled(false);
                        } else {
                            AppContext.showToast(resultBean.error.message);
                        }
                        break;
                    //第二步请求进行注册
                    case 2:
                        try {
                            Type registerType = new TypeToken<ResultBean>() {
                            }.getType();
                            ResultBean result = AppOperator.createGson().fromJson(responseString, registerType);
                            if (result.isSuccess()) {
                                logSucceed();
                            } else {
                                String message = result.error.message;
                                showToastForKeyBord(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            onFailure(statusCode, headers, responseString, e);
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

    private void logSucceed() {
        View view;
        if ((view = getCurrentFocus()) != null) {
            hideKeyBoard(view.getWindowToken());
        }
        setResult(RESULT_OK);
        UIHelper.clearAppCache(false);
        // 注销操作
        AccountHelper.logout(btLoginSubmit, new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                finish();
            }
        });
    }

    /**
     * show the login activity
     *
     * @param context context
     */
    public static void show(Context context) {
        Intent intent = new Intent(context, ForgetPasswordActivity.class);
        context.startActivity(intent);
    }

    /**
     * show the login activity
     *
     * @param context context
     */
    public static void show(Activity context, int requestCode) {
        Intent intent = new Intent(context, ForgetPasswordActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * show the login activity
     *
     * @param fragment fragment
     */
    public static void show(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), ForgetPasswordActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_forget_password;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        etLoginUsername.setOnFocusChangeListener(this);
        etLoginUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @SuppressWarnings("deprecation")
            @Override
            public void afterTextChanged(Editable s) {
                String username = s.toString().trim();
                if (username.length() > 0) {
                    llLoginUsername.setBackgroundResource(R.drawable.bg_login_input_ok);
                    ivLoginUsernameDel.setVisibility(View.VISIBLE);
                } else {
                    llLoginUsername.setBackgroundResource(R.drawable.bg_login_input_ok);
                    ivLoginUsernameDel.setVisibility(View.INVISIBLE);
                }
                String input = s.toString();
                mMachPhoneNum = RichTextParser.machPhoneNum(input);
                String pwd = etLoginPwd.getText().toString().trim();
                String code = etCode.getText().toString().trim();
                btLoginSubmit.setEnabled(mMachPhoneNum && !TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(code));
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
                    ivLoginPwdDel.setVisibility(View.VISIBLE);
                } else {
                    ivLoginPwdDel.setVisibility(View.INVISIBLE);
                }
                String username = etLoginUsername.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    showToastForKeyBord(R.string.message_username_null);
                }
                String pwd = etLoginPwd.getText().toString().trim();
                String code = etCode.getText().toString().trim();
                btLoginSubmit.setEnabled(mMachPhoneNum && !TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(code));
            }
        });


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

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();
        if (id == R.id.et_login_username) {
            if (hasFocus) {
                llLoginUsername.setActivated(true);
                llLoginPwd.setActivated(false);
            }
        } else {
            if (hasFocus) {
                llLoginPwd.setActivated(true);
                llLoginUsername.setActivated(false);
            }
        }
    }


    @OnClick({
            R.id.bt_login_submit,
            R.id.et_login_username, R.id.et_login_pwd, R.id.tv_register_sms_call,
            R.id.iv_login_username_del, R.id.iv_login_pwd_del,
            R.id.lay_login_container, R.id.et_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_login_submit://注册
                requestRegister();
                break;
            case R.id.tv_register_sms_call://获取验证码
                requestSmsCode();
                break;
            case R.id.et_code:
                etLoginPwd.clearFocus();
                etLoginUsername.clearFocus();
                etCode.setFocusableInTouchMode(true);
                etCode.requestFocus();
                break;
            case R.id.et_login_username:
                etLoginPwd.clearFocus();
                etCode.clearFocus();
                etLoginUsername.setFocusableInTouchMode(true);
                etLoginUsername.requestFocus();
                break;
            case R.id.et_login_pwd:
                etLoginUsername.clearFocus();
                etCode.clearFocus();
                etLoginPwd.setFocusableInTouchMode(true);
                etLoginPwd.requestFocus();
                break;
            case R.id.iv_login_username_del:
                etLoginUsername.setText(null);
                break;
            case R.id.iv_login_pwd_del:
                etLoginPwd.setText(null);
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

    private void requestRegister() {
        String smsCode = etCode.getText().toString().trim();
        if (!mMachPhoneNum || TextUtils.isEmpty(smsCode)) {
            return;
        }
        if (!TDevice.hasInternet()) {
            showToastForKeyBord(R.string.tip_network_error);
            return;
        }
        mRequestType = 2;
        String phoneNumber = etLoginUsername.getText().toString().trim();
        String password = etLoginPwd.getText().toString();
        LoginApi.forgetPassword(phoneNumber, password, smsCode, mHandler);
    }

    private void requestSmsCode() {
        if (!mMachPhoneNum) {
            return;
        }
        if (!TDevice.hasInternet()) {
            showToastForKeyBord(R.string.tip_network_error);
            return;
        }
        if (tvRegisterSmsCall.getTag() == null) {
            mRequestType = 1;
            tvRegisterSmsCall.setAlpha(0.6f);
            tvRegisterSmsCall.setTag(true);
            tvRegisterSmsCall.setAlpha(0.6f);
            tvRegisterSmsCall.setTag(true);
            mTimer = new CountDownTimer(60 * 1000, 1000) {
                @SuppressLint("DefaultLocale")
                @Override
                public void onTick(long millisUntilFinished) {
                    tvRegisterSmsCall.setText((millisUntilFinished / 1000) + "s");
                }

                @Override
                public void onFinish() {
                    tvRegisterSmsCall.setTag(null);
                    tvRegisterSmsCall.setText("获取验证码");
                    tvRegisterSmsCall.setAlpha(1.0f);
                }
            }.start();
            String phoneNumber = etLoginUsername.getText().toString().trim();
            LoginApi.codeSend(phoneNumber, "forget", mHandler);
        } else {
            AppContext.showToast(getResources().getString(R.string.register_sms_wait_hint), Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
