package com.lianren.android.improve.explore.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianren.android.R;
import com.lianren.android.improve.account.AccountHelper;
import com.lianren.android.improve.bean.DatingListBean;
import com.lianren.android.util.ImageLoader;

/**
 * @package: com.lianren.android.improve.explore.adapter
 * @user:xhkj
 * @date:2019/12/30
 * @description:
 **/
public class DatingListAdapter extends BaseQuickAdapter<DatingListBean, BaseViewHolder> {
    public DatingListAdapter() {
        super(R.layout.adapter_dating_list);
    }

    @Override
    protected void convert(BaseViewHolder helper, DatingListBean item) {
        ImageLoader.loadAutoImage(Glide.with(mContext), (ImageView) helper.getView(R.id.img_image), item.subject.image);
        helper.setText(R.id.tv_time, item.created_at)
                .setText(R.id.tv_name, item.subject.name)
                .setText(R.id.tv_address, item.subject.address)
                .setText(R.id.tv_invite_time, item.subject.time);
        String status = "";
        if (item.present_user.id == AccountHelper.getUserId()) {//发起人
            ImageLoader.loadImage(Glide.with(mContext), (ImageView) helper.getView(R.id.img_avater), item.remote_user.avatar_url);
            helper.setText(R.id.tv_nickname, item.remote_user.nickname);
            switch (item.present_status) {
                case 0:
                    status = "已发出邀约，待对方接受";
                    break;
                case 100:
                    status = "对方已接受，请您购票";
                    break;
                case 101:
                    status = "已购票";
                    break;
                case 102:
                    status = "已取消";
                    break;
                case 103:
                    status = "购票超时取消";
                    break;
                case 200:
                    status = "双方已购票，请按时参加约会";
                    break;
                case 201:
                    status = "已约见";
                    break;
            }
        } else {//受邀人
            ImageLoader.loadImage(Glide.with(mContext), (ImageView) helper.getView(R.id.img_avater), item.present_user.avatar_url);
            helper.setText(R.id.tv_nickname, item.present_user.nickname);
            switch (item.remote_status) {
                case 0:
                    status = "待接受";
                    break;
                case 2:
                    status = "已拒绝";
                    break;
                case 100:
                    status = "待购票";
                    break;
                case 101:
                    status = "已购票";
                    break;
                case 102:
                    status = "已取消";
                    break;
                case 103:
                    status = "购票超时取消";
                    break;
                case 200:
                    status = "双方已购票，请按时参加约会";
                    break;
                case 201:
                    status = "已约见";
                    break;
            }
        }
        helper.setText(R.id.tv_status, status);

    }
}
