package com.lianren.android.improve.explore.presenter;

import com.google.gson.reflect.TypeToken;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.EventApi;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.bean.OrderTicketBean;
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
public class OrderTicketPresenter implements OrderTicketContract.Presenter {
    private final OrderTicketContract.View mView;

    public OrderTicketPresenter(OrderTicketContract.View mView) {
        this.mView = mView;

        this.mView.setPresenter(this);
    }

    @Override
    public void getOrderTicket(String order_id) {
        EventApi.orderTicket(order_id, new CommonHttpResponseHandler() {
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
                    Type type = new TypeToken<ResultBean<OrderTicketBean>>() {
                    }.getType();

                    ResultBean<OrderTicketBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        mView.showOrderTicket(resultBean.data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }
}
