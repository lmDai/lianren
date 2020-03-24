package com.lianren.android.improve.feedback;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.reflect.TypeToken;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.EventApi;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.bean.FeedTypeBean;
import com.lianren.android.improve.bean.base.ResultBean;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

/**
 * @package: com.lianren.android.improve.feedback
 * @user:xhkj
 * @date:2019/12/30
 * @description:反馈类型
 **/
public class FeedTypeActivity extends FeedBaseActivity implements OnRefreshListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private String type;
    private FeedTypeAdapter mAdapter;
    private String fb_obj_id;
    private String contact;

    @Override
    protected int getContentView() {
        return R.layout.activity_feed_type;
    }

    public static void show(Context mContext, String type) {
        Intent intent = new Intent();
        intent.putExtra("type", type);
        intent.setClass(mContext, FeedTypeActivity.class);
        mContext.startActivity(intent);
    }

    public static void show(Context mContext, String type, String fb_obj_id) {
        Intent intent = new Intent();
        intent.putExtra("type", type);
        intent.putExtra("fb_obj_id", fb_obj_id);
        intent.setClass(mContext, FeedTypeActivity.class);
        mContext.startActivity(intent);
    }

    public static void show(Context mContext, String type, String fb_obj_id, String contact) {
        Intent intent = new Intent();
        intent.putExtra("type", type);
        intent.putExtra("fb_obj_id", fb_obj_id);
        intent.putExtra("contact", contact);
        intent.setClass(mContext, FeedTypeActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setDarkToolBar();
        setStatusBarDarkMode();
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new FeedTypeAdapter();
        mAdapter.bindToRecyclerView(recyclerview);
    }

    @Override
    protected void initData() {
        super.initData();
        type = getIntent().getStringExtra("type");
        fb_obj_id = getIntent().getStringExtra("fb_obj_id");
        contact = getIntent().getStringExtra("contact");
        getData();
        refreshLayout.setOnRefreshListener(this);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FeedTypeBean item = mAdapter.getItem(position);
                FeedCommitActivity.show(FeedTypeActivity.this, fb_obj_id, item.id, contact, item.name);
            }
        });
    }

    private void getData() {
        EventApi.fbFeedType(type, new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AppContext.showToast(R.string.tip_network_error);
                refreshLayout.finishRefresh();
            }

            @Override
            public void onStart() {
                super.onStart();
                showLoadingDialog();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissLoadingDialog();
                refreshLayout.finishRefresh();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<List<FeedTypeBean>>>() {
                    }.getType();
                    ResultBean<List<FeedTypeBean>> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    mAdapter.setNewData(resultBean.data);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getData();
    }
}
