package com.lianren.android.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.gson.reflect.TypeToken;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.bean.QiNiuBean;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.util.pickimage.TweetSelectImageAdapter;
import com.lianren.android.util.pickimage.media.SelectImageActivity;
import com.lianren.android.util.pickimage.media.config.SelectOptions;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;


/**
 * @package: com.lianren.android.widget
 * @user:xhkj
 * @date:2019/12/20
 * @description:
 **/
public class TweetPicturesPreviewer extends RecyclerView implements TweetSelectImageAdapter.Callback {
    private TweetSelectImageAdapter mImageAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private RequestManager mCurImageLoader;
    public int type;//文件类型 1用户个人图片(相册、头像) 2商家图片 3印记图片 4其他图片(举报等非1，2，3类的图片)

    public TweetPicturesPreviewer(Context context) {
        super(context);
        init();
    }

    public TweetPicturesPreviewer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TweetPicturesPreviewer(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setType(int type) {
        this.type = type;
    }

    private void init() {
        mImageAdapter = new TweetSelectImageAdapter(this);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 5);
        this.setLayoutManager(layoutManager);
        this.setAdapter(mImageAdapter);
        this.setOverScrollMode(View.OVER_SCROLL_NEVER);
        ItemTouchHelper.Callback callback = new TweetPicturesPreviewerItemTouchCallback(mImageAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(this);
    }

    public void set(String[] paths) {
        for (String path : paths) {
            qiniuParams(path);
        }
        mImageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadMoreClick() {
        SelectImageActivity.show(getContext(), new SelectOptions.Builder()
                .setHasCam(true)
                .setSelectCount(6 - mImageAdapter.getItemCount())
                .setCallback(new SelectOptions.Callback() {
                    @Override
                    public void doSelected(String[] images) {
                        set(images);
                    }
                }).build());
    }

    @Override
    public RequestManager getImgLoader() {
        if (mCurImageLoader == null) {
            mCurImageLoader = Glide.with(getContext());
        }
        return mCurImageLoader;
    }

    @Override
    public void onStartDrag(ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public List<TweetSelectImageAdapter.Model> getModels() {
        return mImageAdapter.getmModels();
    }

    public String[] getPaths() {
        return mImageAdapter.getPaths();
    }

    public void qiniuParams(final String file_name) {
        LRApi.qiniuUpload(file_name, type, new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<QiNiuBean>>() {
                    }.getType();

                    ResultBean<QiNiuBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        mImageAdapter.add(file_name, resultBean.data);
                        mImageAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });

    }
}
