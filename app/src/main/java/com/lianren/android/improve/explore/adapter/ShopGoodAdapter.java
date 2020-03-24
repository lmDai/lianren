package com.lianren.android.improve.explore.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianren.android.R;
import com.lianren.android.improve.bean.ShopGoodsBean;

/**
 * @package: com.lianren.android.improve.explore.adapter
 * @user:xhkj
 * @date:2019/12/24
 * @description:
 **/
public class ShopGoodAdapter extends BaseQuickAdapter<ShopGoodsBean.GoodsBean, BaseViewHolder> {
    public ShopGoodAdapter() {
        super(R.layout.adapter_shop_goods);
    }

    @Override
    protected void convert(BaseViewHolder helper, ShopGoodsBean.GoodsBean item) {
        helper.setGone(R.id.ll_content, !item.checked);
        helper.setGone(R.id.ll_content1, item.checked);
        //未选中
        helper.setText(R.id.tv_ticket_name, item.name).setText(R.id.txt_price, "¥" + item.price)
                .setGone(R.id.tv_discount1, !TextUtils.isEmpty(item.discount))
                .setText(R.id.tv_discount1, item.discount);
        //选中
        helper.setText(R.id.txt_goods_name1, item.name)
                .setText(R.id.txt_goods_time, "有效时间：" + (!TextUtils.isEmpty(item.time) ? item.time : ""))
                .setText(R.id.txt_price1, "¥" + item.price)
                .setGone(R.id.tv_discount, !TextUtils.isEmpty(item.discount))
                .setText(R.id.tv_discount, item.discount);
    }

    public void setChecked(int position) {
        for (ShopGoodsBean.GoodsBean item : getData()) {
            item.checked = false;
        }
        getData().get(position).checked = true;
        notifyDataSetChanged();
    }

    public void notifyItem(int position, int purchaseNum) {
        getData().get(position).purchaseNum = purchaseNum;
        notifyItemChanged(position);
    }

    public ShopGoodsBean.GoodsBean getChecked() {
        for (ShopGoodsBean.GoodsBean item : getData()) {
            if (item.checked) return item;
        }
        return null;
    }
}
