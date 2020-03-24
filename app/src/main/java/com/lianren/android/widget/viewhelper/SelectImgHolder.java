package com.lianren.android.widget.viewhelper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lianren.android.R;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.util.ImageLoader;

/**
 * Created by Elemt on 2017/2/22.
 */

public class SelectImgHolder extends RecyclerView.ViewHolder {

    private LongPressListener listener;
    ImageView ivAdd;
    ImageView ivImg, ivDel;
    FrameLayout frameLayoutImgs;
    Context mContext;

    public SelectImgHolder(View itemView, Context context) {
        super(itemView);
        this.mContext = context;
        ivAdd = itemView.findViewById(R.id.ivAdd);
        ivDel = itemView.findViewById(R.id.ivDel);
        ivImg = itemView.findViewById(R.id.ivImg);
        frameLayoutImgs = itemView.findViewById(R.id.frameLayoutImgs);
    }

    public void bind(final UsersInfoBean.PhotoBean photo, final int position) {
        String url = photo.img_uri;
        isShowAdd(url);
        ivDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.delPicture(photo, position);
            }
        });
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.addPicture();
            }
        });
        ivImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.itemOnClick(position);
            }
        });
        frameLayoutImgs.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (listener != null)
                    listener.longPress(SelectImgHolder.this);
                return false;
            }
        });
        refreTxt(url);
    }

    void refreTxt(String url) {
        ImageLoader.loadImage(Glide.with(ivImg.getContext()), ivImg, url);
    }

    void isShowAdd(String txt) {
        if ("添加".equals(txt)) {
            ivImg.setVisibility(View.GONE);
            ivAdd.setVisibility(View.VISIBLE);
            frameLayoutImgs.setVisibility(View.GONE);
        } else {
            ivImg.setVisibility(View.VISIBLE);
            ivAdd.setVisibility(View.GONE);
            frameLayoutImgs.setVisibility(View.VISIBLE);
        }
    }

    public interface LongPressListener {
        void longPress(SelectImgHolder holder);

        void delPicture(UsersInfoBean.PhotoBean photo, int mPosition);

        void addPicture();

        void itemOnClick(int position);
    }

    public SelectImgHolder setLister(LongPressListener listener) {
        this.listener = listener;
        return this;
    }
}
