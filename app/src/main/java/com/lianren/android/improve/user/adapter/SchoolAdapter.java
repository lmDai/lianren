package com.lianren.android.improve.user.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianren.android.R;
import com.lianren.android.improve.bean.BasicBean;

/**
 * @package: com.lianren.android.improve.user.adapter
 * @user:xhkj
 * @date:2019/12/26
 * @description:
 **/
public class SchoolAdapter extends BaseQuickAdapter<BasicBean.ItemBean, BaseViewHolder> {
    public SchoolAdapter() {
        super(R.layout.adapter_school);
    }

    @Override
    protected void convert(BaseViewHolder helper, BasicBean.ItemBean item) {
        helper.setText(R.id.tv_content, item.name);
    }
}
