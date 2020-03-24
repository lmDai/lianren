package com.lianren.android.improve.explore.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianren.android.R;

/**
 * @package: com.lianren.android.improve.explore.adapter
 * @user:xhkj
 * @date:2019/12/26
 * @description:
 **/
public class TagsAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public TagsAdapter() {
        super(R.layout.adapter_tag);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_content, item);
    }
}
