package com.lianren.android.improve.user.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lianren.android.R;
import com.lianren.android.base.BaseRecyclerAdapter;
import com.lianren.android.improve.bean.ContactUserBean;
import com.lianren.android.util.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @package: com.lianren.android.improve.home.adapter
 * @user:xhkj
 * @date:2019/12/31
 * @description:
 **/

public class ShiledUserAdapter extends BaseRecyclerAdapter<ContactUserBean> {

    private OnViewClickListener mDeleteListener;
    private OnViewClickListener itemListener;

    public ShiledUserAdapter(Context context) {
        super(context, NEITHER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new TagHolder(mInflater.inflate(R.layout.adapter_shiled_user, parent, false));
    }

    @Override
    protected void onBindClickListener(RecyclerView.ViewHolder holder) {
        TagHolder h = (TagHolder) holder;
        h.mTextDelete.setTag(holder);
        h.mTextDelete.setOnClickListener(mDeleteListener);
        h.llItem.setTag(holder);
        h.llItem.setOnClickListener(itemListener);
    }

    public void setItemListener(OnViewClickListener itemListener) {
        this.itemListener = itemListener;
    }

    public void setDeleteListener(OnViewClickListener mDeleteListener) {
        this.mDeleteListener = mDeleteListener;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        super.setOnItemClickListener(onItemClickListener);
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, ContactUserBean item, int position) {
        TagHolder h = (TagHolder) holder;
        ImageLoader.loadImage(Glide.with(mContext), h.imgAvatar, item.avatar_url);
        h.tvNickName.setText(item.nickname);
    }

    private static final class TagHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgAvatar;
        private TextView tvNickName, mTextDelete;
        private LinearLayout llItem;

        private TagHolder(View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.img_avater);
            llItem = itemView.findViewById(R.id.ll_item);
            tvNickName = itemView.findViewById(R.id.tv_nickname);
            mTextDelete = itemView.findViewById(R.id.tv_delete);
        }
    }
}

