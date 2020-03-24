package com.lianren.android.widget.tag;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.lianren.android.improve.explore.activities.ImprintTopicActivity;
import com.lianren.android.util.pickimage.media.SpaceGridItemDecoration;
import com.lianren.android.widget.TweetPicturesPreviewerItemTouchCallback;

import java.util.List;


/**
 * @package: com.lianren.android.widget
 * @user:xhkj
 * @date:2019/12/20
 * @description:
 **/
public class TagPickPreviewer extends RecyclerView implements TagSelectAdapter.Callback {
    private TagSelectAdapter mImageAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private RequestManager mCurImageLoader;

    public TagPickPreviewer(Context context) {
        super(context);
        init();
    }

    public TagPickPreviewer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TagPickPreviewer(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mImageAdapter = new TagSelectAdapter(this);
        FlowLayoutManager layout = new FlowLayoutManager();
        //必须，防止recyclerview高度为wrap时测量item高度0
        layout.setAutoMeasureEnabled(true);
        this.setLayoutManager(layout);
        this.addItemDecoration(new SpaceGridItemDecoration(10));
        this.setAdapter(mImageAdapter);
        this.setOverScrollMode(View.OVER_SCROLL_NEVER);
        ItemTouchHelper.Callback callback = new TweetPicturesPreviewerItemTouchCallback(mImageAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(this);
    }

    public void set(List<String> paths) {
        mImageAdapter.clear();
        if (paths != null && paths.size() > 0)
            for (String path : paths) {
                mImageAdapter.add(path);
            }
        mImageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadMoreClick() {
        ImprintTopicActivity.show(getContext(), new SelectOptions.Builder()
                .setHasCam(true)
                .setSelectCount(1)
                .setSelectedImages(mImageAdapter.getPaths())
                .setCallback(new SelectOptions.Callback() {
                    @Override
                    public void doSelected(List<String> images) {
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

    public List<TagSelectAdapter.Model> getModels() {
        return mImageAdapter.getmModels();
    }

    public List<String> getPaths() {
        return mImageAdapter.getPaths();
    }

}
