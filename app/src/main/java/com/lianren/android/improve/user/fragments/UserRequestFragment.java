package com.lianren.android.improve.user.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.basicData.db.BasicBeanDao;
import com.lianren.android.improve.account.constants.UserConstants;
import com.lianren.android.improve.base.fragments.BaseFragment;
import com.lianren.android.improve.bean.BasicBean;
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.ItemBaseBean;
import com.lianren.android.improve.bean.MessageWrap;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.improve.user.activities.RequestLocalActivity;
import com.lianren.android.improve.user.adapter.UserInfoBaseAdapter;
import com.lianren.android.util.CacheManager;
import com.lianren.android.util.greendao.DaoSessionUtils;
import com.lianren.android.widget.GridItemDecoration;
import com.lianren.android.widget.SimplexToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.WhereCondition;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

import static android.app.Activity.RESULT_OK;

/**
 * @package: com.lianren.android.improve.user.fragments
 * @user:xhkj
 * @date:2019/12/19
 * @description:个人恋人要求
 **/
public class UserRequestFragment extends BaseFragment {

    @Bind(R.id.recycler_other)
    RecyclerView recyclerOther;
    private UserInfoBaseAdapter userInfoOtherAdapter;
    private UsersInfoBean usersInfoBean;
    private OptionsPickerView pvOptionChild, pvOptionMarital, pvOptionRevenue,
            pvOptionEducation, pvOptionAge, pvOptionHeight, pvOptionWeight;
    private List<BasicBean.ItemBean> optionChild, optionMarital, optionsRevenue, optionsEducation;
    private List<BasicBean.ItemBean> optionsStartAge = new ArrayList<>();
    private List<List<BasicBean.ItemBean.ChildrenBean>> optionsEndAge = new ArrayList<>();
    private List<BasicBean.ItemBean> optionsStartHeight = new ArrayList<>();
    private List<List<BasicBean.ItemBean.ChildrenBean>> optionsEndHeight = new ArrayList<>();
    private List<BasicBean.ItemBean> optionsStartWeight = new ArrayList<>();
    private List<List<BasicBean.ItemBean.ChildrenBean>> optionsEndWeight = new ArrayList<>();
    private String age_range = "-1";
    private String height_range = "-1";
    private String weight_range = "-1";

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_request;
    }

    public static UserRequestFragment newInstance(UsersInfoBean usersInfoBean) {
        Bundle args = new Bundle();
        args.putSerializable("user_info", usersInfoBean);
        UserRequestFragment fragment = new UserRequestFragment();
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
        userInfoOtherAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (position) {
                    case 0://年龄
                        initAge();
                        break;
                    case 1://居住地
                        Intent intent = new Intent(mContext, RequestLocalActivity.class);
                        intent.putExtra("user_info", usersInfoBean);
                        intent.putExtra("type", RequestLocalActivity.TYPE_REQUST_LOCAL);
                        startActivityForResult(intent, RequestLocalActivity.TYPE_REQUST_LOCAL);
                        break;
                    case 2://收入
                        initRevenue();
                        break;
                    case 3://学历
                        initEducation();
                        break;
                    case 4://身高
                        initHeight();
                        break;
                    case 5://婚姻
                        initMaritalSta();
                        break;
                    case 6://子女
                        initChild();
                        break;
                    case 7://体重
                        initWeight();
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {// 是否需要同步到application
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null)
            return;
        switch (requestCode) {
            case RequestLocalActivity.TYPE_REQUST_LOCAL:
                List<UsersInfoBean.RequireBean.DomicileRangeBean> ids = (List<UsersInfoBean.RequireBean.DomicileRangeBean>) data.getSerializableExtra("ids");
                usersInfoBean.require.domicile_range = ids;
                UserRequestFragment.this.usersInfoBean = usersInfoBean;
                List<String> ranges = new ArrayList<>();
                String value = "";
                if (ids != null) {
                    for (UsersInfoBean.RequireBean.DomicileRangeBean item : ids) {
                        ranges.add(item.key);
                        if (TextUtils.isEmpty(value)) {
                            value = item.value;
                        } else {
                            value = value + "、" + item.value;
                        }
                    }
                }
                upDateUserInfo(ranges, UserConstants.DOMICILE_RANGE);
                userInfoOtherAdapter.updataPostition(value, 1);
                break;
        }
    }

    //子女要求
    private void initChild() {
        if (pvOptionChild == null) {
            showLoadingDialog();
            int selectOption = 0;
//            List<BasicBean> basicBeans = CacheManager.readListJson(mContext, "BasicBean", BasicBean.class);
            optionChild = new ArrayList<>();
//            for (BasicBean basicBean : basicBeans) {
//                if (basicBean.type.equals("child_range")) {
//                    optionChild = basicBean.item;
//                    break;
//                }
//            }
            List<WhereCondition> whereConditions = new ArrayList<>();
            whereConditions.add(BasicBeanDao.Properties.Type.eq("child_range"));
            List<BasicBean> basic = (List<BasicBean>) DaoSessionUtils.queryConditionAll(whereConditions);
            if (basic != null && basic.size() == 1) {
                optionChild = basic.get(0).item;
            }
            if (usersInfoBean.require.child_range != null)
                for (int i = 0; i < optionChild.size(); i++) {
                    if (TextUtils.equals(optionChild.get(i).id, usersInfoBean.require.child_range.key)) {
                        selectOption = i;
                        break;
                    }
                }
            pvOptionChild = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    upDateUserInfo(optionChild.get(options1).id, UserConstants.CHILDREN_STATUS_RANGE);
                    userInfoOtherAdapter.updataPostition(optionChild.get(options1).getPickerViewText(), 6);
                }
            }).setCancelText("取消")//取消按钮文字
                    .setSubmitText("确定")//确认按钮文字
                    .setTitleSize(14)//标题文字大小
                    .setTitleText("子女要求")//标题文字
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .setTitleColor(ContextCompat.getColor(mContext, R.color.tab_selected_color))//标题文字颜色
                    .setSubmitColor(ContextCompat.getColor(mContext, R.color.day_colorPrimary))//确定按钮文字颜色
                    .setCancelColor(ContextCompat.getColor(mContext, R.color.main_gray))//取消按钮文字颜色
                    .setTitleBgColor(0xFFFFFFFF)//标题背景颜色 Night mode
                    .setBgColor(0xFFFFFFFF)//滚轮背景颜色 Night mode
                    .setItemVisibleCount(5) //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
                    .setLineSpacingMultiplier(2.0f)
                    .isAlphaGradient(true)
                    .setSelectOptions(selectOption)
                    .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .build();
            pvOptionChild.setPicker(optionChild);//二级选择器
            dismissLoadingDialog();
        }
        pvOptionChild.show();
    }

    //婚姻
    private void initMaritalSta() {//条件选择器初始化
        if (pvOptionMarital == null) {
            showLoadingDialog();
            int selectOption = 0;
//            List<BasicBean> basicBeans = CacheManager.readListJson(mContext, "BasicBean", BasicBean.class);
            optionMarital = new ArrayList<>();
//            for (BasicBean basicBean : basicBeans) {
//                if (basicBean.type.equals("marital_range")) {
//                    optionMarital = basicBean.item;
//                    break;
//                }
//            }
            List<WhereCondition> whereConditions = new ArrayList<>();
            whereConditions.add(BasicBeanDao.Properties.Type.eq("marital_range"));
            List<BasicBean> basic = (List<BasicBean>) DaoSessionUtils.queryConditionAll(whereConditions);
            if (basic != null && basic.size() == 1) {
                optionMarital = basic.get(0).item;
            }
            if (usersInfoBean.require.marital_range != null)
                for (int i = 0; i < optionMarital.size(); i++) {
                    if (optionMarital.get(i).id.equals(usersInfoBean.require.marital_range.key)) {
                        selectOption = i;
                        break;
                    }
                }
            pvOptionMarital = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    upDateUserInfo(optionMarital.get(options1).id, UserConstants.MARITAL_STATUS_RANGE);
                    userInfoOtherAdapter.updataPostition(optionMarital.get(options1).getPickerViewText(), 5);
                }
            }).setCancelText("取消")//取消按钮文字
                    .setSubmitText("确定")//确认按钮文字
                    .setTitleSize(14)//标题文字大小
                    .setTitleText("婚姻要求")//标题文字
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .setTitleColor(ContextCompat.getColor(mContext, R.color.tab_selected_color))//标题文字颜色
                    .setSubmitColor(ContextCompat.getColor(mContext, R.color.day_colorPrimary))//确定按钮文字颜色
                    .setCancelColor(ContextCompat.getColor(mContext, R.color.main_gray))//取消按钮文字颜色
                    .setTitleBgColor(0xFFFFFFFF)//标题背景颜色 Night mode
                    .setBgColor(0xFFFFFFFF)//滚轮背景颜色 Night mode
                    .setItemVisibleCount(5) //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
                    .setLineSpacingMultiplier(2.0f)
                    .isAlphaGradient(true)
                    .setSelectOptions(selectOption)
                    .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .build();
            pvOptionMarital.setPicker(optionMarital);//二级选择器
            dismissLoadingDialog();
        }
        pvOptionMarital.show();
    }

    //收入
    private void initRevenue() {//条件选择器初始化
        if (pvOptionRevenue == null) {

            showLoadingDialog();
            int selectOption = 0;
//            List<BasicBean> basicBeans = CacheManager.readListJson(mContext, "BasicBean", BasicBean.class);
            optionsRevenue = new ArrayList<>();
//            for (BasicBean basicBean : basicBeans) {
//                if (basicBean.type.equals("revenue_range")) {
//                    optionsRevenue = basicBean.item;
//                    break;
//                }
//            }
            List<WhereCondition> whereConditions = new ArrayList<>();
            whereConditions.add(BasicBeanDao.Properties.Type.eq("revenue_range"));
            List<BasicBean> basic = (List<BasicBean>) DaoSessionUtils.queryConditionAll(whereConditions);
            if (basic != null && basic.size() == 1) {
                optionsRevenue = basic.get(0).item;
            }
            if (usersInfoBean.require.revenue_range != null)
                for (int i = 0; i < optionsRevenue.size(); i++) {
                    if (optionsRevenue.get(i).id.equals(usersInfoBean.require.revenue_range.key)) {
                        selectOption = i;
                        break;
                    }
                }
            pvOptionRevenue = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    upDateUserInfo(optionsRevenue.get(options1).id, UserConstants.REVENUE_RANGE);
                    userInfoOtherAdapter.updataPostition(optionsRevenue.get(options1).getPickerViewText(), 2);
                }
            }).setCancelText("取消")//取消按钮文字
                    .setSubmitText("确定")//确认按钮文字
                    .setTitleSize(14)//标题文字大小
                    .setTitleText("收入要求")//标题文字
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .setTitleColor(ContextCompat.getColor(mContext, R.color.tab_selected_color))//标题文字颜色
                    .setSubmitColor(ContextCompat.getColor(mContext, R.color.day_colorPrimary))//确定按钮文字颜色
                    .setCancelColor(ContextCompat.getColor(mContext, R.color.main_gray))//取消按钮文字颜色
                    .setTitleBgColor(0xFFFFFFFF)//标题背景颜色 Night mode
                    .setBgColor(0xFFFFFFFF)//滚轮背景颜色 Night mode
                    .setItemVisibleCount(5) //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
                    .setLineSpacingMultiplier(2.0f)
                    .isAlphaGradient(true)
                    .setSelectOptions(selectOption)
                    .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .build();
            pvOptionRevenue.setPicker(optionsRevenue);//二级选择器
            dismissLoadingDialog();
        }
        pvOptionRevenue.show();
    }

    //学历
    private void initEducation() {//条件选择器初始化
        if (pvOptionEducation == null) {
            showLoadingDialog();
            int selectOption = 0;
//            List<BasicBean> basicBeans = CacheManager.readListJson(mContext, "BasicBean", BasicBean.class);
            optionsEducation = new ArrayList<>();
//            for (BasicBean basicBean : basicBeans) {
//                if (basicBean.type.equals("education_range")) {
//                    optionsEducation = basicBean.item;
//                    break;
//                }
//            }
            List<WhereCondition> whereConditions = new ArrayList<>();
            whereConditions.add(BasicBeanDao.Properties.Type.eq("education_range"));
            List<BasicBean> basic = (List<BasicBean>) DaoSessionUtils.queryConditionAll(whereConditions);
            if (basic != null && basic.size() == 1) {
                optionsEducation = basic.get(0).item;
            }
            if (usersInfoBean.require.education_range!=null)
            for (int i = 0; i < optionsEducation.size(); i++) {
                if (optionsEducation.get(i).id.equals(usersInfoBean.require.education_range.key)) {
                    selectOption = i;
                    break;
                }
            }
            pvOptionEducation = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    upDateUserInfo(optionsEducation.get(options1).id, UserConstants.EDUCATION_RANGE);
                    userInfoOtherAdapter.updataPostition(optionsEducation.get(options1).getPickerViewText(), 3);
                }
            }).setCancelText("取消")//取消按钮文字
                    .setSubmitText("确定")//确认按钮文字
                    .setTitleSize(14)//标题文字大小
                    .setTitleText("学历")//标题文字
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .setTitleColor(ContextCompat.getColor(mContext, R.color.tab_selected_color))//标题文字颜色
                    .setSubmitColor(ContextCompat.getColor(mContext, R.color.day_colorPrimary))//确定按钮文字颜色
                    .setCancelColor(ContextCompat.getColor(mContext, R.color.main_gray))//取消按钮文字颜色
                    .setTitleBgColor(0xFFFFFFFF)//标题背景颜色 Night mode
                    .setBgColor(0xFFFFFFFF)//滚轮背景颜色 Night mode
                    .setItemVisibleCount(5) //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
                    .setLineSpacingMultiplier(2.0f)
                    .isAlphaGradient(true)
                    .setSelectOptions(selectOption)
                    .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .build();
            pvOptionEducation.setPicker(optionsEducation);//二级选择器
            dismissLoadingDialog();
        }
        pvOptionEducation.show();
    }

    //年龄
    private void initAge() {
        if (pvOptionAge == null) {
            showLoadingDialog();
            int selectOption = 0;
            int secondOption = 0;
//            List<BasicBean> basicBeans = CacheManager.readListJson(mContext, "BasicBean", BasicBean.class);
            optionsStartAge = new ArrayList<>();
            BasicBean.RangBean ageRange = null;
//            for (BasicBean basicBean : basicBeans) {
//                if (basicBean.type.equals("age")) {
//                    ageRange = basicBean.range;
//                    break;
//                }
//            }
            List<WhereCondition> whereConditions = new ArrayList<>();
            whereConditions.add(BasicBeanDao.Properties.Type.eq("age"));
            List<BasicBean> basic = (List<BasicBean>) DaoSessionUtils.queryConditionAll(whereConditions);
            if (basic != null && basic.size() == 1) {
                ageRange = basic.get(0).range;
            }
            if (ageRange == null) return;
            int startAge = Integer.parseInt(ageRange.start);
            int endAge = Integer.parseInt(ageRange.end);
            optionsStartAge.add(new BasicBean.ItemBean("-1", "不限"));
            for (int i = startAge; i <= endAge; i++) {
                optionsStartAge.add(new BasicBean.ItemBean(i + "", i + ""));
            }
            for (int i = 0; i < optionsStartAge.size(); i++) {
                List<BasicBean.ItemBean.ChildrenBean> childrenBeans = new ArrayList<>();
                BasicBean.ItemBean.ChildrenBean itemBean1 = new BasicBean.ItemBean.ChildrenBean();
                itemBean1.id = "-1";
                itemBean1.name = "不限";
                childrenBeans.add(itemBean1);
                for (int j = i + 1; j < optionsStartAge.size(); j++) {
                    BasicBean.ItemBean.ChildrenBean childrenBean = new BasicBean.ItemBean.ChildrenBean();
                    childrenBean.id = optionsStartAge.get(j).id;
                    childrenBean.name = optionsStartAge.get(j).name;
                    childrenBeans.add(childrenBean);
                }
                optionsEndAge.add(childrenBeans);
            }

            pvOptionAge = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2, int options3, View v) {
                    if (optionsStartAge.get(options1).id.equals("-1") && optionsEndAge.get(options1).get(option2).id.equals("-1")) {
                        age_range = optionsStartAge.get(options1).id;

                    } else if (optionsStartAge.get(options1).id.equals("-1") && !optionsEndAge.get(options1).get(option2).id.equals("-1")) {
                        age_range = optionsEndAge.get(options1).get(option2).id + "-";

                    } else if (optionsEndAge.get(options1).get(option2).id.equals("-1")) {
                        age_range = optionsStartAge.get(options1).id + "+";
                    } else {
                        age_range = optionsStartAge.get(options1).id + "~" + optionsEndAge.get(options1).get(option2).id;
                    }
                    upDateUserInfo(age_range, UserConstants.AGE_RANGE);
                    userInfoOtherAdapter.updataPostition(String.format("%s-%s", optionsStartAge.get(options1).name, optionsEndAge.get(options1).get(option2).name), 0);

                }
            }).setCancelText("取消")//取消按钮文字
                    .setSubmitText("确定")//确认按钮文字
                    .setTitleSize(14)//标题文字大小
                    .setTitleText("年龄(岁)")//标题文字
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .setTitleColor(ContextCompat.getColor(mContext, R.color.tab_selected_color))//标题文字颜色
                    .setSubmitColor(ContextCompat.getColor(mContext, R.color.day_colorPrimary))//确定按钮文字颜色
                    .setCancelColor(ContextCompat.getColor(mContext, R.color.main_gray))//取消按钮文字颜色
                    .setTitleBgColor(0xFFFFFFFF)//标题背景颜色 Night mode
                    .setBgColor(0xFFFFFFFF)//滚轮背景颜色 Night mode
                    .setItemVisibleCount(5) //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
                    .setLineSpacingMultiplier(2.0f)
                    .isAlphaGradient(true)
                    .setSelectOptions(selectOption, secondOption)
                    .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .build();
            pvOptionAge.setPicker(optionsStartAge, optionsEndAge);//二级选择器
            dismissLoadingDialog();
        }
        pvOptionAge.show();
    }

    //身高
    private void initHeight() {
        if (pvOptionHeight == null) {
            showLoadingDialog();
            int selectOption = 0;
            int secondOption = 0;
//            List<BasicBean> basicBeans = CacheManager.readListJson(mContext, "BasicBean", BasicBean.class);
            optionsStartHeight = new ArrayList<>();
            BasicBean.RangBean ageRange = null;
//            for (BasicBean basicBean : basicBeans) {
//                if (basicBean.type.equals("height")) {
//                    ageRange = basicBean.range;
//                    break;
//                }
//            }
            List<WhereCondition> whereConditions = new ArrayList<>();
            whereConditions.add(BasicBeanDao.Properties.Type.eq("height"));
            List<BasicBean> basic = (List<BasicBean>) DaoSessionUtils.queryConditionAll(whereConditions);
            if (basic != null && basic.size() == 1) {
                ageRange = basic.get(0).range;
            }
            if (ageRange == null) return;
            int startAge = Integer.parseInt(ageRange.start);
            int endAge = Integer.parseInt(ageRange.end);
            optionsStartHeight.add(new BasicBean.ItemBean("-1", "不限"));
            for (int i = startAge; i <= endAge; i++) {
                optionsStartHeight.add(new BasicBean.ItemBean(i + "", i + ""));
            }
            for (int i = 0; i < optionsStartHeight.size(); i++) {
                List<BasicBean.ItemBean.ChildrenBean> childrenBeans = new ArrayList<>();
                BasicBean.ItemBean.ChildrenBean itemBean1 = new BasicBean.ItemBean.ChildrenBean();
                itemBean1.id = "-1";
                itemBean1.name = "不限";
                childrenBeans.add(itemBean1);
                for (int j = i + 1; j < optionsStartHeight.size(); j++) {
                    BasicBean.ItemBean.ChildrenBean childrenBean = new BasicBean.ItemBean.ChildrenBean();
                    childrenBean.id = optionsStartHeight.get(j).id;
                    childrenBean.name = optionsStartHeight.get(j).name;
                    childrenBeans.add(childrenBean);
                }
                optionsEndHeight.add(childrenBeans);
            }

            pvOptionHeight = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2, int options3, View v) {
                    if (optionsStartHeight.get(options1).id.equals("-1") && optionsEndHeight.get(options1).get(option2).id.equals("-1")) {
                        height_range = optionsStartHeight.get(options1).id;
                    } else if (optionsStartHeight.get(options1).id.equals("-1") && !optionsEndHeight.get(options1).get(option2).id.equals("-1")) {
                        height_range = optionsEndHeight.get(options1).get(option2).id + "-";
                    } else if (optionsEndHeight.get(options1).get(option2).id.equals("-1")) {
                        height_range = optionsStartHeight.get(options1).id + "+";
                    } else {
                        height_range = optionsStartHeight.get(options1).id + "~" + optionsEndHeight.get(options1).get(option2).id;
                    }
                    upDateUserInfo(height_range, UserConstants.HEIGHT_RANGE);
                    userInfoOtherAdapter.updataPostition(String.format("%s-%s", optionsStartHeight.get(options1).name, optionsEndHeight.get(options1).get(option2).name), 4);

                }
            }).setCancelText("取消")//取消按钮文字
                    .setSubmitText("确定")//确认按钮文字
                    .setTitleSize(14)//标题文字大小
                    .setTitleText("身高cm")//标题文字
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .setTitleColor(ContextCompat.getColor(mContext, R.color.tab_selected_color))//标题文字颜色
                    .setSubmitColor(ContextCompat.getColor(mContext, R.color.day_colorPrimary))//确定按钮文字颜色
                    .setCancelColor(ContextCompat.getColor(mContext, R.color.main_gray))//取消按钮文字颜色
                    .setTitleBgColor(0xFFFFFFFF)//标题背景颜色 Night mode
                    .setBgColor(0xFFFFFFFF)//滚轮背景颜色 Night mode
                    .setItemVisibleCount(5) //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
                    .setLineSpacingMultiplier(2.0f)
                    .isAlphaGradient(true)
                    .setSelectOptions(selectOption, secondOption)
                    .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .build();
            pvOptionHeight.setPicker(optionsStartHeight, optionsEndHeight);//二级选择器
            dismissLoadingDialog();
        }
        pvOptionHeight.show();
    }

    //体重
    private void initWeight() {
        if (pvOptionWeight == null) {
            showLoadingDialog();
            int selectOption = 0;
            int secondOption = 0;
//            List<BasicBean> basicBeans = CacheManager.readListJson(mContext, "BasicBean", BasicBean.class);
            optionsStartWeight = new ArrayList<>();
            BasicBean.RangBean ageRange = null;
//            for (BasicBean basicBean : basicBeans) {
//                if (basicBean.type.equals("weight")) {
//                    ageRange = basicBean.range;
//                    break;
//                }
//            }
            List<WhereCondition> whereConditions = new ArrayList<>();
            whereConditions.add(BasicBeanDao.Properties.Type.eq("weight"));
            List<BasicBean> basic = (List<BasicBean>) DaoSessionUtils.queryConditionAll(whereConditions);
            if (basic != null && basic.size() == 1) {
                ageRange = basic.get(0).range;
            }
            if (ageRange == null) return;
            int startAge = Integer.parseInt(ageRange.start);
            int endAge = Integer.parseInt(ageRange.end);
            optionsStartWeight.add(new BasicBean.ItemBean("-1", "不限"));
            for (int i = startAge; i <= endAge; i++) {
                optionsStartWeight.add(new BasicBean.ItemBean(i + "", i + ""));
            }
            for (int i = 0; i < optionsStartWeight.size(); i++) {
                List<BasicBean.ItemBean.ChildrenBean> childrenBeans = new ArrayList<>();
                BasicBean.ItemBean.ChildrenBean itemBean1 = new BasicBean.ItemBean.ChildrenBean();
                itemBean1.id = "-1";
                itemBean1.name = "不限";
                childrenBeans.add(itemBean1);
                for (int j = i + 1; j < optionsStartWeight.size(); j++) {
                    BasicBean.ItemBean.ChildrenBean childrenBean = new BasicBean.ItemBean.ChildrenBean();
                    childrenBean.id = optionsStartWeight.get(j).id;
                    childrenBean.name = optionsStartWeight.get(j).name;
                    childrenBeans.add(childrenBean);
                }
                optionsEndWeight.add(childrenBeans);
            }

            pvOptionWeight = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2, int options3, View v) {
                    if (optionsStartWeight.get(options1).id.equals("-1") && optionsEndWeight.get(options1).get(option2).id.equals("-1")) {
                        weight_range = optionsStartWeight.get(options1).id;
                    } else if (optionsStartWeight.get(options1).id.equals("-1") && !optionsEndWeight.get(options1).get(option2).id.equals("-1")) {
                        weight_range = optionsEndWeight.get(options1).get(option2).id + "-";
                    } else if (optionsEndWeight.get(options1).get(option2).id.equals("-1")) {
                        weight_range = optionsStartWeight.get(options1).id + "+";
                    } else {
                        weight_range = optionsStartWeight.get(options1).id + "~" + optionsEndWeight.get(options1).get(option2).id;
                    }
                    upDateUserInfo(weight_range, UserConstants.WEIGHT_RANGE);
                    userInfoOtherAdapter.updataPostition(String.format("%s-%s", optionsStartWeight.get(options1).name, optionsEndWeight.get(options1).get(option2).name), 7);
                }
            }).setCancelText("取消")//取消按钮文字
                    .setSubmitText("确定")//确认按钮文字
                    .setTitleSize(14)//标题文字大小
                    .setTitleText("体重kg")//标题文字
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .setTitleColor(ContextCompat.getColor(mContext, R.color.tab_selected_color))//标题文字颜色
                    .setSubmitColor(ContextCompat.getColor(mContext, R.color.day_colorPrimary))//确定按钮文字颜色
                    .setCancelColor(ContextCompat.getColor(mContext, R.color.main_gray))//取消按钮文字颜色
                    .setTitleBgColor(0xFFFFFFFF)//标题背景颜色 Night mode
                    .setBgColor(0xFFFFFFFF)//滚轮背景颜色 Night mode
                    .setItemVisibleCount(5) //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
                    .setLineSpacingMultiplier(2.0f)
                    .isAlphaGradient(true)
                    .setSelectOptions(selectOption, secondOption)
                    .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .build();
            pvOptionWeight.setPicker(optionsStartWeight, optionsEndWeight);//二级选择器
            dismissLoadingDialog();
        }
        pvOptionWeight.show();
    }

    private void upDateUserInfo(final Object content, int type) {
        LRApi.upDateUserRequest(content, type,
                new CommonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        SimplexToast.show(mContext, "网络错误");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            Type type = new TypeToken<ResultBean>() {
                            }.getType();
                            ResultBean bean = new Gson().fromJson(responseString, type);
                            if (bean.isSuccess()) {
                                EventBus.getDefault().post(new MessageWrap(Constants.REFRESH_USER, null));
                            } else {
                                SimplexToast.show(mContext, bean.error.message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            SimplexToast.show(mContext, "修改失败");
                        }
                    }
                });
    }

    @Override
    protected void initData() {
        super.initData();
        usersInfoBean = (UsersInfoBean) getArguments().getSerializable("user_info");
        UsersInfoBean.RequireBean requireBean = usersInfoBean.require;
        if (requireBean == null) return;
        String domicile_range = "";
        List<ItemBaseBean> itemOthers = new ArrayList<>();//其他资料
        itemOthers.add(new ItemBaseBean("年龄", requireBean.age_range != null ? requireBean.age_range.value : ""));
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
        itemOthers.add(new ItemBaseBean("收入", requireBean.revenue_range != null ? requireBean.revenue_range.value : ""));
        itemOthers.add(new ItemBaseBean("学历", requireBean.education_range != null ? requireBean.education_range.value : ""));
        itemOthers.add(new ItemBaseBean("身高", requireBean.height_range != null ? requireBean.height_range.value : ""));
        itemOthers.add(new ItemBaseBean("婚姻", requireBean.marital_range != null ? requireBean.marital_range.value : ""));
        itemOthers.add(new ItemBaseBean("子女", requireBean.child_range != null ? requireBean.child_range.value : ""));
        itemOthers.add(new ItemBaseBean("体重", requireBean.weight_range != null ? requireBean.weight_range.value : ""));
        userInfoOtherAdapter.setNewData(itemOthers);
    }
}
