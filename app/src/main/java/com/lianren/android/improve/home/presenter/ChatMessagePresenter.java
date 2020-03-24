package com.lianren.android.improve.home.presenter;

import com.google.gson.reflect.TypeToken;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.bean.ContactsStautusBean;
import com.lianren.android.improve.bean.Message;
import com.lianren.android.improve.bean.StatusBean;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.improve.bean.base.ResultPageBean;
import com.lianren.android.util.TDevice;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * @package: com.lianren.android.improve.user.presenter
 * @user:xhkj
 * @date:2019/12/20
 * @description:
 **/
public class ChatMessagePresenter implements ChatMessageContract.Presenter {
    private final ChatMessageContract.View mView;

    public ChatMessagePresenter(ChatMessageContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
    }

    @Override
    public void getChatMessage(String remote_id, int page) {
        LRApi.getChatMessage(remote_id, page, new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.tip_network_error);
            }

            @Override
            public void onCancel() {
                super.onCancel();
                if (!TDevice.hasInternet()) {
                    mView.showNetworkError(R.string.tip_network_error);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultPageBean<Message>>() {
                    }.getType();
                    ResultPageBean<Message> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    mView.showChatMessage(resultBean.data.items);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    @Override
    public void getContactsStatus(int user_id) {
        LRApi.usersContactsStatus(user_id, new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.request_error_hint);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<ContactsStautusBean>>() {
                    }.getType();
                    ResultBean<ContactsStautusBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        if (resultBean.data.status == 1) {//联系人
                            mView.showResult(1);
                        } else {
                            mView.showResult(0);
                        }
                    } else {
                        mView.showResult(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    @Override
    public void getDatingOpenStatus(String longitude, String latitude) {
        LRApi.datingOpenStatus(longitude, latitude, new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.tip_network_error);
            }

            @Override
            public void onCancel() {
                super.onCancel();
                if (!TDevice.hasInternet()) {
                    mView.showNetworkError(R.string.tip_network_error);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<StatusBean>>() {
                    }.getType();
                    ResultBean<StatusBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    mView.showDatingStatus(resultBean.data);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }
}
