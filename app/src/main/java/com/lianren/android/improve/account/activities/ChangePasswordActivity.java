package com.lianren.android.improve.account.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.reflect.TypeToken;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LoginApi;
import com.lianren.android.improve.account.AccountHelper;
import com.lianren.android.improve.account.base.AccountBaseActivity;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.bean.base.ResultBean;

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
public class ChangePasswordActivity extends AccountBaseActivity implements View.OnFocusChangeListener, TextWatcher {

    @Bind(R.id.et_login_username)
    EditText etLoginUsername;
    @Bind(R.id.ll_login_username)
    LinearLayout llLoginUsername;
    @Bind(R.id.et_new_password)
    EditText etNewPassword;
    @Bind(R.id.ll_new_password)
    LinearLayout llNewPassword;
    @Bind(R.id.et_comfir_password)
    EditText etComfirPassword;
    @Bind(R.id.ll_comfire)
    LinearLayout llComfire;
    @Bind(R.id.bt_login_submit)
    Button btLoginSubmit;
    String identifier;

    public static void show(Context context, String str) {
        Intent intent = new Intent(context, ChangePasswordActivity.class);
        intent.putExtra("str", str);
        context.startActivity(intent);
    }


    /**
     * show the login activity
     *
     * @param fragment fragment
     */
    public static void show(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), ChangePasswordActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_change_password;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        etLoginUsername.setOnFocusChangeListener(this);
        etNewPassword.setOnFocusChangeListener(this);
        etComfirPassword.setOnFocusChangeListener(this);
        etLoginUsername.addTextChangedListener(this);
        etNewPassword.addTextChangedListener(this);
        etComfirPassword.addTextChangedListener(this);
    }

    @Override
    protected void initData() {
        super.initData();//必须要,用来注册本地广播
        identifier = getIntent().getStringExtra("str");
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
                llNewPassword.setActivated(false);
                llComfire.setActivated(false);
            }
        } else if (id == R.id.et_new_password) {
            if (hasFocus) {
                llLoginUsername.setActivated(false);
                llNewPassword.setActivated(true);
                llComfire.setActivated(false);
            }
        } else if (id == R.id.et_comfir_password) {
            if (hasFocus) {
                llLoginUsername.setActivated(false);
                llNewPassword.setActivated(false);
                llComfire.setActivated(true);
            }
        }
    }

    @OnClick({R.id.tv_forget_password, R.id.bt_login_submit, R.id.lay_login_container})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_forget_password:
                ForgetPasswordActivity.show(this);
                break;
            case R.id.bt_login_submit:
                changePassword();
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

    private void changePassword() {
        String credential = etLoginUsername.getText().toString();
        String new_credential = etNewPassword.getText().toString();
        String confirm = etComfirPassword.getText().toString();
        if (!TextUtils.equals(new_credential, confirm)) {
            AppContext.showToast("两次密码输入不一致");
            return;
        }
        LoginApi.passportsChange(identifier, credential, new_credential, new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                requestFailureHint(throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean>() {
                    }.getType();
                    ResultBean resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        AccountHelper.logout(btLoginSubmit, new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                finish();
                            }
                        });
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String comfir = etComfirPassword.getText().toString();
        String newPass = etNewPassword.getText().toString();
        String orianal = etLoginUsername.getText().toString();
        btLoginSubmit.setEnabled(!TextUtils.isEmpty(comfir) && !TextUtils.isEmpty(newPass) && !TextUtils.isEmpty(orianal));
    }
}
