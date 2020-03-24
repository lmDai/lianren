package com.lianren.android.improve.user.presenter;

import com.google.gson.reflect.TypeToken;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.bean.base.ResultBean;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * @package: com.lianren.android.improve.home.presenter
 * @user:xhkj
 * @date:2019/12/18
 * @description:
 **/
public class UsersInfoPresenter implements UsersInfoContract.Presenter {
    private final UsersInfoContract.View mView;

    public UsersInfoPresenter(UsersInfoContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
    }


    @Override
    public void getUsersInfo(String user_uuid, int user_id) {
        LRApi.usersInfo(user_uuid, user_id, new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.request_error_hint);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<UsersInfoBean>>() {
                    }.getType();
                    ResultBean<UsersInfoBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        mView.showUsersInfo(resultBean.data);
                    } else {
                        mView.showError(resultBean.error.message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }
}
