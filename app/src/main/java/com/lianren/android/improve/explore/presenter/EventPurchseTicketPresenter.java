package com.lianren.android.improve.explore.presenter;

import com.google.gson.reflect.TypeToken;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.bean.EventTicketBean;
import com.lianren.android.improve.bean.TicketOrderBean;
import com.lianren.android.improve.bean.TicketOrderDetailBean;
import com.lianren.android.improve.bean.base.ResultBean;
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
public class EventPurchseTicketPresenter implements EventPurchseTicketContract.Presenter {
    private final EventPurchseTicketContract.View mView;


    public EventPurchseTicketPresenter(EventPurchseTicketContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
    }


    @Override
    public void getActivityGood(String activity_id, int source) {
        LRApi.activityGood(activity_id, source, new CommonHttpResponseHandler() {
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
                    Type type = new TypeToken<ResultBean<EventTicketBean>>() {
                    }.getType();

                    ResultBean<EventTicketBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        mView.showEventGood(resultBean.data);
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
    public void createOrder(int type, String obj_id, List<TicketOrderBean> goods) {
        LRApi.createOrder(type,obj_id, goods, new CommonHttpResponseHandler() {
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
                    Type type = new TypeToken<ResultBean<TicketOrderDetailBean>>() {
                    }.getType();

                    ResultBean<TicketOrderDetailBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    mView.showResult(resultBean);

                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }
}
