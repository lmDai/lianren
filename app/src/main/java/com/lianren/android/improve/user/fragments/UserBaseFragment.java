package com.lianren.android.improve.user.fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lianren.android.AppContext;
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
import com.lianren.android.improve.user.activities.ModifyNickNameActivity;
import com.lianren.android.improve.user.activities.ProfessionActivity;
import com.lianren.android.improve.user.activities.SchoolListActivity;
import com.lianren.android.improve.user.activities.UserPhotoActivity;
import com.lianren.android.improve.user.adapter.UserInfoBaseAdapter;
import com.lianren.android.util.DateUtil;
import com.lianren.android.util.ImageLoader;
import com.lianren.android.util.TDevice;
import com.lianren.android.util.greendao.DaoSessionUtils;
import com.lianren.android.widget.GridItemDecoration;
import com.lianren.android.widget.SimplexToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.WhereCondition;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.lianren.android.improve.user.fragments.UserInfoFragment.ACTION_UP_DATE;

/**
 * @package: com.lianren.android.improve.user.fragments
 * @user:xhkj
 * @date:2019/12/19
 * @description:个人基本资料
 **/
public class UserBaseFragment extends BaseFragment {
    @Bind(R.id.recycler_other)
    RecyclerView recyclerOther;
    @Bind(R.id.tv_nickname)
    TextView tvNickname;
    @Bind(R.id.img_avater)
    CircleImageView imgAvater;
    @Bind(R.id.tv_age)
    TextView tvAge;
    @Bind(R.id.tv_sex)
    TextView tvSex;
    @Bind(R.id.tv_democil)
    TextView tvDemocil;
    @Bind(R.id.img_age)
    ImageView imgAge;
    @Bind(R.id.img_sex)
    ImageView imgSex;


    private UserInfoBaseAdapter userInfoOtherAdapter;
    private UsersInfoBean usersInfoBean;
    private TimePickerView pvTime;//年龄
    private OptionsPickerView pvOptionSex, pvOptionRevenue, pvOptionEducation,
            pvOptionMarital, pvOptionChild, pvOptionHeight, pvOptionCity;
    private List<BasicBean.ItemBean> options1Items, optionsRevenue, optionsEducation,
            optionMarital, optionChild, optionHeight;
    private List<BasicBean.ItemBean> optionsArea = new ArrayList<>();
    private List<List<BasicBean.ItemBean.ChildrenBean>> optionsCity = new ArrayList<>();

    //其他
    private OptionsPickerView pvOptionIndustry, pvOptionOccupation, pvOptionsBirth,
            pvOptionConstellation, pvOptionWeight;
    private List<BasicBean.ItemBean> optionsIndustry, optionsOccupation,
            optionsConstellation, optionsWeight;

    private List<BasicBean.ItemBean> optionsBirthArea = new ArrayList<>();
    private List<List<BasicBean.ItemBean.ChildrenBean>> optionsBirthCity = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_base;
    }

    public static UserBaseFragment newInstance(UsersInfoBean usersInfoBean) {
        Bundle args = new Bundle();
        args.putSerializable("user_info", usersInfoBean);
        UserBaseFragment fragment = new UserBaseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerLocalReceiver();
    }

    protected LocalBroadcastManager mManager;
    private BroadcastReceiver mReceiver;

    private void registerLocalReceiver() {
        if (mManager == null)
            mManager = LocalBroadcastManager.getInstance(mContext);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_UP_DATE);
        if (mReceiver == null)
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (ACTION_UP_DATE.equals(action)) {
                        usersInfoBean = (UsersInfoBean) intent.getSerializableExtra("user_info_bean");
                        setData(usersInfoBean);
                    }
                }
            };
        mManager.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mManager.unregisterReceiver(mReceiver);
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
                    case 0://学历
                        initEducation();
                        break;
                    case 1://身高
                        initHeight();
                        break;
                    case 2://年收入
                        initRevenue();
                        break;
                    case 3://婚姻
                        initMaritalSta();
                        break;
                    case 4://体重
                        initWeight();
                        break;
                    case 5://子女
                        initChild();
                        break;

                    case 6://从事行业
                        initIndustry();
                        break;
                    case 7://从事职业
                        initOccupation();
                        break;
                    case 8://籍贯
                        initBirth();
                        break;
                    case 9://星座
                        initConstellation();
                        break;
                    case 10://学校
                        Intent intent = new Intent(mContext, SchoolListActivity.class);
                        intent.putExtra("user_info", usersInfoBean);
                        intent.putExtra("type", SchoolListActivity.TYPE_SCHOOL);
                        startActivityForResult(intent, SchoolListActivity.TYPE_SCHOOL);
                        break;
                    case 11:
                        Intent intentp = new Intent(mContext, ProfessionActivity.class);
                        intentp.putExtra("user_info", usersInfoBean);
                        intentp.putExtra("type", ProfessionActivity.TYPE_SCHOOL_Profession);
                        startActivityForResult(intentp, ProfessionActivity.TYPE_SCHOOL_Profession);
                        break;
                }
            }
        });
    }

    private void upDateUserInfo(final String content, int type, final int position) {
        LRApi.upDateUserInfo(content, type,
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
        setData(usersInfoBean);
    }

    private void setData(UsersInfoBean usersInfoBean) {
        this.usersInfoBean = usersInfoBean;
        UsersInfoBean.BaseBean baseBean = usersInfoBean.base;
        if (usersInfoBean == null) return;
        if (baseBean == null) return;
        if (usersInfoBean.advance != null && usersInfoBean.advance.ora != null) {
            if (TextUtils.equals(usersInfoBean.advance.ora.status, "pass")) {
                imgAge.setVisibility(View.VISIBLE);
            } else {
                imgAge.setVisibility(View.GONE);
            }
        } else {
            imgAge.setVisibility(View.GONE);
        }
        imgSex.setVisibility(TextUtils.isEmpty(baseBean.sex_zh) ? View.GONE : View.VISIBLE);

        tvNickname.setText(baseBean.nickname);
        ImageLoader.loadImage(getImgLoader(), imgAvater, baseBean.avatar_url);
        tvAge.setText(TextUtils.isEmpty(baseBean.age) ? "" : baseBean.age + "岁");
        tvSex.setText(TextUtils.isEmpty(baseBean.sex_zh) ? "" : baseBean.sex_zh);
        tvDemocil.setText(TextUtils.isEmpty(baseBean.domicile_name) ? "" : baseBean.domicile_name);

        List<ItemBaseBean> itemOthers = new ArrayList<>();//其他资料
        itemOthers.add(new ItemBaseBean("学历", TextUtils.isEmpty(baseBean.education_zh) ? "" : baseBean.education_zh));
        itemOthers.add(new ItemBaseBean("身高", TextUtils.isEmpty(baseBean.height) || TextUtils.equals(baseBean.height, "0") ? "" : baseBean.height + "cm"));
        itemOthers.add(new ItemBaseBean("年收入", TextUtils.isEmpty(baseBean.revenue_zh) ? "" : baseBean.revenue_zh));
        itemOthers.add(new ItemBaseBean("婚姻", TextUtils.isEmpty(baseBean.marital_status_zh) ? "" : baseBean.marital_status_zh));
        itemOthers.add(new ItemBaseBean("体重", TextUtils.isEmpty(baseBean.weight) || TextUtils.equals(baseBean.weight, "0") ? "" : baseBean.weight + "kg"));
        itemOthers.add(new ItemBaseBean("子女", TextUtils.isEmpty(baseBean.is_has_children_zh) ? "" : baseBean.is_has_children_zh));

        itemOthers.add(new ItemBaseBean("行业", baseBean.industry_name));
        itemOthers.add(new ItemBaseBean("职业", baseBean.occupation_name));
        itemOthers.add(new ItemBaseBean("籍贯", baseBean.birth_place_name));
        itemOthers.add(new ItemBaseBean("星座", baseBean.constellation_zh));
        itemOthers.add(new ItemBaseBean("学校", baseBean.school_name));
        itemOthers.add(new ItemBaseBean("专业", baseBean.profession_name));

        userInfoOtherAdapter.setNewData(itemOthers);
        userInfoOtherAdapter.notifyDataSetChanged();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {// 是否需要同步到application
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null)
            return;
        switch (requestCode) {
            case ModifyNickNameActivity.TYPE_NICKNAME:
                usersInfoBean = (UsersInfoBean) data.getSerializableExtra("user_info");
                setData(usersInfoBean);
                break;
            case SchoolListActivity.TYPE_SCHOOL:
                BasicBean.ItemBean itemBean = (BasicBean.ItemBean) data.getSerializableExtra("user_school");
                upDateUserInfo(itemBean.id, UserConstants.SCHOOL_ID, 10);
                userInfoOtherAdapter.updataPostition(itemBean.name, 10);
                break;
            case ProfessionActivity.TYPE_SCHOOL_Profession:
                BasicBean.ItemBean.ChildrenBean itemProfession = (BasicBean.ItemBean.ChildrenBean) data.getSerializableExtra("user_school_profess");
                upDateUserInfo(itemProfession.id, UserConstants.PROFESSION_ID, 11);
                userInfoOtherAdapter.updataPostition(itemProfession.name, 11);
                break;
        }
    }

    //出生日期选择
    private void initTimePicker() {//Dialog 模式下，在底部弹出
        if (pvTime == null) {
            showLoadingDialog();
            Calendar selectedDate;
            if (TextUtils.isEmpty(usersInfoBean.base.birthday)) {
                selectedDate = Calendar.getInstance();
                selectedDate.set(1995, 1, 1);
            } else
                selectedDate = DateUtil.String2Calendar(usersInfoBean.base.birthday);
            Calendar calendar = Calendar.getInstance();
            calendar.set(new Date().getYear() - 18 + 1900, new Date().getMonth(), new Date().getDate());
            Calendar startDate = Calendar.getInstance();
            startDate.set(new Date().getYear() - 55 + 1900, 1, 1);

            pvTime = new TimePickerBuilder(mContext, new OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    int ageVal = TDevice.getCurrentAgeByBirthdate(getTime(date));
                    upDateUserInfo(getTime(date), UserConstants.BIRTHDAY, 2);
                    tvAge.setText(ageVal + "岁");
                }
            }).setType(new boolean[]{true, true, true, false, false, false})
                    .setCancelText("取消")//取消按钮文字
                    .setSubmitText("确定")//确认按钮文字
                    .setTitleSize(14)//标题文字大小
                    .setTitleText("出生日期")//标题文字
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .isCyclic(false)//是否循环滚动
                    .setTitleColor(ContextCompat.getColor(mContext, R.color.tab_selected_color))//标题文字颜色
                    .setSubmitColor(ContextCompat.getColor(mContext, R.color.day_colorPrimary))//确定按钮文字颜色
                    .setCancelColor(ContextCompat.getColor(mContext, R.color.main_gray))//取消按钮文字颜色
                    .setTitleBgColor(0xFFFFFFFF)//标题背景颜色 Night mode
                    .setBgColor(0xFFFFFFFF)//滚轮背景颜色 Night mode
                    .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                    .setRangDate(startDate, calendar)
                    .isDialog(true)
                    .setItemVisibleCount(5) //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
                    .setLineSpacingMultiplier(2.0f)
                    .isAlphaGradient(true)
                    .build();
            Dialog mDialog = pvTime.getDialog();
            if (mDialog != null) {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        Gravity.BOTTOM);
                params.leftMargin = 0;
                params.rightMargin = 0;
                pvTime.getDialogContainerLayout().setLayoutParams(params);
                Window dialogWindow = mDialog.getWindow();
                if (dialogWindow != null) {
                    dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//修改动画样式
                    dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
                    dialogWindow.setDimAmount(0.3f);
                }
            }
            dismissLoadingDialog();
        }
        pvTime.show();
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    private void initOptionSex() {//条件选择器初始化
        if (pvOptionSex == null) {
            showLoadingDialog();
            int selectOption = 0;
            options1Items = new ArrayList<>();
//            List<BasicBean> basicBeans = CacheManager.readListJson(mContext, "BasicBean", BasicBean.class);
            List<WhereCondition> whereConditions = new ArrayList<>();
            whereConditions.add(BasicBeanDao.Properties.Type.eq("sex"));
            List<BasicBean> basic = (List<BasicBean>) DaoSessionUtils.queryConditionAll(whereConditions);
            if (basic != null && basic.size() == 1) {
                options1Items = basic.get(0).item;
            }
//            for (BasicBean basicBean : basicBeans) {
//                if (basicBean.type.equals("sex")) {
//                    options1Items = basicBean.item;
//                    break;
//                }
//            }
            for (int i = 0; i < options1Items.size(); i++) {
                if (options1Items.get(i).id.equals(usersInfoBean.base.sex)) {
                    selectOption = i;
                    break;
                }
            }
            pvOptionSex = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    upDateUserInfo(options1Items.get(options1).id, UserConstants.SEX, 3);
                    tvSex.setText(options1Items.get(options1).getPickerViewText());
                    imgSex.setVisibility(View.VISIBLE);
                }
            }).setCancelText("取消")//取消按钮文字
                    .setSubmitText("确定")//确认按钮文字
                    .setTitleSize(14)//标题文字大小
                    .setTitleText("性别")//标题文字
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
            pvOptionSex.setPicker(options1Items);//二级选择器
            dismissLoadingDialog();
        }
        pvOptionSex.show();

    }

    private void initRevenue() {//条件选择器初始化
        if (pvOptionRevenue == null) {

            showLoadingDialog();
            int selectOption = 0;
//            List<BasicBean> basicBeans = CacheManager.readListJson(mContext, "BasicBean", BasicBean.class);
            optionsRevenue = new ArrayList<>();
//            for (BasicBean basicBean : basicBeans) {
//                if (basicBean.type.equals("revenue")) {
//                    optionsRevenue = basicBean.item;
//                    break;
//                }
//            }
            List<WhereCondition> whereConditions = new ArrayList<>();
            whereConditions.add(BasicBeanDao.Properties.Type.eq("revenue"));
            List<BasicBean> basic = (List<BasicBean>) DaoSessionUtils.queryConditionAll(whereConditions);
            if (basic != null && basic.size() == 1) {
                optionsRevenue = basic.get(0).item;
            }

            for (int i = 0; i < optionsRevenue.size(); i++) {
                if (optionsRevenue.get(i).id.equals(usersInfoBean.base.revenue_code)) {
                    selectOption = i;
                    break;
                }
            }
            pvOptionRevenue = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    upDateUserInfo(optionsRevenue.get(options1).id, UserConstants.REVENUE_CODE, 2);
                    userInfoOtherAdapter.updataPostition(optionsRevenue.get(options1).getPickerViewText(), 2);
                }
            }).setCancelText("取消")//取消按钮文字
                    .setSubmitText("确定")//确认按钮文字
                    .setTitleSize(14)//标题文字大小
                    .setTitleText("年收入")//标题文字
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

    private void initEducation() {//条件选择器初始化
        if (pvOptionEducation == null) {
            showLoadingDialog();
            int selectOption = 0;
//            List<BasicBean> basicBeans = CacheManager.readListJson(mContext, "BasicBean", BasicBean.class);
            optionsEducation = new ArrayList<>();
//            for (BasicBean basicBean : basicBeans) {
//                if (basicBean.type.equals("education")) {
//                    optionsEducation = basicBean.item;
//                    break;
//                }
//            }
            List<WhereCondition> whereConditions = new ArrayList<>();
            whereConditions.add(BasicBeanDao.Properties.Type.eq("education"));
            List<BasicBean> basic = (List<BasicBean>) DaoSessionUtils.queryConditionAll(whereConditions);
            if (basic != null && basic.size() == 1) {
                optionsEducation = basic.get(0).item;
            }
            if (!TextUtils.isEmpty(usersInfoBean.base.education_zh)) {
                for (int i = 0; i < optionsEducation.size(); i++) {
                    if (optionsEducation.get(i).id.equals(usersInfoBean.base.education_code)) {
                        selectOption = i;
                        break;
                    }
                }
            } else {
                for (int i = 0; i < optionsEducation.size(); i++) {
                    if (optionsEducation.get(i).name.equals("本科")) {
                        selectOption = i;
                        break;
                    }
                }
            }

            pvOptionEducation = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    upDateUserInfo(optionsEducation.get(options1).id, UserConstants.EDU_CODE, 7);
                    userInfoOtherAdapter.updataPostition(optionsEducation.get(options1).getPickerViewText(), 0);
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

    private void initMaritalSta() {//条件选择器初始化
        if (pvOptionMarital == null) {
            showLoadingDialog();
            int selectOption = 0;
//            List<BasicBean> basicBeans = CacheManager.readListJson(mContext, "BasicBean", BasicBean.class);
            optionMarital = new ArrayList<>();
//            for (BasicBean basicBean : basicBeans) {
//                if (basicBean.type.equals("marital")) {
//                    optionMarital = basicBean.item;
//                    break;
//                }
//            }
            List<WhereCondition> whereConditions = new ArrayList<>();
            whereConditions.add(BasicBeanDao.Properties.Type.eq("marital"));
            List<BasicBean> basic = (List<BasicBean>) DaoSessionUtils.queryConditionAll(whereConditions);
            if (basic != null && basic.size() == 1) {
                optionMarital = basic.get(0).item;
            }
            for (int i = 0; i < optionMarital.size(); i++) {
                if (optionMarital.get(i).id.equals(usersInfoBean.base.marital_status)) {
                    selectOption = i;
                    break;
                }
            }
            final List<BasicBean.ItemBean> finalItems = optionMarital;
            pvOptionMarital = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    upDateUserInfo(finalItems.get(options1).id, UserConstants.MARITAL_STATUS, 3);
                    userInfoOtherAdapter.updataPostition(finalItems.get(options1).getPickerViewText(), 3);
                }
            }).setCancelText("取消")//取消按钮文字
                    .setSubmitText("确定")//确认按钮文字
                    .setTitleSize(14)//标题文字大小
                    .setTitleText("婚姻状况")//标题文字
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

    private void initChild() {//条件选择器初始化
        if (pvOptionChild == null) {
            showLoadingDialog();
            int selectOption = 0;
//            List<BasicBean> basicBeans = CacheManager.readListJson(mContext, "BasicBean", BasicBean.class);
            optionChild = new ArrayList<>();
//            for (BasicBean basicBean : basicBeans) {
//                if (basicBean.type.equals("child")) {
//                    optionChild = basicBean.item;
//                    break;
//                }
//            }
            List<WhereCondition> whereConditions = new ArrayList<>();
            whereConditions.add(BasicBeanDao.Properties.Type.eq("child"));
            List<BasicBean> basic = (List<BasicBean>) DaoSessionUtils.queryConditionAll(whereConditions);
            if (basic != null && basic.size() == 1) {
                optionChild = basic.get(0).item;
            }

            for (int i = 0; i < optionChild.size(); i++) {
                if (TextUtils.equals(optionChild.get(i).id, usersInfoBean.base.is_has_children)) {
                    selectOption = i;
                    break;
                }
            }
            final List<BasicBean.ItemBean> finalItems = optionChild;
            pvOptionChild = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    upDateUserInfo(finalItems.get(options1).id, UserConstants.CHILDREN_STATUS, 5);
                    userInfoOtherAdapter.updataPostition(finalItems.get(options1).getPickerViewText(), 5);
                }
            }).setCancelText("取消")//取消按钮文字
                    .setSubmitText("确定")//确认按钮文字
                    .setTitleSize(14)//标题文字大小
                    .setTitleText("子女状况")//标题文字
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


    private void initHeight() {
        if (pvOptionHeight == null) {
            int selectOption = 0;
//            List<BasicBean> basicBeans = CacheManager.readListJson(mContext, "BasicBean", BasicBean.class);
            optionHeight = new ArrayList<>();
            BasicBean.RangBean rangBean = null;
//            for (BasicBean basicBean : basicBeans) {
//                if (basicBean.type.equals("height")) {
//                    rangBean = basicBean.range;
//                    break;
//                }
//            }
            List<WhereCondition> whereConditions = new ArrayList<>();
            whereConditions.add(BasicBeanDao.Properties.Type.eq("height"));
            List<BasicBean> basic = (List<BasicBean>) DaoSessionUtils.queryConditionAll(whereConditions);
            if (basic != null && basic.size() == 1) {
                rangBean = basic.get(0).range;
            }
            int start = Integer.parseInt(rangBean.start);
            int end = Integer.parseInt(rangBean.end);
            for (int i = start; i <= end; i++) {
                optionHeight.add(new BasicBean.ItemBean(i + "", i + ""));
            }
            if (TextUtils.isEmpty(usersInfoBean.base.height) || TextUtils.equals(usersInfoBean.base.height, "0")) {
                for (int i = 0; i < optionHeight.size(); i++) {
                    if (optionHeight.get(i).name.equals("170")) {
                        selectOption = i;
                        break;
                    }
                }
            } else
                for (int i = 0; i < optionHeight.size(); i++) {
                    if (optionHeight.get(i).id.equals(usersInfoBean.base.height)) {
                        selectOption = i;
                        break;
                    }
                }
            final List<BasicBean.ItemBean> finalItems = optionHeight;
            pvOptionHeight = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    upDateUserInfo(finalItems.get(options1).id, UserConstants.HEIGHT, 1);
                    userInfoOtherAdapter.updataPostition(finalItems.get(options1).getPickerViewText() + "cm", 1);
                }
            }).setCancelText("取消")//取消按钮文字
                    .setSubmitText("确定")//确认按钮文字
                    .setTitleSize(14)//标题文字大小
                    .setTitleText("身高")//标题文字
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
            pvOptionHeight.setPicker(optionHeight);//二级选择器
            dismissLoadingDialog();
        }
        pvOptionHeight.show();
    }

    private void initDomicile() {
        if (pvOptionCity == null) {
            showLoadingDialog();
            int selectOption = 0;
            int secondOption = 0;
            String parentId = "";
//            List<BasicBean> basicBeans = CacheManager.readListJson(mContext, "BasicBean", BasicBean.class);
            optionsArea = new ArrayList<>();
//            for (BasicBean basicBean : basicBeans) {
//                if (basicBean.type.equals("region")) {
//                    optionsArea = basicBean.item;
//                    break;
//                }
//            }
            List<WhereCondition> whereConditions = new ArrayList<>();
            whereConditions.add(BasicBeanDao.Properties.Type.eq("region"));
            List<BasicBean> basic = (List<BasicBean>) DaoSessionUtils.queryConditionAll(whereConditions);
            if (basic != null && basic.size() == 1) {
                optionsArea = basic.get(0).item;
            }
            optionsCity = new ArrayList<>();
            for (BasicBean.ItemBean childrenBean : optionsArea) {
                optionsCity.add(childrenBean.children);
                for (int i = 0; i < childrenBean.children.size(); i++) {
                    if (TextUtils.equals(childrenBean.children.get(i).id, usersInfoBean.base.domicile_id)) {
                        secondOption = i;
                        parentId = childrenBean.children.get(i).parent_id;
                        break;
                    }
                }
            }
            for (int i = 0; i < optionsArea.size(); i++) {
                if (TextUtils.equals(parentId, optionsArea.get(i).id)) {
                    selectOption = i;
                    break;
                }
            }
            pvOptionCity = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    upDateUserInfo(optionsArea.get(options1).children.get(options2).id, UserConstants.DOMICILE_ID, 4);
                    tvDemocil.setText(optionsArea.get(options1).children.get(options2).getPickerViewText());
                }
            }).setCancelText("取消")//取消按钮文字
                    .setSubmitText("确定")//确认按钮文字
                    .setTitleSize(14)//标题文字大小
                    .setTitleText("居住地")//标题文字
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
            pvOptionCity.setPicker(optionsArea, optionsCity);//二级选择器
            dismissLoadingDialog();
        }
        pvOptionCity.show();
    }

    //行业
    private void initIndustry() {
        if (pvOptionIndustry == null) {
            showLoadingDialog();
            int selectOption = 0;
//            List<BasicBean> basicBeans = CacheManager.readListJson(mContext, "BasicBean", BasicBean.class);
            optionsIndustry = new ArrayList<>();
//            for (BasicBean basicBean : basicBeans) {
//                if (basicBean.type.equals("industry")) {
//                    optionsIndustry = basicBean.item;
//                    break;
//                }
//            }
            List<WhereCondition> whereConditions = new ArrayList<>();
            whereConditions.add(BasicBeanDao.Properties.Type.eq("industry"));
            List<BasicBean> basic = (List<BasicBean>) DaoSessionUtils.queryConditionAll(whereConditions);
            if (basic != null && basic.size() == 1) {
                optionsIndustry = basic.get(0).item;
            }
            for (int i = 0; i < optionsIndustry.size(); i++) {
                if (TextUtils.equals(usersInfoBean.base.industry_id, optionsIndustry.get(i).id)) {
                    selectOption = i;
                    break;
                }
            }
            pvOptionIndustry = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    upDateUserInfo(optionsIndustry.get(options1).id, UserConstants.INDUSTRY_ID, 6);
                    userInfoOtherAdapter.updataPostition(optionsIndustry.get(options1).getPickerViewText(), 6);
                }
            }).setCancelText("取消")//取消按钮文字
                    .setSubmitText("确定")//确认按钮文字
                    .setTitleSize(14)//标题文字大小
                    .setTitleText("从事行业")//标题文字
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
            pvOptionIndustry.setPicker(optionsIndustry);//二级选择器
            dismissLoadingDialog();
        }
        pvOptionIndustry.show();
    }

    //职业
    private void initOccupation() {
        if (pvOptionOccupation == null) {
            showLoadingDialog();
            int selectOption = 0;
//            List<BasicBean> basicBeans = CacheManager.readListJson(mContext, "BasicBean", BasicBean.class);
            optionsOccupation = new ArrayList<>();
//            for (BasicBean basicBean : basicBeans) {
//                if (basicBean.type.equals("occupation")) {
//                    optionsOccupation = basicBean.item;
//                    break;
//                }
//            }
            List<WhereCondition> whereConditions = new ArrayList<>();
            whereConditions.add(BasicBeanDao.Properties.Type.eq("occupation"));
            List<BasicBean> basic = (List<BasicBean>) DaoSessionUtils.queryConditionAll(whereConditions);
            if (basic != null && basic.size() == 1) {
                optionsOccupation = basic.get(0).item;
            }
            for (int i = 0; i < optionsOccupation.size(); i++) {
                if (TextUtils.equals(usersInfoBean.base.occupation_id, optionsOccupation.get(i).id)) {
                    selectOption = i;
                    break;
                }
            }
            pvOptionOccupation = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    upDateUserInfo(optionsOccupation.get(options1).id, UserConstants.OCC_ID, 7);
                    userInfoOtherAdapter.updataPostition(optionsOccupation.get(options1).getPickerViewText(), 7);
                }
            }).setCancelText("取消")//取消按钮文字
                    .setSubmitText("确定")//确认按钮文字
                    .setTitleSize(14)//标题文字大小
                    .setTitleText("从事职业")//标题文字
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
            pvOptionOccupation.setPicker(optionsOccupation);//二级选择器
            dismissLoadingDialog();
        }
        pvOptionOccupation.show();
    }

    //籍贯
    private void initBirth() {
        if (pvOptionsBirth == null) {
            showLoadingDialog();
            int selectOption = 0;
            int secondOption = 0;
            String parentId = "";
//            List<BasicBean> basicBeans = CacheManager.readListJson(mContext, "BasicBean", BasicBean.class);
            optionsBirthArea = new ArrayList<>();
//            for (BasicBean basicBean : basicBeans) {
//                if (basicBean.type.equals("region")) {
//                    optionsBirthArea = basicBean.item;
//                    break;
//                }
//            }
            List<WhereCondition> whereConditions = new ArrayList<>();
            whereConditions.add(BasicBeanDao.Properties.Type.eq("region"));
            List<BasicBean> basic = (List<BasicBean>) DaoSessionUtils.queryConditionAll(whereConditions);
            if (basic != null && basic.size() == 1) {
                optionsBirthArea = basic.get(0).item;
            }
            optionsBirthCity = new ArrayList<>();
            for (BasicBean.ItemBean childrenBean : optionsBirthArea) {
                optionsBirthCity.add(childrenBean.children);
                for (int i = 0; i < childrenBean.children.size(); i++) {
                    if (TextUtils.equals(childrenBean.children.get(i).id, usersInfoBean.base.birth_place_id)) {
                        secondOption = i;
                        parentId = childrenBean.children.get(i).parent_id;
                        break;
                    }
                }
            }
            for (int i = 0; i < optionsBirthArea.size(); i++) {
                if (TextUtils.equals(parentId, optionsBirthArea.get(i).id)) {
                    selectOption = i;
                    break;
                }
            }
            pvOptionsBirth = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    upDateUserInfo(optionsBirthArea.get(options1).children.get(options2).id, UserConstants.BIRTH_ID, 8);
                    userInfoOtherAdapter.updataPostition(optionsBirthArea.get(options1).children.get(options2).getPickerViewText(), 8);
                }
            }).setCancelText("取消")//取消按钮文字
                    .setSubmitText("确定")//确认按钮文字
                    .setTitleSize(14)//标题文字大小
                    .setTitleText("籍贯")//标题文字
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
            pvOptionsBirth.setPicker(optionsBirthArea, optionsBirthCity);//二级选择器
            dismissLoadingDialog();
        }
        pvOptionsBirth.show();
    }

    //星座
    private void initConstellation() {
        if (pvOptionConstellation == null) {
            showLoadingDialog();
            int selectOption = 0;
//            List<BasicBean> basicBeans = CacheManager.readListJson(mContext, "BasicBean", BasicBean.class);
            optionsConstellation = new ArrayList<>();
//            for (BasicBean basicBean : basicBeans) {
//                if (basicBean.type.equals("constellation")) {
//                    optionsConstellation = basicBean.item;
//                    break;
//                }
//            }
            List<WhereCondition> whereConditions = new ArrayList<>();
            whereConditions.add(BasicBeanDao.Properties.Type.eq("constellation"));
            List<BasicBean> basic = (List<BasicBean>) DaoSessionUtils.queryConditionAll(whereConditions);
            if (basic != null && basic.size() == 1) {
                optionsConstellation = basic.get(0).item;
            }
            for (int i = 0; i < optionsConstellation.size(); i++) {
                if (TextUtils.equals(usersInfoBean.base.constellation, optionsConstellation.get(i).id)) {
                    selectOption = i;
                    break;
                }
            }
            pvOptionConstellation = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    upDateUserInfo(optionsConstellation.get(options1).id, UserConstants.CONSTELLATION, 9);
                    userInfoOtherAdapter.updataPostition(optionsConstellation.get(options1).getPickerViewText(), 9);
                }
            }).setCancelText("取消")//取消按钮文字
                    .setSubmitText("确定")//确认按钮文字
                    .setTitleSize(14)//标题文字大小
                    .setTitleText("星座")//标题文字
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
            pvOptionConstellation.setPicker(optionsConstellation);//二级选择器
            dismissLoadingDialog();
        }
        pvOptionConstellation.show();
    }

    //体重
    private void initWeight() {
        if (pvOptionWeight == null) {
            int selectOption = 0;
//            List<BasicBean> basicBeans = CacheManager.readListJson(mContext, "BasicBean", BasicBean.class);
            optionsWeight = new ArrayList<>();
            BasicBean.RangBean rangBean = null;
//            for (BasicBean basicBean : basicBeans) {
//                if (basicBean.type.equals("weight")) {
//                    rangBean = basicBean.range;
//                    break;
//                }
//            }
            List<WhereCondition> whereConditions = new ArrayList<>();
            whereConditions.add(BasicBeanDao.Properties.Type.eq("weight"));
            List<BasicBean> basic = (List<BasicBean>) DaoSessionUtils.queryConditionAll(whereConditions);
            if (basic != null && basic.size() == 1) {
                rangBean = basic.get(0).range;
            }
            int start = Integer.parseInt(rangBean.start);
            int end = Integer.parseInt(rangBean.end);
            for (int i = start; i <= end; i++) {
                optionsWeight.add(new BasicBean.ItemBean(i + "", i + ""));
            }
            if (TextUtils.isEmpty(usersInfoBean.base.weight) || TextUtils.equals(usersInfoBean.base.weight, "0")) {
                for (int i = 0; i < optionsWeight.size(); i++) {
                    if (optionsWeight.get(i).name.equals("50")) {
                        selectOption = i;
                        break;
                    }
                }
            } else
                for (int i = 0; i < optionsWeight.size(); i++) {
                    if (optionsWeight.get(i).id.equals(usersInfoBean.base.weight)) {
                        selectOption = i;
                        break;
                    }
                }
            final List<BasicBean.ItemBean> finalItems = optionsWeight;
            pvOptionWeight = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    upDateUserInfo(finalItems.get(options1).id, UserConstants.WEIGHT, 4);
                    userInfoOtherAdapter.updataPostition(finalItems.get(options1).getPickerViewText() + "kg", 4);
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
                    .setSelectOptions(selectOption)
                    .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .build();
            pvOptionWeight.setPicker(optionsWeight);//二级选择器
            dismissLoadingDialog();
        }
        pvOptionWeight.show();
    }

    @OnClick({R.id.ll_nick_name, R.id.ll_avatar, R.id.ll_age,
            R.id.ll_sex, R.id.ll_democil})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_nick_name:
                Intent intent = new Intent(mContext, ModifyNickNameActivity.class);
                intent.putExtra("user_info", usersInfoBean);
                intent.putExtra("type", ModifyNickNameActivity.TYPE_NICKNAME);
                startActivityForResult(intent, ModifyNickNameActivity.TYPE_NICKNAME);
                break;
            case R.id.ll_avatar:
                Intent intentHead = new Intent(mContext, UserPhotoActivity.class);
                intentHead.putExtra("user_info", usersInfoBean);
                startActivityForResult(intentHead, UserPhotoActivity.TYPE_HEAD);
                break;
            case R.id.ll_age://年龄
                if (usersInfoBean.advance != null && usersInfoBean.advance.ora != null) {
                    if (TextUtils.equals(usersInfoBean.advance.ora.status, "pass")) {
                        AppContext.showToast("年龄认证后不可修改");
                    } else {
                        initTimePicker();
                    }
                } else {
                    initTimePicker();
                }
                break;
            case R.id.ll_sex://性别
                if (!TextUtils.isEmpty(tvSex.getText().toString())) {
                    AppContext.showToast("性别不可修改");
                } else {
                    initOptionSex();
                }
                break;
            case R.id.ll_democil:
                initDomicile();
                break;
        }
    }
}
