package com.lianren.android.ui.dialog;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lianren.android.R;
import com.lianren.android.base.BaseDialog;
import com.lianren.android.base.BaseDialogFragment;
import com.lianren.android.improve.bean.UserVipBean;
import com.lianren.android.improve.user.adapter.EnableMemberAdapter;
import com.lianren.android.util.pickimage.media.SpaceGridItemDecoration;

import java.util.List;

/**
 * @package: com.lianren.android.ui.dialog
 * @user:xhkj
 * @date:2020/1/15
 * @description:
 **/
public class VipInfoDialog {
    public static final class Builder
            extends BaseDialogFragment.Builder<Builder> implements View.OnClickListener {
        private RecyclerView vipRecyclerview;
        private LinearLayout llClose;
        private Button btnVip;
        private OnListener mListener;
        private boolean mAutoDismiss = true;
        private EnableMemberAdapter enableMemberAdapter;

        public Builder(FragmentActivity activity) {
            super(activity);
            setContentView(R.layout.dialog_vip_info);
            setAnimStyle(BaseDialog.AnimStyle.BOTTOM);
            setGravity(Gravity.CENTER);
            llClose = findViewById(R.id.ll_close);
            btnVip = findViewById(R.id.btn_vip);
            llClose.setOnClickListener(this);
            btnVip.setOnClickListener(this);
            //专区
            vipRecyclerview = findViewById(R.id.vip_recyclerview);
            vipRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            vipRecyclerview.addItemDecoration(new SpaceGridItemDecoration(10));
            enableMemberAdapter = new EnableMemberAdapter();
            enableMemberAdapter.bindToRecyclerView(vipRecyclerview);
            enableMemberAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    enableMemberAdapter.chcked(position);
                }
            });
        }

        public Builder setListener(OnListener mListener) {
            this.mListener = mListener;
            return this;
        }

        public Builder setList(List<UserVipBean> filterList) {
            enableMemberAdapter.setNewData(filterList);
            return this;
        }

        @Override
        public void onClick(View v) {
            if (mAutoDismiss) {
                dismiss();
            }
            if (v == llClose) {
                if (mListener != null) {
                    mListener.onCancel(getDialog());
                }
            } else if (v == btnVip) {
                if (mListener != null) {
                    mListener.onSelected(getDialog(), enableMemberAdapter.getSelected());
                }
            }
        }

        @Override
        public BaseDialog show() {
            return super.show();
        }
    }

    public interface OnListener {

        /**
         * 选择条目时回调
         */
        void onSelected(Dialog dialog, UserVipBean text);

        /**
         * 点击取消时回调
         */
        void onCancel(Dialog dialog);
    }
}