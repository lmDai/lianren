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
 * @description:其他用户基本资料
 **/
public class OtherUserBaseFragment extends BaseFragment {
    @Bind(R.id.recycler_base)
    RecyclerView recyclerBase;
    @Bind(R.id.recycler_other)
    RecyclerView recyclerOther;
    private UserInfoBaseAdapter userInfoBaseAdapter;
    private UserInfoBaseAdapter userInfoOtherAdapter;
    private UsersInfoBean usersInfoBean;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_other_user_base;
    }

    public static OtherUserBaseFragment newInstance(UsersInfoBean usersInfoBean) {
        Bundle args = new Bundle();
        args.putSerializable("user_info", usersInfoBean);
        OtherUserBaseFragment fragment = new OtherUserBaseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        recyclerBase.setNestedScrollingEnabled(false);
        recyclerOther.setNestedScrollingEnabled(false);
        userInfoBaseAdapter = new UserInfoBaseAdapter();
        recyclerBase.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerBase.addItemDecoration(new GridItemDecoration(16));
        userInfoBaseAdapter.bindToRecyclerView(recyclerBase);

        userInfoOtherAdapter = new UserInfoBaseAdapter();
        recyclerOther.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerOther.addItemDecoration(new GridItemDecoration(16));
        userInfoOtherAdapter.bindToRecyclerView(recyclerOther);
    }

    @Override
    protected void initData() {
        super.initData();
        usersInfoBean = (UsersInfoBean) getArguments().getSerializable("user_info");
        List<ItemBaseBean> itemBaseBeans = new ArrayList<>();//基础资料
        UsersInfoBean.BaseBean baseBean = usersInfoBean.base;
        if (baseBean == null) return;
        if (!TextUtils.isEmpty(baseBean.nickname))
            itemBaseBeans.add(new ItemBaseBean("昵称", baseBean.nickname));
        if (!TextUtils.isEmpty(baseBean.sex_zh))
            itemBaseBeans.add(new ItemBaseBean("性别", baseBean.sex_zh));
        itemBaseBeans.add(new ItemBaseBean("年龄", TextUtils.isEmpty(baseBean.age)?"":baseBean.age + "岁"));
        if (!TextUtils.isEmpty(baseBean.domicile_name))
            itemBaseBeans.add(new ItemBaseBean("居住地", baseBean.domicile_name));
        if (!TextUtils.isEmpty(baseBean.revenue_zh))
            itemBaseBeans.add(new ItemBaseBean("年收入", baseBean.revenue_zh));
        if (!TextUtils.isEmpty(baseBean.education_zh))
            itemBaseBeans.add(new ItemBaseBean("学历", baseBean.education_zh));
        itemBaseBeans.add(new ItemBaseBean("身高", TextUtils.isEmpty(baseBean.height)?"":baseBean.height + "cm"));
        if (!TextUtils.isEmpty(baseBean.marital_status_zh))
            itemBaseBeans.add(new ItemBaseBean("婚姻", baseBean.marital_status_zh));
        if (!TextUtils.isEmpty(baseBean.is_has_children_zh))
            itemBaseBeans.add(new ItemBaseBean("子女", baseBean.is_has_children_zh));

        List<ItemBaseBean> itemOthers = new ArrayList<>();//其他资料
        if (!TextUtils.isEmpty(baseBean.industry_name))
            itemOthers.add(new ItemBaseBean("行业", baseBean.industry_name));
        if (!TextUtils.isEmpty(baseBean.occupation_name))
            itemOthers.add(new ItemBaseBean("职业", baseBean.occupation_name));
        if (!TextUtils.isEmpty(baseBean.birth_place_name))
            itemOthers.add(new ItemBaseBean("籍贯", baseBean.birth_place_name));
        if (!TextUtils.isEmpty(baseBean.constellation_zh))
            itemOthers.add(new ItemBaseBean("星座", baseBean.constellation_zh));
        if (!TextUtils.isEmpty(baseBean.school_name))
            itemOthers.add(new ItemBaseBean("学校", baseBean.school_name));
        if (!TextUtils.isEmpty(baseBean.profession_name))
            itemOthers.add(new ItemBaseBean("专业", baseBean.profession_name));
        itemOthers.add(new ItemBaseBean("体重", TextUtils.isEmpty(baseBean.weight)?"":baseBean.weight + "kg"));

        userInfoBaseAdapter.setNewData(itemBaseBeans);
        userInfoOtherAdapter.setNewData(itemOthers);
    }
}
