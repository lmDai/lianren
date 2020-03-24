package com.lianren.android.improve.feedback;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianren.android.R;
import com.lianren.android.improve.bean.FeedTypeBean;

import java.util.List;

/**
 * 反馈分类列表
 */
public class FeedTypeAdapter extends BaseQuickAdapter<FeedTypeBean, BaseViewHolder> {

    public FeedTypeAdapter() {
        super(R.layout.adapter_feed_type);
    }

    @Override
    protected void convert(BaseViewHolder helper, FeedTypeBean item) {
        helper.setText(R.id.tv_content, item.name);
    }
}
