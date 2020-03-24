package com.lianren.android.improve.user.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianren.android.R;
import com.lianren.android.improve.account.AccountHelper;
import com.lianren.android.improve.bean.ImprintsBean;
import com.lianren.android.improve.bean.PickStatusBean;
import com.lianren.android.improve.explore.activities.SearchImprintActivity;
import com.lianren.android.improve.user.activities.ImprintsDetailActivity;
import com.lianren.android.util.ImageLoader;
import com.lianren.android.widget.PageNumberListPoint;
import com.lianren.android.widget.circle.ExpandView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @package: com.lianren.android.improve.user.adapter
 * @user:xhkj
 * @date:2019/12/20
 * @description:印记列表
 **/
public class ImprintsListAdapter extends BaseQuickAdapter<ImprintsBean, BaseViewHolder> {
    public ImprintsListAdapter() {
        super(R.layout.adapter_imprints_list);
    }

    @Override
    protected void convert(BaseViewHolder helper, final ImprintsBean item) {
        ImageLoader.loadImage(Glide.with(mContext), (ImageView) helper.getView(R.id.img_avater), item.user.avatar_url);
        TextView tvPicks = helper.getView(R.id.tv_picks);

        tvPicks.setText(item.picks + "");


        helper.setText(R.id.tv_nickname, item.user.nickname)
                .setText(R.id.tv_hide, item.status == 1 ? "公开" : "私密")
                .setVisible(R.id.tv_hide, item.user.id == AccountHelper.getUserId() && item.status == 0)
                .setVisible(R.id.tv_picks, item.picks != 0)
                .setText(R.id.tv_replys, item.replys + "")
                .addOnClickListener(R.id.page_number_point)
                .addOnClickListener(R.id.ll_comment)
                .addOnClickListener(R.id.ll_user)
                .addOnClickListener(R.id.ll_pick);
        final ExpandView expand = helper.getView(R.id.tv_content);
        //设置最大显示行数
        expand.setExpand(item.expand);
        expand.setExpandStatusListener(new ExpandView.ExpandStatusListener() {
            @Override
            public void statusChange(boolean isExpand) {
                item.expand = isExpand;
                notifyDataSetChanged();
            }
        });
        expand.setOnItemClickListener(new ExpandView.OnItemClickListener() {
            @Override
            public void onClick() {
                ImprintsDetailActivity.show(mContext, item.user.id, item.id, "");
            }
        });
        expand.setText(item.content.text, R.color.imprint_content_color);
        PageNumberListPoint pageNumberPoint = helper.getView(R.id.page_number_point);
        pageNumberPoint.addDot(item.content.image_url);
        final TagFlowLayout tagFlowLayout = helper.getView(R.id.tag_flow);
        tagFlowLayout.setAdapter(new TagAdapter<String>(item.tag) {
            @Override
            public View getView(FlowLayout parent, int position, final String tag) {
                TextView tvTag = (TextView) LayoutInflater.from(mContext).inflate(R.layout.tag_item_imprints, tagFlowLayout, false);
                tvTag.setText(String.format("#%s", tag));
                return tvTag;
            }
        });
        tagFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                List<String> tag = new ArrayList<>();
                tag.add(tagFlowLayout.getAdapter().getItem(position).toString());
                SearchImprintActivity.show(mContext, tag);
                return false;
            }
        });
    }

    public void notifyItem(PickStatusBean bean, int position) {
        getData().get(position).picks = bean.pick_num;
        notifyItemChanged(position + getHeaderLayoutCount());
    }
}
