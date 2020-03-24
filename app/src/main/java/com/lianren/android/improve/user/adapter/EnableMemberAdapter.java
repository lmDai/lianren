package com.lianren.android.improve.user.adapter;

import android.support.v4.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianren.android.R;
import com.lianren.android.improve.bean.UserVipBean;

/**
 * @package: com.lianren.android.view.adapter
 * @user:xhkj
 * @date:2019/8/15
 * @description:
 **/
public class EnableMemberAdapter extends BaseQuickAdapter<UserVipBean, BaseViewHolder> {

    public EnableMemberAdapter() {
        super(R.layout.item_enable_member);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserVipBean item) {
        helper.setText(R.id.txt_content, item.name)
                .setText(R.id.tv_actual_money, "¥ " + item.price)
                .setTextColor(R.id.txt_content, item.chcked ? ContextCompat.getColor(mContext, R.color.day_colorPrimary) :
                        ContextCompat.getColor(mContext, R.color.imprint_content_color))
                .setTextColor(R.id.tv_actual_money, item.chcked ? ContextCompat.getColor(mContext, R.color.day_colorPrimary) :
                        ContextCompat.getColor(mContext, R.color.imprint_content_color));
        helper.setBackgroundRes(R.id.ll_content, item.chcked ? R.drawable.bg_vip_item_checked : R.drawable.bg_vip_item_normal);
    }

    public UserVipBean getSelected() {
        for (UserVipBean userVipBean : getData()) {
            if (userVipBean.chcked) {
                return userVipBean;
            }
        }
        return null;
    }

    public void chcked(int position) {
        for (UserVipBean userVipBean : getData()) {
            userVipBean.chcked = false;
        }
        getData().get(position).chcked = true;
        notifyDataSetChanged();
    }
}
