package com.lianren.android.improve.account.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.reflect.TypeToken;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.api.remote.LoginApi;
import com.lianren.android.base.BaseDialog;
import com.lianren.android.base.BaseDialogFragment;
import com.lianren.android.improve.account.base.AccountBaseActivity;
import com.lianren.android.improve.account.constants.UserConstants;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.bean.UriBean;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.improve.main.WebActivity;
import com.lianren.android.improve.main.update.LRSharedPreference;
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
public class NewLoginActivity extends AccountBaseActivity implements View.OnFocusChangeListener {
    public static final String HOLD_USERNAME_KEY = "holdUsernameKey";
    @Bind(R.id.iv_login_logo)
    ImageView ivLoginLogo;
    @Bind(R.id.et_login_username)
    EditText etLoginUsername;
    @Bind(R.id.iv_login_username_del)
    ImageView ivLoginUsernameDel;
    @Bind(R.id.ll_login_username)
    LinearLayout llLoginUsername;
    @Bind(R.id.bt_login_submit)
    Button btLoginSubmit;
    private boolean mMachPhoneNum;


    public static void show(Context context, String str) {
        Intent intent = new Intent(context, NewLoginActivity.class);
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
        Intent intent = new Intent(context, NewLoginActivity.class);
        context.startActivity(intent);
    }

    /**
     * show the login activity
     *
     * @param context context
     */
    public static void show(Activity context, int requestCode) {
        Intent intent = new Intent(context, NewLoginActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * show the login activity
     *
     * @param fragment fragment
     */
    public static void show(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), NewLoginActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main_login_new;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setSwipeBackEnable(false);
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
                mMachPhoneNum = RichTextParser.machPhoneNum(username);
                btLoginSubmit.setEnabled(mMachPhoneNum);

            }
        });
    }

    private void showAgreementDialog() {
        new BaseDialogFragment.Builder(this)
                .setContentView(R.layout.dialog_privacy)
                .setAnimStyle(BaseDialog.AnimStyle.IOS)
                .setGravity(Gravity.CENTER)
                .setCancelable(false)
                .setOnClickListener(R.id.txt_privacy, new BaseDialog.OnClickListener() {
                    @Override
                    public void onClick(BaseDialog dialog, View view) {
                        appPrivcy();
                    }
                })
                .setOnClickListener(R.id.txt_agreement, new BaseDialog.OnClickListener() {
                    @Override
                    public void onClick(BaseDialog dialog, View view) {
                        appAgreeMent();
                    }
                })
                .setOnClickListener(R.id.tvNegativeButton, new BaseDialog.OnClickListener() {
                    @Override
                    public void onClick(BaseDialog dialog, View view) {
                        dialog.dismiss();
                        LRSharedPreference.getInstance().putFirstUsing();
                    }
                })
                .setOnClickListener(R.id.tvPositiveButton, new BaseDialog.OnClickListener() {
                    @Override
                    public void onClick(BaseDialog dialog, View view) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .show();
    }

    @Override
    protected void initData() {
        super.initData();//必须要,用来注册本地广播
        //初始化控件状态数据
        SharedPreferences sp = getSharedPreferences(UserConstants.HOLD_ACCOUNT, Context.MODE_PRIVATE);
        String holdUsername = sp.getString(HOLD_USERNAME_KEY, null);
        etLoginUsername.setText(holdUsername);
        if (LRSharedPreference.getInstance().isFirstUsing()) {
            showAgreementDialog();
        }
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

    private void loginRequest() {
        String tempUsername = etLoginUsername.getText().toString().trim();
        if (!TextUtils.isEmpty(tempUsername)) {
            if (TDevice.hasInternet()) {
                requestLogin(tempUsername);
            } else {
                showToastForKeyBord(R.string.footer_type_net_error);
            }
        } else {
            showToastForKeyBord(R.string.login_input_username_hint_error);
        }
    }

    private void requestLogin(String tempUsername) {
        LoginApi.codeSend(tempUsername, "register", new CommonHttpResponseHandler() {
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
                    CodeLoginActivity.show(NewLoginActivity.this, etLoginUsername.getText().toString(), 1);
                } else {
                    InputCodeActivity.show(NewLoginActivity.this, etLoginUsername.getText().toString(), 1);
                }
            }
        });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();
        if (id == R.id.et_login_username) {
            if (hasFocus) {
                llLoginUsername.setActivated(true);
            }
        } else {
            if (hasFocus) {
                llLoginUsername.setActivated(false);
            }
        }
    }


    @OnClick({R.id.bt_login_submit, R.id.tv_user_agent,
            R.id.et_login_username,
            R.id.iv_login_username_del,
            R.id.lay_login_container})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_login_submit://登录
                loginRequest();
                break;
            case R.id.tv_user_agent://用户协议
                appAgreeMent();
                break;
            case R.id.et_login_username:
                etLoginUsername.setFocusableInTouchMode(true);
                etLoginUsername.requestFocus();
                break;
            case R.id.iv_login_username_del:
                etLoginUsername.setText(null);
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

    private void appAgreeMent() {
        LRApi.appAgreeMent(new CommonHttpResponseHandler() {
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
                    Type type = new TypeToken<ResultBean<UriBean>>() {
                    }.getType();

                    ResultBean<UriBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        WebActivity.show(NewLoginActivity.this, resultBean.data.uri);
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

    private void appPrivcy() {
        LRApi.appPrivacy(new CommonHttpResponseHandler() {
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
                    Type type = new TypeToken<ResultBean<UriBean>>() {
                    }.getType();

                    ResultBean<UriBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        WebActivity.show(NewLoginActivity.this, resultBean.data.uri);
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
}
