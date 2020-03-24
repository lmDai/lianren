package com.lianren.android.improve.home.presenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.MatchingBean;
import com.lianren.android.improve.bean.MessageWrap;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.ui.empty.EmptyLayout;
import com.lianren.android.util.TDevice;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * @package: com.lianren.android.improve.home.presenter
 * @user:xhkj
 * @date:2019/12/18
 * @description:
 **/
public class MatchingPresenter implements MatchingContract.Presenter {
    private final MatchingContract.View mView;
    private final MatchingContract.EmptyView mEmptyView;

    public MatchingPresenter(MatchingContract.View mView, MatchingContract.EmptyView mEmptyView) {
        this.mView = mView;
        this.mEmptyView = mEmptyView;
        this.mView.setPresenter(this);
    }

    @Override
    public void getMatchings() {
        LRApi.usersHome(new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mEmptyView.showErrorLayout(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onCancel() {
                super.onCancel();
                if (!TDevice.hasInternet())
                    mEmptyView.showErrorLayout(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<MatchingBean>>() {
                    }.getType();
                    ResultBean<MatchingBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()||resultBean.code==ResultBean.RESULT_FAIL){
                        EventBus.getDefault().post(new MessageWrap(Constants.CLOSE_BREAK, null));
                    }
                    if (resultBean.isSuccess()) {
                        if (resultBean.data.user != null && resultBean.data.user.size() > 0) {
                            mEmptyView.hideEmptyLayout();
                            mView.showMatchings(resultBean.data);
                        } else {
                            mEmptyView.showErrorLayout(EmptyLayout.NODATA);
                        }
                    } else {
                        mEmptyView.showErrorLayout(EmptyLayout.NETWORK_ERROR);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    @Override
    public void pairsDisLike(final MatchingBean.UserBean tags, final int position) {
        LRApi.pairsDislike(tags.remote_id,
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
                                    mView.showDeleteSuccess(tags, position);
                                }else {
                                    mView.showDeleteFaile(bean.error.message);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
