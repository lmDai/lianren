package com.lianren.android.improve.user;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianren.android.R;
import com.lianren.android.improve.bean.ContactUserBean;
import com.lianren.android.util.ImageLoader;

/**
 * @package: com.lianren.android.improve.home.adapter
 * @user:xhkj
 * @date:2019/12/31
 * @description:
 **/

public class ContactLikeAdapter extends BaseQuickAdapter<ContactUserBean, BaseViewHolder> {

    public ContactLikeAdapter() {
        super(R.layout.adapter_contact_like);
    }

    @Override
    protected void convert(BaseViewHolder helper, ContactUserBean item) {
        ImageLoader.loadImage(Glide.with(mContext), (ImageView) helper.getView(R.id.img_avater), item.avatar_url);
        helper.setText(R.id.tv_nickname, item.nickname)
                .setText(R.id.tv_create, item.created_at);
    }
}

