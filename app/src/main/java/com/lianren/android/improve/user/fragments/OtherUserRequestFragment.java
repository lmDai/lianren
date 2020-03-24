package com.lianren.android.improve.user.fragments;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.lianren.android.R;
import com.lianren.android.improve.base.fragments.BaseFragment;
import com.lianren.android.improve.bean.ItemBaseBean;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.user.adapter.UserInfoBaseAdapter;
import com.lianren.android.widget.GridItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * @package: com.lianren.android.improve.user.fragments
 * @user:xhkj
 * @date:2019/12/19
 * @description:其他用户恋人要求
 **/
public class OtherUserRequestFragment extends BaseFragment {

    @Bind(R.id.recycler_other)
    RecyclerView recyclerOther;
    private UserInfoBaseAdapter userInfoOtherAdapter;
    private UsersInfoBean usersInfoBean;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_other_user_request;
    }

    public static OtherUserRequestFragment newInstance(UsersInfoBean usersInfoBean) {
        Bundle args = new Bundle();
        args.putSerializable("user_info", usersInfoBean);
        OtherUserRequestFragment fragment = new OtherUserRequestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        recyclerOther.setNestedScrollingEnabled(false);
        userInfoOtherAdapter = new UserInfoBaseAdapter();
        recyclerOther.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerOther.addItemDecoration(new GridItemDecoration(16));
        userInfoOtherAdapter.bindToRecyclerView(recyclerOther);
    }

    @Override
    protected void initData() {
        super.initData();
        usersInfoBean = (UsersInfoBean) getArguments().getSerializable("user_info");
        UsersInfoBean.RequireBean requireBean = usersInfoBean.require;
        if (requireBean == null) return;
        String domicile_range = "";
        List<ItemBaseBean> itemOthers = new ArrayList<>();//其他资料
        if (requireBean.age_range != null)
            itemOthers.add(new ItemBaseBean("年龄", requireBean.age_range.value));
        if (requireBean.domicile_range != null && requireBean.domicile_range.size() > 0) {
            for (UsersInfoBean.RequireBean.DomicileRangeBean bean : requireBean.domicile_range) {
                if (TextUtils.isEmpty(domicile_range)) {
                    domicile_range = bean.value;
                } else {
                    domicile_range = domicile_range + "、" + bean.value;
                }
            }
        }
        itemOthers.add(new ItemBaseBean("居住地", domicile_range));
        if (requireBean.revenue_range != null)
            itemOthers.add(new ItemBaseBean("收入", requireBean.revenue_range.value));
        if (requireBean.education_range != null)
            itemOthers.add(new ItemBaseBean("学历", requireBean.education_range.value));
        if (requireBean.height_range != null)
            itemOthers.add(new ItemBaseBean("身高", requireBean.height_range.value));
        if (requireBean.marital_range != null)
            itemOthers.add(new ItemBaseBean("婚姻", requireBean.marital_range.value));
        if (requireBean.child_range != null)
            itemOthers.add(new ItemBaseBean("子女", requireBean.child_range.value));
        if (requireBean.weight_range != null)
            itemOthers.add(new ItemBaseBean("体重", requireBean.weight_range.value));

        userInfoOtherAdapter.setNewData(itemOthers);
    }
}
