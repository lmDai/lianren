package com.lianren.android.improve.user.presenter;

import com.google.gson.reflect.TypeToken;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.bean.ContactUserBean;
import com.lianren.android.improve.bean.base.ResultPageBean;
import com.lianren.android.ui.empty.EmptyLayout;
import com.lianren.android.util.TDevice;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * @package: com.lianren.android.improve.user.presenter
 * @user:xhkj
 * @date:2019/12/20
 * @description:
 **/
public class ContactLikePresenter implements ContactLikeContract.Presenter {
    private final ContactLikeContract.View mView;
    private final ContactLikeContract.EmptyView mEmptyView;

    public ContactLikePresenter(ContactLikeContract.View mView, ContactLikeContract.EmptyView mEmptyView) {
        this.mView = mView;
        this.mEmptyView = mEmptyView;
        this.mView.setPresenter(this);
    }

    @Override
    public void getContactUser(int page, int type) {
        LRApi.contactLike(page, type, new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.tip_network_error);
                mEmptyView.showErrorLayout(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onCancel() {
                super.onCancel();
                if (!TDevice.hasInternet()) {
                    mView.showNetworkError(R.string.tip_network_error);
                    mEmptyView.showErrorLayout(EmptyLayout.NETWORK_ERROR);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultPageBean<ContactUserBean>>() {
                    }.getType();

                    ResultPageBean<ContactUserBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.data.total_num > 0) {
                        mEmptyView.hideEmptyLayout();
                        mView.showContactUser(resultBean.data.items);
                    } else {
                        mEmptyView.showErrorLayout(EmptyLayout.NODATA);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }
}
