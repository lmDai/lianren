package com.lianren.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.lianren.android.R;
import com.lianren.android.improve.bean.EventDetailBean;
import com.lianren.android.util.ImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @package: com.lianren.android.widget
 * @user:xhkj
 * @date:2019/12/24
 * @description:
 **/
public class PileAvertView extends LinearLayout {

    @Bind(R.id.pile_view)
    PileView pileView;

    private Context context = null;
    public static final int VISIBLE_COUNT = 10;//默认显示个数

    public PileAvertView(Context context) {
        this(context, null);
        this.context = context;
    }

    public PileAvertView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_group_pile_avert, this);
        ButterKnife.bind(view);
    }

    public void setAvertImages(List<EventDetailBean.UsersBean> imageList) {
        setAvertImages(imageList, VISIBLE_COUNT);
    }

    //如果imageList>visiableCount,显示List最上面的几个
    public void setAvertImages(List<EventDetailBean.UsersBean> imageList, int visibleCount) {
        List<EventDetailBean.UsersBean> visibleList = null;
        if (imageList.size() > visibleCount) {
            visibleList = imageList.subList(imageList.size() - 1 - visibleCount, imageList.size() - 1);
        } else {
            visibleList = imageList;
        }
        pileView.removeAllViews();
        for (int i = 0; i < visibleList.size(); i++) {
            CircleImageView image = (CircleImageView) LayoutInflater.from(context).inflate(R.layout.item_group_round_avert, pileView, false);
            ImageLoader.loadImage(Glide.with(context), image, imageList.get(i).avatar_url);
            pileView.addView(image);
        }
    }
}
