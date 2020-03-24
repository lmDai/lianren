package com.lianren.android.improve.explore.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianren.android.R;
import com.lianren.android.improve.bean.EventBean;
import com.lianren.android.util.DateUtil;
import com.lianren.android.util.ImageLoader;

import java.text.ParseException;

/**
 * @package: com.lianren.android.improve.explore.adapter
 * @user:xhkj
 * @date:2019/12/23
 * @description:
 **/
public class HomeEventAdapter extends BaseQuickAdapter<EventBean, BaseViewHolder> {
    public HomeEventAdapter() {
        super(R.layout.adapter_home_event);
    }

    @Override
    protected void convert(BaseViewHolder helper, EventBean item) {
        String time = "";
        try {
            if (!TextUtils.isEmpty(item.s_time))
                time = DateUtil.dateToString(DateUtil.stringToDate(item.s_time, "yyyy-MM-dd hh:mm:ss"), "MM月dd");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        helper.setText(R.id.tv_about, item.name)
                .setText(R.id.tv_detail, item.type + "," + time + "," + item.address);
        ImageLoader.loadImage(Glide.with(mContext), (ImageView) helper.getView(R.id.img_avater),
                item.image);
    }
}
