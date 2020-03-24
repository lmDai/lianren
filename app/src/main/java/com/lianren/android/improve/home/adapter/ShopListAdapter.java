package com.lianren.android.improve.home.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianren.android.R;
import com.lianren.android.improve.bean.ShopBean;
import com.lianren.android.util.ImageLoader;

/**
 * @package: com.lianren.android.improve.user.adapter
 * @user:xhkj
 * @date:2019/12/20
 * @description:空间列表
 **/
public class ShopListAdapter extends BaseQuickAdapter<ShopBean, BaseViewHolder> {
    public ShopListAdapter() {
        super(R.layout.adapter_space_list);
    }

    @Override
    protected void convert(BaseViewHolder helper, final ShopBean item) {
        ImageLoader.loadAutoImage(Glide.with(mContext), (ImageView) helper.getView(R.id.image), item.image);
        helper.setText(R.id.tv_name, item.name)
                .setText(R.id.tv_address, item.address)
                .setText(R.id.tv_total_cost, item.total_cost)
                .addOnClickListener(R.id.tv_status);
    }
}
