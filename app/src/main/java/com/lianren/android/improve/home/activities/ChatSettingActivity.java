package com.lianren.android.improve.home.activities;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.improve.account.constants.UserConstants;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.improve.feedback.FeedTypeActivity;
import com.lianren.android.improve.user.activities.UserInfoActivity;

import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * @package: com.lianren.android.improve.home.activities
 * @user:xhkj
 * @date:2019/12/31
 * @description:聊天设置
 **/
public class ChatSettingActivity extends BackActivity {
    private UsersInfoBean.BaseBean mReceiver;

    public static void show(Context context, UsersInfoBean.BaseBean sender) {
        Intent intent = new Intent(context, ChatSettingActivity.class);
        intent.putExtra("receiver", sender);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_chat_setting;
    }

    private long mLastClickTime;

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();

    }

    @Override
    protected void initData() {
        super.initData();
        mReceiver = (UsersInfoBean.BaseBean) getIntent().getSerializableExtra("receiver");
    }

    @OnClick({R.id.ll_invite, R.id.ll_user_info, R.id.ll_feed_back, R.id.ll_shiled})
    public void onViewClicked(View view) {
        long nowTime = System.currentTimeMillis();
        if ((nowTime - mLastClickTime) < 500)
            return;
        mLastClickTime = nowTime;
        switch (view.getId()) {
            case R.id.ll_invite:
                InviteBaseActivity.show(this, mReceiver);
                finish();
                break;
            case R.id.ll_user_info:
                UserInfoActivity.show(this, mReceiver.id, mReceiver.uuid);
                finish();
                break;
            case R.id.ll_feed_back:
                FeedTypeActivity.show(this, UserConstants.FEED_USER, mReceiver.uuid);
                break;
            case R.id.ll_shiled:
                dealBlackAdd(mReceiver.id);
                break;
        }
    }

    public void dealBlackAdd(final int id) {
        LRApi.contactBlackAdd(id,
                new CommonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            ResultBean bean = new Gson().fromJson(responseString,
                                    new TypeToken<ResultBean>() {
                                    }.getType());
                            if (bean != null) {
                                if (bean.isSuccess()) {
                                    finish();
                                } else {

                                }
                            } else {

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
