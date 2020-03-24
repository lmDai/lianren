package com.lianren.android.improve.home.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianren.android.R;
import com.lianren.android.improve.bean.EventBean;
import com.lianren.android.util.ImageLoader;

/**
 * @package: com.lianren.android.improve.user.adapter
 * @user:xhkj
 * @date:2019/12/20
 * @description:空间列表
 **/
public class EventListAdapter extends BaseQuickAdapter<EventBean, BaseViewHolder> {
    public EventListAdapter() {
        super(R.layout.adapter_event_list);
    }

    @Override
    protected void convert(BaseViewHolder helper, final EventBean item) {
        ImageLoader.loadImage(Glide.with(mContext), (ImageView) helper.getView(R.id.image), item.image);
        helper.setText(R.id.tv_name, item.name)
                .setText(R.id.tv_title, item.type + "、" + item.total_cost)
                .setText(R.id.tv_address, item.address)
                .setText(R.id.tv_time, item.time)
                .addOnClickListener(R.id.tv_status);
    }
}
