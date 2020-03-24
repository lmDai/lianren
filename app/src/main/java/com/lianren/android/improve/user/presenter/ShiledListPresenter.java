package com.lianren.android.improve.user.presenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.bean.ContactUserBean;
import com.lianren.android.improve.bean.PairListBean;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.ui.empty.EmptyLayout;
import com.lianren.android.util.TDevice;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * @package: com.lianren.android.improve.user.presenter
 * @user:xhkj
 * @date:2019/12/20
 * @description:
 **/
public class ShiledListPresenter implements ShiledListContract.Presenter {
    private final ShiledListContract.View mView;
    private final ShiledListContract.EmptyView mEmptyView;

    public ShiledListPresenter(ShiledListContract.View mView, ShiledListContract.EmptyView mEmptyView) {
        this.mView = mView;
        this.mEmptyView = mEmptyView;
        this.mView.setPresenter(this);
    }

    @Override
    public void contactBlackList(int page) {
        LRApi.contactBlackList(page, new CommonHttpResponseHandler() {
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
                    Type type = new TypeToken<ResultBean<List<ContactUserBean>>>() {
                    }.getType();

                    ResultBean<List<ContactUserBean>> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()&& resultBean.data!=null && resultBean.data.size()>0) {
                        mEmptyView.hideEmptyLayout();
                        mView.showContactUser(resultBean.data);
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

    @Override
    public void dealBlackRevert(final ContactUserBean tags, final int position ) {
        LRApi.contactBlackRevert(tags.id,
                new CommonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        mView.showDeleteFailure(R.string.delete_failed);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            ResultBean bean = new Gson().fromJson(responseString,
                                    new TypeToken<ResultBean>() {
                                    }.getType());
                            if (bean != null) {
                                if (bean.isSuccess()) {
                                    mView.showDeleteSuccess(tags, position);
                                } else {
                                    mView.showDeleteFailure(bean.error.message);
                                }
                            } else {
                                mView.showDeleteFailure(R.string.request_error_hint);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
