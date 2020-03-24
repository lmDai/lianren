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
public class SearchTagsAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public SearchTagsAdapter() {
        super(R.layout.adapter_search_tag);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_content, item)
                .addOnClickListener(R.id.tag_add);
    }
}
