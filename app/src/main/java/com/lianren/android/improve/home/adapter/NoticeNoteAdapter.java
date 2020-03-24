package com.lianren.android.improve.home.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianren.android.R;
import com.lianren.android.improve.bean.NoticeNoteBean;
import com.lianren.android.util.ImageLoader;

/**
 * @package: com.lianren.android.improve.home.adapter
 * @user:xhkj
 * @date:2019/12/31
 * @description:
 **/
public class NoticeNoteAdapter extends BaseQuickAdapter<NoticeNoteBean, BaseViewHolder> {
    public NoticeNoteAdapter() {
        super(R.layout.adapter_notice_note);
    }

    @Override
    protected void convert(BaseViewHolder helper, NoticeNoteBean item) {
        ImageLoader.loadImage(Glide.with(mContext), (ImageView) helper.getView(R.id.img_avater), item.user.avatar_url);
        helper.setText(R.id.tv_name, item.user.nickname)
                .setText(R.id.tv_content, item.content)
                .setGone(R.id.img_praise,item.type.equals("1"))
                .setGone(R.id.tv_content,item.type.equals("2"))
                .setText(R.id.tv_print_content, "回复了你：印记|" + item.note.content.text)
                .setText(R.id.tv_date, item.created_at);
    }
}
