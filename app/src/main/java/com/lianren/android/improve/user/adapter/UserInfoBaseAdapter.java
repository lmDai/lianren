package com.lianren.android.improve.user.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianren.android.R;
import com.lianren.android.improve.bean.ItemBaseBean;

/**
 * @package: com.lianren.android.improve.user.adapter
 * @user:xhkj
 * @date:2019/12/20
 * @description:
 **/
public class UserInfoBaseAdapter extends BaseQuickAdapter<ItemBaseBean, BaseViewHolder> {
    public UserInfoBaseAdapter() {
        super(R.layout.adapter_user_info_base);
    }

    @Override
    protected void convert(BaseViewHolder helper, ItemBaseBean item) {
        helper.setText(R.id.tv_title, item.title)
                .setText(R.id.tv_content, TextUtils.isEmpty(item.content) ? "" : item.content);
    }

    public void updataPostition(String content, int position) {
        getData().get(position).content = content;
        notifyItemChanged(position);
    }
}
