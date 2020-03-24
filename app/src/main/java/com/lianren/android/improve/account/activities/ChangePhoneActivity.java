package com.lianren.android.improve.account.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
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
import com.lianren.android.improve.account.base.AccountBaseActivity;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.util.RichTextParser;
import com.lianren.android.util.TDevice;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * @package: com.lianren.android.improve.account.base
 * @user:xhkj
 * @date:2019/12/18
 * @description:账号密码登录
 **/
public class ChangePhoneActivity extends AccountBaseActivity implements View.OnFocusChangeListener {
    public static final String HOLD_USERNAME_KEY = "holdUsernameKey";
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
    @Bind(R.id.bt_login_submit)
    Button btLoginSubmit;
    private CountDownTimer mTimer;
    private boolean mMachPhoneNum;
    private int mRequestType;
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
                                NewLoginActivity.show(ChangePhoneActivity.this, responseString);
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


    public static void show(Context context, String str) {
        Intent intent = new Intent(context, ChangePhoneActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("str", str);
        context.startActivity(intent);
    }

    /**
     * show the login activity
     *
     * @param context context
     */
    public static void show(Context context) {
        Intent intent = new Intent(context, ChangePhoneActivity.class);
        context.startActivity(intent);
    }

    /**
     * show the login activity
     *
     * @param context context
     */
    public static void show(Activity context, int requestCode) {
        Intent intent = new Intent(context, ChangePhoneActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * show the login activity
     *
     * @param fragment fragment
     */
    public static void show(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), ChangePhoneActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_change_phone;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        etLoginUsername.setOnFocusChangeListener(this);
        etCode.setOnFocusChangeListener(this);
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
                mMachPhoneNum = RichTextParser.machPhoneNum(username);
                btLoginSubmit.setEnabled(mMachPhoneNum);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();//必须要,用来注册本地广播
    }

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
                llLoginCode.setActivated(false);
            }
        } else {
            if (hasFocus) {
                llLoginUsername.setActivated(false);
                llLoginCode.setActivated(true);
            }
        }
    }

    @OnClick({R.id.iv_login_username_del, R.id.tv_register_sms_call,
            R.id.bt_login_submit, R.id.lay_login_container})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_login_username_del:
                etLoginUsername.setText(null);
                break;
            case R.id.tv_register_sms_call:
                requestSmsCode();
                break;
            case R.id.bt_login_submit:
                requestChange();
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
            LoginApi.codeSend(phoneNumber, "change_phone", mHandler);
        } else {
            AppContext.showToast(getResources().getString(R.string.register_sms_wait_hint), Toast.LENGTH_SHORT);
        }
    }

    private void requestChange() {
        mRequestType = 2;
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
        String password = etCode.getText().toString();
        LoginApi.changePhone(phoneNumber, smsCode, mHandler);
    }
}
