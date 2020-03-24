package com.lianren.android.improve.home.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianren.android.R;
import com.lianren.android.improve.bean.MatchingBean;
import com.lianren.android.util.ImageLoader;

/**
 * @package: com.lianren.android.improve.home.adapter
 * @user:xhkj
 * @date:2019/12/19
 * @description:匹配列表
 **/
public class MatchingsAdapter extends BaseQuickAdapter<MatchingBean.UserBean, BaseViewHolder> {
    public MatchingsAdapter() {
        super(R.layout.adapter_matchings);
    }

    @Override
    protected void convert(BaseViewHolder helper, MatchingBean.UserBean item) {
        helper.setText(R.id.tv_about, item.remote.about);
        ImageLoader.loadImage(Glide.with(mContext), (ImageView) helper.getView(R.id.img_avater),
                item.remote.avatar_url, mContext);
        if (TextUtils.isEmpty(item.view_at)) {
            ImageLoader.loadImage(Glide.with(mContext), (ImageView) helper.getView(R.id.img_avater),
                    item.remote.avatar_url, mContext);
        } else {
            ImageLoader.loadImage(Glide.with(mContext), (ImageView) helper.getView(R.id.img_avater),
                    item.remote.avatar_url);
        }
    }

    public void itemView(int position) {
        getData().get(position).view_at = System.currentTimeMillis() + "";
        notifyItemChanged(position);
    }
}
