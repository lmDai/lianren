package com.lianren.android.improve.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.lianren.android.R;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.util.TDevice;
import com.lianren.android.widget.OSCWebView;
import com.loopj.android.http.TextHttpResponseHandler;

import butterknife.Bind;

/**
 * @package: com.lianren.android.improve.main
 * @user:xhkj
 * @date:2019/12/24
 * @description:
 **/
public class WebActivity extends BackActivity implements OSCWebView.OnFinishListener {

    protected OSCWebView mWebView;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.ll_root)
    LinearLayout mLinearRoot;
    private String mTitle;
    private String mUrl;
    private boolean isWebViewFinish;

    public static void show(Context context, String url) {
        if (!TDevice.hasWebView(context))
            return;
        if (TextUtils.isEmpty(url))
            return;
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_web;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void initWidget() {
        super.initWidget();

        mWebView = new OSCWebView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        params.weight = 1;
        mWebView.setVisibility(View.INVISIBLE);
        mWebView.setLayoutParams(params);
        mLinearRoot.addView(mWebView);
        mWebView.setOnFinishFinish(this);

        mUrl = getIntent().getStringExtra("url");
        setStatusBarDarkMode();
        setSwipeBackEnable(true);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(false);
            }
            mToolBar.setTitleTextColor(Color.BLACK);
            DrawableCompat.setTint(mToolBar.getNavigationIcon(), Color.BLACK);
        }
        mWebView.loadUrl(mUrl);

    }


    public void onReceivedTitle(String title) {
        if (isDestroyed())
            return;
        mTitle = title;
        mToolBar.setTitle(title);
    }

    @Override
    public void onProgressChange(int progress) {
        if (isDestroyed())
            return;
        mProgressBar.setProgress(progress);
        if (progress >= 60 && !isWebViewFinish) {
            isWebViewFinish = true;
            mWebView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isDestroyed())
                        return;
                    mWebView.setVisibility(View.VISIBLE);
                }
            }, 800);
        }
    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {
        if (isDestroyed())
            return;
        mProgressBar.setVisibility(View.INVISIBLE);
    }


    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.onDestroy();
        }
    }
}

