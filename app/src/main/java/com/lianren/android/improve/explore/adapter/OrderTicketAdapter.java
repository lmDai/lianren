package com.lianren.android.improve.explore.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianren.android.R;
import com.lianren.android.improve.bean.OrderTicketBean;
import com.lianren.android.util.ImageLoader;

/**
 * @package: com.lianren.android.improve.explore.adapter
 * @user:xhkj
 * @date:2019/12/30
 * @description:订单票券
 **/
public class OrderTicketAdapter extends BaseQuickAdapter<OrderTicketBean.Ticket, BaseViewHolder> {
    public OrderTicketAdapter() {
        super(R.layout.adapter_order_ticket);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderTicketBean.Ticket item) {
        String status;
        if (item.status == 0) {
            status = "未使用";
        } else if (item.status == 1) {
            status = "已使用";
        } else {
            status = "已过期";
        }
        helper.setText(R.id.tv_name, item.name)
                .setText(R.id.tv_no, item.no)
                .setText(R.id.tv_price, item.price + "")
                .setText(R.id.time, item.time)
                .setText(R.id.tv_status, status)
                .addOnClickListener(R.id.img_copy);
        ImageLoader.loadImage(Glide.with(mContext), (ImageView) helper.getView(R.id.img_qr_image), item.qr_image);
    }
}
