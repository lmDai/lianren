package com.lianren.android.improve.explore.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.reflect.TypeToken;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.EventApi;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.base.ResultBean;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * @package: com.lianren.android.improve.explore.activities
 * @user:xhkj
 * @date:2019/12/31
 * @description:修改报名信息
 **/
public class ContactUpdateActivity extends BackActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.edit_phone)
    EditText editPhone;
    @Bind(R.id.edit_nick_name)
    EditText editNickName;
    @Bind(R.id.btn_comfire)
    Button btnComfire;
    private String phone;
    private String nickeName;
    private String order_id;

    public static void show(Activity activity, String order_id, String phone, String nickname, int requestCode) {
        Intent intent = new Intent();
        intent.putExtra("order_id", order_id);
        intent.putExtra("phone", phone);
        intent.putExtra("nickname", nickname);
        intent.setClass(activity, ContactUpdateActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_contact_update;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
    }

    @Override
    protected void initData() {
        super.initData();
        phone = getIntent().getStringExtra("phone");
        nickeName = getIntent().getStringExtra("nickname");
        order_id = getIntent().getStringExtra("order_id");
        editPhone.setText(phone);
        editNickName.setText(nickeName);
    }

    @OnClick(R.id.btn_comfire)
    public void onViewClicked() {
        String phone = editPhone.getText().toString();
        String nickName = editNickName.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            AppContext.showToast("请输入手机号");
            return;
        }
        if (TextUtils.isEmpty(nickName)) {
            AppContext.showToast("请输入姓名");
            return;
        }
        contactUpdate(phone, nickName);
    }

    private void contactUpdate(final String phone, final String nickName) {
        EventApi.orderContactUpdate(phone, nickName, order_id, new CommonHttpResponseHandler() {
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
            public void onStart() {
                super.onStart();
                showLoadingDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean>() {
                    }.getType();

                    ResultBean resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        Intent intent = new Intent();
                        intent.putExtra("phone", phone);
                        intent.putExtra("nickName", nickName);
                        setResult(RESULT_OK, intent);
                        finish();
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
