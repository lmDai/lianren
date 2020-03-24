package com.lianren.android.improve.home.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lianren.android.R;
import com.lianren.android.base.BaseRecyclerAdapter;
import com.lianren.android.improve.bean.PairListBean;
import com.lianren.android.util.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @package: com.lianren.android.improve.home.adapter
 * @user:xhkj
 * @date:2019/12/31
 * @description:
 **/

public class PairListAdapter extends BaseRecyclerAdapter<PairListBean> {

    private OnViewClickListener mDeleteListener;
    private OnViewClickListener mAccepetListener;
    private OnViewClickListener itemListener;

    public PairListAdapter(Context context) {
        super(context, NEITHER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new TagHolder(mInflater.inflate(R.layout.adapter_pair, parent, false));
    }

    @Override
    protected void onBindClickListener(RecyclerView.ViewHolder holder) {
        TagHolder h = (TagHolder) holder;
        h.mTextDelete.setTag(holder);
        h.mTextDelete.setOnClickListener(mDeleteListener);
        h.tvAccept.setTag(holder);
        h.tvAccept.setOnClickListener(mAccepetListener);
        h.llItem.setTag(holder);
        h.llItem.setOnClickListener(itemListener);
    }

    public void setAcceptListener(OnViewClickListener mAccepetListener) {
        this.mAccepetListener = mAccepetListener;
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
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, PairListBean item, int position) {
        TagHolder h = (TagHolder) holder;
        ImageLoader.loadImage(Glide.with(mContext), h.imgAvatar, item.user.avatar_url);
        h.tvAbout.setText(item.user.about);
        h.tvNickName.setText(item.user.nickname);
        h.tvTime.setText(item.apply_time);
        if (item.status == 0) {
            h.tvAccept.setText("接受");
            h.tvAccept.setTextColor(ContextCompat.getColor(mContext, R.color.day_colorPrimary));
            h.tvAccept.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_pair_accept));
        } else if (item.status == 1) {
            h.tvAccept.setText("已接受");
            h.tvAccept.setTextColor(Color.parseColor("#FFE6E6E6"));
            h.tvAccept.setBackground(null);
        }
        h.tvAccept.setVisibility(item.status == 2 ? View.GONE : View.VISIBLE);
    }

    private static final class TagHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgAvatar;
        private TextView tvNickName, tvTime, tvAbout, tvAccept, mTextDelete;
        private LinearLayout llItem;

        private TagHolder(View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.img_avater);
            llItem = itemView.findViewById(R.id.ll_item);
            tvNickName = itemView.findViewById(R.id.tv_nickname);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvAbout = itemView.findViewById(R.id.tv_about);
            tvAccept = itemView.findViewById(R.id.tv_accept);
            mTextDelete = itemView.findViewById(R.id.tv_delete);
        }
    }
}

