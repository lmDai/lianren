package com.lianren.android.improve.explore.presenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.improve.bean.TagBean;
import com.lianren.android.improve.bean.base.ResultBean;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * 用户搜索标签界面
 * Created by haibin on 2018/05/28.
 */
public class SearchTagsPresenter implements SearchTagsContract.Presenter {
    private final SearchTagsContract.View mView;

    public SearchTagsPresenter(SearchTagsContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
    }

    @Override
    public void search(String keyword) {
        LRApi.userNoteTagList(keyword, new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showSearchFailure(R.string.network_timeout_hint);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<TagBean> bean = new Gson().fromJson(responseString, getType());
                    if (bean != null) {
                        if (bean.isSuccess()) {
                            mView.showSearchSuccess(bean.data.tags);
                        }
                    } else {
                        mView.showSearchFailure(R.string.search_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void tagAdd(String keyword) {
        LRApi.userNoteTagAdd(keyword, new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showSearchFailure(R.string.network_timeout_hint);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<TagBean> bean = new Gson().fromJson(responseString, getType());
                    if (bean != null) {
                        if (bean.isSuccess()) {
                            mView.showTagAdd();
                        }
                    } else {
                        mView.showSearchFailure(R.string.search_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static Type getType() {
        return new TypeToken<ResultBean<TagBean>>() {
        }.getType();
    }
}
