package com.lianren.android.improve.user.activities;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.lianren.android.R;
import com.lianren.android.improve.account.activities.ChangePasswordActivity;
import com.lianren.android.improve.account.activities.ChangePhoneActivity;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.util.RichTextParser;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @package: com.lianren.android.improve.user.activities
 * @user:xhkj
 * @date:2019/12/24
 * @description:设置
 **/
public class RegisterInfoActivity extends BackActivity {
    private UsersInfoBean usersInfoBean;
    @Bind(R.id.tv_phone)
    TextView tvPhone;

    public static void show(Context mContext, UsersInfoBean usersInfoBean) {
        Intent intent = new Intent();
        intent.putExtra("user_info", usersInfoBean);
        intent.setClass(mContext, RegisterInfoActivity.class);
        mContext.startActivity(intent);
    }


    @Override
    protected int getContentView() {
        return R.layout.activity_register_info;
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
        usersInfoBean = (UsersInfoBean) getIntent().getSerializableExtra("user_info");
        tvPhone.setText(RichTextParser.showPhone(usersInfoBean.base.phone));
    }

    @OnClick({R.id.ll_change_phone, R.id.ll_change_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_change_phone:
                ChangePhoneActivity.show(this);
                break;
            case R.id.ll_change_password:
                ChangePasswordActivity.show(this, usersInfoBean.base.phone);
                break;
        }
    }
}
