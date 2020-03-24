package com.lianren.android.improve.home.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianren.android.R;
import com.lianren.android.improve.bean.SystemMessageBean;

/**
 * @package: com.lianren.android.improve.home.adapter
 * @user:xhkj
 * @date:2019/12/31
 * @description:
 **/
public class SystemMessageAdapter extends BaseQuickAdapter<SystemMessageBean, BaseViewHolder> {
    public SystemMessageAdapter() {
        super(R.layout.adapter_system_message);
    }

    @Override
    protected void convert(BaseViewHolder helper, SystemMessageBean item) {
        helper.setText(R.id.tv_time, item.created_at)
                .setText(R.id.tv_title, item.title)
                .setText(R.id.tv_content, item.content);
    }
}
