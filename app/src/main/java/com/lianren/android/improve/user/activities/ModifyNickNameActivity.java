package com.lianren.android.improve.user.activities;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.improve.account.constants.UserConstants;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.MessageWrap;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.widget.BottomLineEditText;
import com.lianren.android.widget.SimplexToast;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

/**
 * @package: com.lianren.android.improve.user.activities
 * @user:xhkj
 * @date:2019/12/25
 * @description:修改昵称
 **/
public class ModifyNickNameActivity extends BackActivity {
    public static final int TYPE_NICKNAME = 1;
    private int mType;

    @Bind(R.id.et_data)
    BottomLineEditText mEditData;
    private UsersInfoBean mUser;

    public static void show(Activity activity, UsersInfoBean info, int type) {
        Intent intent = new Intent(activity, ModifyNickNameActivity.class);
        intent.putExtra("user_info", info);
        intent.putExtra("type", type);
        activity.startActivityForResult(intent, type);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_modify_nick_name;
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
        mUser = (UsersInfoBean) getIntent().getSerializableExtra("user_info");
        mType = getIntent().getIntExtra("type", 0);
        if (mUser == null || mType == 0) {
            finish();
            return;
        }
        if (mType == TYPE_NICKNAME) {
            mEditData.setMaxCount(16);
            mEditData.setSingleLine();
            mEditData.setText(mUser.base.nickname);
        }
        mEditData.setSelection(mEditData.getText().toString().length());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_commit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_commit) {
            String text = mEditData.getText().toString().trim();
            if (TextUtils.isEmpty(text)) {
                return false;
            }
            if (mType == TYPE_NICKNAME) {
                modifyNickname(text);
            }
        }
        return false;
    }

    private void modifyNickname(final String text) {
        showLoadingDialog("正在修改昵称...");
        LRApi.upDateUserInfo(text, UserConstants.NICKNAME,
                new CommonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        if (isDestroy()) {
                            return;
                        }
                        dismissLoadingDialog();
                        SimplexToast.show(ModifyNickNameActivity.this, "网络错误");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        if (isDestroy()) {
                            return;
                        }
                        dismissLoadingDialog();
                        try {
                            Type type = new TypeToken<ResultBean>() {
                            }.getType();
                            ResultBean bean = new Gson().fromJson(responseString, type);
                            if (bean.isSuccess()) {
                                EventBus.getDefault().post(new MessageWrap(Constants.REFRESH_USER, null));
                                Intent intent = new Intent();
                                mUser.base.nickname = text;
                                intent.putExtra("user_info", mUser);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                SimplexToast.show(ModifyNickNameActivity.this, bean.error.message);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            if (isDestroy()) {
                                return;
                            }
                            SimplexToast.show(ModifyNickNameActivity.this, "修改失败");
                        }
                    }
                });
    }
}

