package com.lianren.android.improve.explore.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianren.android.R;
import com.lianren.android.improve.bean.EventTicketBean;

/**
 * @package: com.lianren.android.improve.explore.adapter
 * @user:xhkj
 * @date:2019/12/24
 * @description:
 **/
public class EventTicketAdapter extends BaseQuickAdapter<EventTicketBean.GoodsBean, BaseViewHolder> {
    public EventTicketAdapter() {
        super(R.layout.adapter_event_ticket);
    }

    @Override
    protected void convert(BaseViewHolder helper, EventTicketBean.GoodsBean item) {
        helper.setGone(R.id.ll_content, !item.checked);
        helper.setGone(R.id.ll_content1, item.checked);
        //未选中
        helper.setText(R.id.tv_ticket_name, item.name).setText(R.id.txt_price, "¥" + item.price);
        //选中
        helper.setText(R.id.txt_goods_name1, item.name).setText(R.id.txt_goods_time, "有效时间：" + item.time)
                .setText(R.id.txt_price1, "¥" + item.price)
                .setText(R.id.txt_num, item.purchaseNum + "")
                .addOnClickListener(R.id.img_delete)
                .addOnClickListener(R.id.img_add);
    }

    public void setChecked(int position) {
        for (EventTicketBean.GoodsBean item : getData()) {
            item.checked = false;
        }
        getData().get(position).checked = true;
        notifyDataSetChanged();
    }

    public void notifyItem(int position, int purchaseNum) {
        getData().get(position).purchaseNum = purchaseNum;
        notifyItemChanged(position);
    }

    public EventTicketBean.GoodsBean getChecked() {
        for (EventTicketBean.GoodsBean item : getData()) {
            if (item.checked) return item;
        }
        return null;
    }
}
