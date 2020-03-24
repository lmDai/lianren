package com.lianren.android.improve.user.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianren.android.R;
import com.lianren.android.improve.bean.BasicBean;
import com.lianren.android.improve.bean.ProfessionBean;

import java.util.List;

/**
 * @package: com.lianren.android.improve.user.adapter
 * @user:xhkj
 * @date:2019/12/26
 * @description:
 **/
public class ProfessionAdapter extends BaseQuickAdapter<ProfessionBean, BaseViewHolder> {


    public ProfessionAdapter() {
        super(R.layout.item_section_content);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ProfessionBean item) {
        BasicBean.ItemBean.ChildrenBean video = item.t;
        helper.setText(R.id.tv_content, video.name);
    }
}

//public class ProfessionAdapter extends BaseSectionQuickAdapter<ProfessionBean, BaseViewHolder> {
//    /**
//     * Same as QuickAdapter#QuickAdapter(Context,int) but with
//     * some initialization data.
//     *
//     * @param sectionHeadResId The section head layout id for each item
//     * @param layoutResId      The layout resource id of each item.
//     * @param data             A new list is created out of this one to avoid mutable list
//     */
//    public ProfessionAdapter(int layoutResId, int sectionHeadResId, List data) {
//        super(layoutResId, sectionHeadResId, data);
//    }
//
//    @Override
//    protected void convertHead(BaseViewHolder helper, final ProfessionBean item) {
//        helper.setText(R.id.header, item.header);
//    }
//
//
//    @Override
//    protected void convert(@NonNull BaseViewHolder helper, ProfessionBean item) {
//        BasicBean.ItemBean.ChildrenBean video = item.t;
//        helper.setText(R.id.tv_content, video.name);
//    }
//}

