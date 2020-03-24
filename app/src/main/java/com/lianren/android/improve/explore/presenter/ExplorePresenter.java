package com.lianren.android.improve.explore.presenter;

import com.google.gson.reflect.TypeToken;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.bean.EventBean;
import com.lianren.android.improve.bean.ImprintsBean;
import com.lianren.android.improve.bean.NoticeNoteCountBean;
import com.lianren.android.improve.bean.PickStatusBean;
import com.lianren.android.improve.bean.TagBean;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.improve.bean.base.ResultPageBean;
import com.lianren.android.util.TDevice;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * @package: com.lianren.android.improve.home.presenter
 * @user:xhkj
 * @date:2019/12/18
 * @description:
 **/
public class ExplorePresenter implements ExploreContract.Presenter {
    private final ExploreContract.View mView;

    public ExplorePresenter(ExploreContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
    }

    @Override
    public void getRecommendEvent(String longitude, String latitude) {
        LRApi.userActivitySuggest(longitude, latitude, new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.request_error_hint);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<List<EventBean>>>() {
                    }.getType();
                    ResultBean<List<EventBean>> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        mView.showRecommendEvent(resultBean.data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    @Override
    public void notiPick(final ImprintsBean bean, final int position) {
        LRApi.usersNotePick(bean.id, new CommonHttpResponseHandler() {
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
                    Type type = new TypeToken<ResultBean<PickStatusBean>>() {
                    }.getType();
                    ResultBean<PickStatusBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        mView.showPickResult(resultBean.data, bean, position);
                    } else {
                        AppContext.showToast(resultBean.error.message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    @Override
    public void getNoticeNoteCount() {
        LRApi.noticeNoteCount(new CommonHttpResponseHandler() {
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
                    Type type = new TypeToken<ResultBean<NoticeNoteCountBean>>() {
                    }.getType();
                    ResultBean<NoticeNoteCountBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        mView.showNoticeNoteCount(resultBean.data.count);
                    } else {
                        AppContext.showToast(resultBean.error.message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    @Override
    public void getNoteRecommend(int user_id, int page) {
        LRApi.userNoteRecommed(user_id, null, page, new CommonHttpResponseHandler() {
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
                    Type type = new TypeToken<ResultPageBean<ImprintsBean>>() {
                    }.getType();

                    ResultPageBean<ImprintsBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.data.total_num == 0) {
                        mView.showNetworkError(R.string.error_view_no_data);
                    } else {
                        mView.showImprintsList(resultBean.data.items);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    @Override
    public void getNoteTags() {
        LRApi.userNoteTags(new CommonHttpResponseHandler() {
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
                    Type type = new TypeToken<ResultBean<TagBean>>() {
                    }.getType();

                    ResultBean<TagBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        mView.showNoteTags(resultBean.data.tags);
                    } else {
                        mView.showNetworkError(R.string.error_view_no_data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }
}
