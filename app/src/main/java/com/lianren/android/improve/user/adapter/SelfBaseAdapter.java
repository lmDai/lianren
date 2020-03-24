package com.lianren.android.improve.user.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianren.android.R;
import com.lianren.android.improve.bean.ItemBaseBean;
import com.lianren.android.util.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @package: com.lianren.android.improve.user.adapter
 * @user:xhkj
 * @date:2019/12/20
 * @description:
 **/
public class SelfBaseAdapter extends BaseQuickAdapter<ItemBaseBean, BaseViewHolder> {
    public SelfBaseAdapter() {
        super(R.layout.adapter_user_self_base);
    }

    @Override
    protected void convert(BaseViewHolder helper, ItemBaseBean item) {
        ImageView img = helper.getView(R.id.img_avater);
        if (item.isHead) {
            img.setVisibility(View.VISIBLE);
            helper.setText(R.id.tv_title, item.title)
                    .setText(R.id.tv_content, TextUtils.isEmpty(item.content) ? "请上传真实头像" : "");
            if (!TextUtils.isEmpty(item.content))
                ImageLoader.loadImage(Glide.with(mContext), img, item.content);
        } else if (item.isLocked) {
            helper.setText(R.id.tv_title, item.title)
                    .setText(R.id.tv_content, item.content);
            img.setImageResource(R.mipmap.lock_small);
            img.setVisibility(View.VISIBLE);
        } else {
            helper.setText(R.id.tv_title, item.title)
                    .setText(R.id.tv_content, item.content);
            img.setVisibility(View.GONE);
        }
    }

    public void updataPostition(String content, int position) {
        getData().get(position).content = content;
        notifyItemChanged(position);
    }
}
