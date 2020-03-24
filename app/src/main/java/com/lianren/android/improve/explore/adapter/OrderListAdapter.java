package com.lianren.android.improve.explore.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianren.android.R;
import com.lianren.android.improve.bean.TicketOrderDetailBean;
import com.lianren.android.util.ImageLoader;

/**
 * @package: com.lianren.android.improve.explore.adapter
 * @user:xhkj
 * @date:2019/12/30
 * @description:
 **/
public class OrderListAdapter extends BaseQuickAdapter<TicketOrderDetailBean, BaseViewHolder> {
    public OrderListAdapter() {
        super(R.layout.adapter_order_list);
    }

    @Override
    protected void convert(BaseViewHolder helper, TicketOrderDetailBean item) {
        //0待支付 1已支付 2取消 3申请退款 4已退款
        String status = "";
        switch (item.order.status) {
            case 0:
                status = "待支付";
                break;
            case 1:
                status = "电子票";
                break;
            case 2:
                status = "已取消";
                break;
            case 3:
                status = "申请退款中";
                break;
            case 4:
                status = "已退款";
                break;
        }
        helper.getView(R.id.tv_status).setEnabled(item.order.status == 0 || item.order.status == 1);
        ImageLoader.loadAutoImage(Glide.with(mContext), (ImageView) helper.getView(R.id.img_image), item.subject.image);
        helper.setText(R.id.tv_no, "订单号:" + item.order.no)
                .setText(R.id.tv_name, item.subject.name)
                .setText(R.id.tv_address, item.subject.address)
                .setText(R.id.tv_time, item.subject.time)
                .setText(R.id.tv_status, status)
                .setText(R.id.tv_create, item.order.created_at)
                .setText(R.id.tv_good_name, item.good.get(0).name + "x" + item.good.size())
                .addOnClickListener(R.id.tv_status);
    }
}
