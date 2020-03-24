package com.lianren.android.improve.explore.presenter;

import com.google.gson.reflect.TypeToken;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.EventApi;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.bean.DatingDetailBean;
import com.lianren.android.improve.bean.ShopGoodsBean;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.util.TDevice;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * @package: com.lianren.android.improve.user.presenter
 * @user:xhkj
 * @date:2019/12/20
 * @description:
 **/
public class InviteStatusPresenter implements InviteStatusContract.Presenter {
    private final InviteStatusContract.View mView;


    public InviteStatusPresenter(InviteStatusContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
    }


    @Override
    public void getDatingDetail(int dating_id ) {
        LRApi.datingDetail(dating_id, new CommonHttpResponseHandler() {
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
                    Type type = new TypeToken<ResultBean<DatingDetailBean>>() {
                    }.getType();

                    ResultBean<DatingDetailBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        mView.showDatingDetail(resultBean.data);
                    } else {
                        mView.showNetworkError(R.string.tip_network_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    @Override
    public void datingRequestDeal(int dating_id,int status) {
        EventApi.datingRequestDeal(dating_id,status,new CommonHttpResponseHandler() {
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
                    Type type = new TypeToken<ResultBean>() {
                    }.getType();
                    ResultBean resultBean = AppOperator.createGson().fromJson(responseString, type);
                    mView.showResult(resultBean);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    @Override
    public void datingCancel(int dating_id) {
        EventApi.datingCancel(dating_id,new CommonHttpResponseHandler() {
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
                    Type type = new TypeToken<ResultBean>() {
                    }.getType();
                    ResultBean resultBean = AppOperator.createGson().fromJson(responseString, type);
                    mView.showResult(resultBean);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

}
