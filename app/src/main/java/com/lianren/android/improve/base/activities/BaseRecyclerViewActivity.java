package com.lianren.android.improve.base.activities;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.base.BaseRecyclerAdapter;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.base.BaseGeneralRecyclerAdapter;
import com.lianren.android.improve.bean.base.ResultPageBean;
import com.lianren.android.widget.RecyclerRefreshLayout;
import com.lianren.android.widget.SimplexToast;

import java.lang.reflect.Type;
import java.util.Date;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

/**
 * Created by huanghaibin_dev
 * on 16-6-23.
 */
public abstract class BaseRecyclerViewActivity<T> extends BackActivity implements
        BaseRecyclerAdapter.OnItemClickListener,
        RecyclerRefreshLayout.SuperRefreshLayoutListener,
        BaseGeneralRecyclerAdapter.Callback {

    @Bind(R.id.refreshLayout)
    protected RecyclerRefreshLayout mRefreshLayout;

    @Bind(R.id.recyclerView)
    protected RecyclerView mRecyclerView;

    protected BaseRecyclerAdapter<T> mAdapter;

    protected CommonHttpResponseHandler mHandler;

    protected ResultPageBean<T> mBean;

    protected boolean mIsRefresh;

    @Override
    protected int getContentView() {
        return R.layout.activity_base_recycler;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mAdapter = mAdapter == null ? getRecyclerAdapter() : mAdapter;
        mRecyclerView.setLayoutManager(getLayoutManager());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mRefreshLayout.setSuperRefreshLayoutListener(this);
        mRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);
    }

    @Override
    protected void initData() {
        super.initData();
        mBean = new ResultPageBean<>();
        mHandler = new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onLoadingFailure();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultPageBean<T> resultBean = AppOperator.createGson().fromJson(responseString, getType());
                    if (resultBean != null && resultBean.isSuccess() && resultBean.data.items != null) {
                        onLoadingSuccess();
                        setListData(resultBean);
                    } else {
                        if (resultBean.code == 1) {
                            SimplexToast.show(BaseRecyclerViewActivity.this, resultBean.error.message);
                        }
                        onLoadingFailure();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                onLoadingStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                onLoadingFinish();
            }
        };

        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                onRefreshing();
            }
        });
    }

    @Override
    public void onItemClick(int position, long itemId) {
        onItemClick(mAdapter.getItem(position), position);
    }

    @Override
    public void onRefreshing() {
        mIsRefresh = true;
        requestData();
    }

    @Override
    public void onLoadMore() {
        mAdapter.setState(mRefreshLayout.isRefreshing() ? BaseRecyclerAdapter.STATE_HIDE : BaseRecyclerAdapter.STATE_LOADING, true);
        requestData();
    }

    @Override
    public void onScrollToBottom() {

    }

    protected void onItemClick(T item, int position) {

    }

    protected void requestData() {

    }

    protected void setListData(ResultPageBean<T> resultBean) {
        mBean.setNextPageToken(String.valueOf(resultBean.data.page + 1));
        mBean.setPrevPageToken(String.valueOf(resultBean.data.page - 1));
        if (mIsRefresh) {
            mBean.data.items = resultBean.data.getItems();
            mAdapter.clear();
            mAdapter.addAll(mBean.data.getItems());
            mBean.setPrevPageToken(String.valueOf(resultBean.data.page - 1));
            mRefreshLayout.setCanLoadMore(true);
        } else {
            mAdapter.addAll(resultBean.data.getItems());
        }
        mAdapter.setState(resultBean.data.getItems() == null ? BaseRecyclerAdapter.STATE_NO_MORE : BaseRecyclerAdapter.STATE_LOADING, true);
    }

    protected void onLoadingStart() {

    }

    protected void onLoadingSuccess() {

    }

    protected void onLoadingFinish() {
        mRefreshLayout.onComplete();
        mIsRefresh = false;
    }

    protected void onLoadingFailure() {
        if (mAdapter.getItems().size() == 0) {
            mAdapter.setState(BaseRecyclerAdapter.STATE_LOAD_ERROR, true);
        } else {
            mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
        }
    }


    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(this);
    }

    protected abstract Type getType();

    protected abstract BaseRecyclerAdapter<T> getRecyclerAdapter();

    @Override
    public RequestManager getImgLoader() {
        return getImageLoader();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Date getSystemTime() {
        return new Date();
    }
}
