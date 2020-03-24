package com.lianren.android.improve.user.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.lianren.android.R;
import com.lianren.android.improve.base.BaseRecycleAdapter;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.widget.viewhelper.SelectImgHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @package: com.lianren.android.improve.user.adapter
 * @user:xhkj
 * @date:2020/1/6
 * @description:
 **/
public class SelectImgsAdapter extends BaseRecycleAdapter<SelectImgHolder> implements SelectImgHolder.LongPressListener{

    private Callback callback;
    private List<UsersInfoBean.PhotoBean> photos = new ArrayList<>();
    private Context mContext;

    /**
     * 默认最多选择9张图片
     */
    public static final int maxImg = 9;

    public SelectImgsAdapter(List<UsersInfoBean.PhotoBean> photos, Context context) {
        this.photos = photos;
        this.mContext = context;
    }

    @Override
    public SelectImgHolder onCreateViewHolder(ViewGroup parent, int viewType, int notuse) {
        return new SelectImgHolder(View.inflate(mContext, R.layout.view_review_add_img,null),mContext).setLister(this);
    }

    @Override
    public void onBindViewHolder(SelectImgHolder holder, int position, int notuse) {
        holder.bind(photos.get(position),position);
    }

    @Override
    public int getItemCount() {
        int count = 1;
        if(photos.size()-1 >= maxImg){
            count = maxImg;
        }else {
            count = photos.size();
        }
        return count;
    }

    @Override
    public void longPress(SelectImgHolder holder) {
        if(this.callback != null)
            this.callback.startDrag(holder);
    }

    @Override
    public void delPicture(UsersInfoBean.PhotoBean photo, int position) {
        if(this.callback != null)
            this.callback.delPicture(photo, position);
    }

    @Override
    public void addPicture() {
        if(this.callback != null)
            this.callback.addPicture();
    }

    @Override
    public void itemOnClick(int position) {
        if(this.callback != null)
            this.callback.itemClick(position);
    }

    public void updateData(List<UsersInfoBean.PhotoBean> images){
        this.photos = images;
        notifyDataSetChanged();
    }

    public interface Callback {
        void startDrag(SelectImgHolder holder);
        void delPicture(UsersInfoBean.PhotoBean photo, int position);
        void addPicture();
        void itemClick(int position);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }
}

