package com.lianren.android.improve.home.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianren.android.R;
import com.lianren.android.improve.bean.MessageBean;
import com.lianren.android.util.ImageLoader;
import com.lianren.android.widget.circle.TweetParser;

/**
 * @package: com.lianren.android.improve.home.adapter
 * @user:xhkj
 * @date:2019/12/31
 * @description:
 **/
public class MessageAdapter extends BaseQuickAdapter<MessageBean, BaseViewHolder> {
    public MessageAdapter() {
        super(R.layout.adapter_message);
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageBean item) {
        ImageLoader.loadImage(Glide.with(mContext), (ImageView) helper.getView(R.id.img_avater), item.user.avatar_url);
        helper.setText(R.id.tv_nick, item.user.nickname)
                .setText(R.id.tv_msg_time, item.msg_time)
                .setVisible(R.id.tv_new, item.is_new == 1)
                .setText(R.id.tv_msg, TweetParser.getInstance().parse(mContext, item.msg))
                .addOnClickListener(R.id.ll_content);
    }

    public void notifyItem(int position) {
        getData().get(position).is_new = 0;
        notifyItemChanged(position + getHeaderLayoutCount());
    }
}
