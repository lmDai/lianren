package com.lianren.android.improve.user.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.improve.account.AccountHelper;
import com.lianren.android.improve.account.constants.UserConstants;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.MessageWrap;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.util.DialogHelper;
import com.lianren.android.util.UIHelper;
import com.lianren.android.widget.SimplexToast;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * @package: com.lianren.android.improve.user.activities
 * @user:xhkj
 * @date:2019/12/24
 * @description:交友状态
 **/
public class UserStatusActivity extends BackActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_my_setting_friend_single)
    TextView tvMySettingFriendSingle;
    @Bind(R.id.tv_my_setting_friend_disable)
    TextView tvMySettingFriendDisable;
    @Bind(R.id.tv_my_setting_friend_close)
    TextView tvMySettingFriendClose;
    private UsersInfoBean usersInfoBean;
    private String currentState;

    public static void show(Context mContext, UsersInfoBean usersInfoBean) {
        Intent intent = new Intent();
        intent.putExtra("user_info", usersInfoBean);
        intent.setClass(mContext, UserStatusActivity.class);
        mContext.startActivity(intent);
    }


    @Override
    protected int getContentView() {
        return R.layout.activity_user_status;
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
        singleOption(usersInfoBean.base.status);
    }

    private void setSelectView(TextView view) {
        Drawable drawableRight = getResources().getDrawable(R.mipmap.selected_ico);
        drawableRight.setBounds(0, 0, drawableRight.getMinimumWidth(), drawableRight.getMinimumHeight());
        view.setCompoundDrawables(null, null, drawableRight, null);
    }

    private void singleOption(String userState) {
        if ("using".equals(userState)) {
            setSelectView(tvMySettingFriendSingle);
            tvMySettingFriendDisable.setCompoundDrawables(null, null, null, null);
            tvMySettingFriendClose.setCompoundDrawables(null, null, null, null);
        } else if ("pause".equals(userState)) {
            setSelectView(tvMySettingFriendDisable);
            tvMySettingFriendSingle.setCompoundDrawables(null, null, null, null);
            tvMySettingFriendClose.setCompoundDrawables(null, null, null, null);
        } else if ("close".equals(userState)) {
            setSelectView(tvMySettingFriendClose);
            tvMySettingFriendSingle.setCompoundDrawables(null, null, null, null);
            tvMySettingFriendDisable.setCompoundDrawables(null, null, null, null);
        }
    }

    @OnClick({R.id.tv_my_setting_friend_single, R.id.tv_my_setting_friend_disable, R.id.tv_my_setting_friend_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_my_setting_friend_single:
                currentState = "using";
                updateUserInfo();
                break;
            case R.id.tv_my_setting_friend_disable:
                currentState = "pause";
                updateUserInfo();
                break;
            case R.id.tv_my_setting_friend_close:
                showConfirm();
                break;
        }
    }

    private void showConfirm() {
        currentState = "close";

        DialogHelper.getConfirmDialog(this, "提示", "关闭后，所有功能将不能使用?", "关闭", "返回", false, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateUserInfo();
                dialog.dismiss();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }

    private void updateUserInfo() {
        LRApi.upDateUserInfo(currentState, UserConstants.STATUS,
                new CommonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        SimplexToast.show(UserStatusActivity.this, "网络错误");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            Type type = new TypeToken<ResultBean>() {
                            }.getType();
                            ResultBean bean = new Gson().fromJson(responseString, type);
                            if (bean.isSuccess()) {
                                if (currentState == null || "".equals(currentState)) {
                                    return;
                                }
                                singleOption(currentState);
                                EventBus.getDefault().post(new MessageWrap(Constants.REFRESH_USER, null));
                                if (TextUtils.equals("close", currentState)) {
                                    UIHelper.clearAppCache(false);
                                    // 注销操作
                                    AccountHelper.logout(tvMySettingFriendClose, new Runnable() {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void run() {
                                            finish();
                                        }
                                    });
                                }

                            } else {
                                SimplexToast.show(UserStatusActivity.this, bean.error.message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            SimplexToast.show(UserStatusActivity.this, "修改失败");
                        }
                    }
                });
    }
}
