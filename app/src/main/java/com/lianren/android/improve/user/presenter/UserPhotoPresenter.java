package com.lianren.android.improve.user.presenter;

import com.google.gson.reflect.TypeToken;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.MessageWrap;
import com.lianren.android.improve.bean.PhotoBean;
import com.lianren.android.improve.bean.base.ResultBean;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * @package: com.lianren.android.improve.home.presenter
 * @user:xhkj
 * @date:2019/12/18
 * @description:用户相册
 **/
public class UserPhotoPresenter implements UserPhotoContract.Presenter {
    private final UserPhotoContract.View mView;

    public UserPhotoPresenter(UserPhotoContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
    }

    @Override
    public void usersPhotoList() {
        LRApi.usersPhotoList(new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.request_error_hint);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<List<PhotoBean>>>() {
                    }.getType();
                    ResultBean<List<PhotoBean>> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess())
                        mView.showUsersPhotoList(resultBean.data);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    @Override
    public void userPhotoSaveAll(List<PhotoBean> mList) {
        LRApi.userPhotosSaveAll(mList, new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.request_error_hint);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<List<PhotoBean>>>() {
                    }.getType();
                    ResultBean<List<PhotoBean>> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        mView.showUsersPhotoList(resultBean.data);
                        EventBus.getDefault().post(new MessageWrap(Constants.REFRESH_USER, null));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }
}
