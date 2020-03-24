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

import cz.msebera.android.httpclient.Header;

/**
 * @package: com.lianren.android.improve.home.presenter
 * @user:xhkj
 * @date:2019/12/18
 * @description:
 **/
public class PhotoPresenter implements PhotoContract.Presenter {
    private final PhotoContract.View mView;

    public PhotoPresenter(PhotoContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
    }

    @Override
    public void deletePhoto(int photo_id) {
        LRApi.photosDelete(photo_id, new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.request_error_hint);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<PhotoBean>>() {
                    }.getType();
                    ResultBean<PhotoBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    mView.showDeleteResult(resultBean);
                    EventBus.getDefault().post(new MessageWrap(Constants.REFRESH_USER, null));
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    @Override
    public void updatePhoto(int photo_id, String img_uri, int location) {
        LRApi.userImageUpload(photo_id, img_uri,location,new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.request_error_hint);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<PhotoBean>>() {
                    }.getType();
                    ResultBean<PhotoBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    mView.showUpdateResult(resultBean);
                    EventBus.getDefault().post(new MessageWrap(Constants.REFRESH_USER, null));
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    @Override
    public void upLoadPhoto(String img_uri, int location) {
        LRApi.userImageUpload(img_uri,location,new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.request_error_hint);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<PhotoBean>>() {
                    }.getType();
                    ResultBean<PhotoBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    mView.showUpLoadResult(resultBean);
                    EventBus.getDefault().post(new MessageWrap(Constants.REFRESH_USER, null));
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }
}
