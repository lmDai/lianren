package com.lianren.android.improve.user.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianren.android.R;
import com.lianren.android.improve.bean.EventBean;
import com.lianren.android.util.ImageLoader;

/**
 * @package: com.lianren.android.improve.explore.adapter
 * @user:xhkj
 * @date:2019/12/23
 * @description:活动列表
 **/
public class CollectEventListAdapter extends BaseQuickAdapter<EventBean, BaseViewHolder> {
    public CollectEventListAdapter() {
        super(R.layout.adapter_collect_event);
    }

    @Override
    protected void convert(BaseViewHolder helper, EventBean item) {
        ImageLoader.loadAutoImage(Glide.with(mContext), (ImageView) helper.getView(R.id.image),
                item.image);

        helper.setText(R.id.tv_name, item.name)
                .setText(R.id.tv_time, item.time)
                .setText(R.id.tv_address, item.address)
                .setText(R.id.tv_coast, item.total_cost);

    }
}
