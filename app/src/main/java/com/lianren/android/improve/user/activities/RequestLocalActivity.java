package com.lianren.android.improve.user.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.basicData.db.BasicBeanDao;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.BasicBean;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.util.CacheManager;
import com.lianren.android.util.greendao.DaoSessionUtils;

import org.greenrobot.greendao.query.WhereCondition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @package: com.lianren.android.improve.user.activities
 * @user:xhkj
 * @date:2019/12/25
 * @description:要求地址
 **/
public class RequestLocalActivity extends BackActivity {
    public static final int TYPE_REQUST_LOCAL = 4000;
    @Bind(R.id.tv_city1)
    TextView tvCity1;
    @Bind(R.id.iv_city1)
    ImageView ivCity1;
    @Bind(R.id.fl_city1)
    FrameLayout flCity1;
    @Bind(R.id.tv_city2)
    TextView tvCity2;
    @Bind(R.id.iv_city2)
    ImageView ivCity2;
    @Bind(R.id.fl_city2)
    FrameLayout flCity2;
    @Bind(R.id.tv_city3)
    TextView tvCity3;
    @Bind(R.id.iv_city3)
    ImageView ivCity3;
    @Bind(R.id.fl_city3)
    FrameLayout flCity3;
    @Bind(R.id.fl_add)
    FrameLayout flAdd;
    private int mType;
    private UsersInfoBean mUser;
    private OptionsPickerView pvOptionCity;
    private List<BasicBean.ItemBean> optionsArea = new ArrayList<>();
    private List<List<BasicBean.ItemBean.ChildrenBean>> optionsCity = new ArrayList<>();

    public static void show(Activity activity, UsersInfoBean info, int type) {
        Intent intent = new Intent(activity, RequestLocalActivity.class);
        intent.putExtra("user_info", info);
        intent.putExtra("type", type);
        activity.startActivityForResult(intent, type);
    }

    private List<UsersInfoBean.RequireBean.DomicileRangeBean> selectedItem = new ArrayList<>();

    @Override
    protected int getContentView() {
        return R.layout.activity_request_local;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
    }

    @Override
    protected void initData() {
        super.initData();
        mUser = (UsersInfoBean) getIntent().getSerializableExtra("user_info");
        mType = getIntent().getIntExtra("type", 0);
        if (mUser == null || mType == 0) {
            finish();
            return;
        }
        if (mUser.require.domicile_range != null) {
            selectedItem = mUser.require.domicile_range;
        }
        BindViewUi();
    }

    private void BindViewUi() {
        if (selectedItem != null) {
            if (selectedItem.size() == 0) {

                flCity1.setVisibility(View.GONE);
                flCity2.setVisibility(View.GONE);
                flCity3.setVisibility(View.GONE);
                //添加之后需要隐藏添加按钮
                flAdd.setVisibility(View.VISIBLE);
            } else if (selectedItem.size() == 1) {
                flCity1.setVisibility(View.VISIBLE);
                tvCity1.setText(selectedItem.get(0).value);
                flCity2.setVisibility(View.GONE);
                flCity3.setVisibility(View.GONE);
                //添加之后需要隐藏添加按钮
                flAdd.setVisibility(View.VISIBLE);
            } else if (selectedItem.size() == 2) {
                flCity1.setVisibility(View.VISIBLE);
                tvCity1.setText(selectedItem.get(0).value);
                flCity2.setVisibility(View.VISIBLE);
                tvCity2.setText(selectedItem.get(1).value);
                flCity3.setVisibility(View.GONE);
                //添加之后需要隐藏添加按钮
                flAdd.setVisibility(View.VISIBLE);
            } else if (selectedItem.size() == 3) {
                flCity1.setVisibility(View.VISIBLE);
                tvCity1.setText(selectedItem.get(0).value);
                flCity2.setVisibility(View.VISIBLE);
                tvCity2.setText(selectedItem.get(1).value);
                flCity3.setVisibility(View.VISIBLE);
                tvCity3.setText(selectedItem.get(2).value);
                flAdd.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_commit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_commit) {
            if (mType == TYPE_REQUST_LOCAL) {
                Intent intent = new Intent();
                intent.putExtra("ids", (Serializable) selectedItem);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        return false;
    }

    private void showItemAddress(String value, String key) {
        UsersInfoBean.RequireBean.DomicileRangeBean item = new UsersInfoBean.RequireBean.DomicileRangeBean(key, value);
        if (selectedItem.contains(new UsersInfoBean.RequireBean.DomicileRangeBean("-1", "不限"))) {
            AppContext.showToast("请勿重复添加");
            return;
        }
        if (TextUtils.equals(key, "-1")) {
            if (selectedItem != null && selectedItem.size() > 0) {
                AppContext.showToast("请勿重复添加");
                return;
            }
        }
        if (selectedItem != null) {
            if (selectedItem.contains(item)) {
                AppContext.showToast("请勿重复添加");
                return;
            } else {
                selectedItem.add(item);
            }
            BindViewUi();
        }
    }

    private void initDomicile() {
        if (pvOptionCity == null) {
            showLoadingDialog();
            int selectOption = 0;
            int secondOption = 0;
            optionsArea = new ArrayList<>();
            List<WhereCondition> whereConditions = new ArrayList<>();
            whereConditions.add(BasicBeanDao.Properties.Type.eq("region"));
            List<BasicBean> basic = (List<BasicBean>) DaoSessionUtils.queryConditionAll(whereConditions);
            if (basic != null && basic.size() == 1) {
                optionsArea = basic.get(0).item;
            }
            optionsCity = new ArrayList<>();
            for (BasicBean.ItemBean childrenBean : optionsArea) {
                optionsCity.add(childrenBean.children);
            }
            pvOptionCity = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    String value = optionsArea.get(options1).children.get(options2).name;
                    String key = optionsArea.get(options1).children.get(options2).id;
                    showItemAddress(value, key);
                }
            }).setCancelText("取消")//取消按钮文字
                    .setSubmitText("确定")//确认按钮文字
                    .setTitleSize(14)//标题文字大小
                    .setTitleText("居住地")//标题文字
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .setTitleColor(ContextCompat.getColor(this, R.color.tab_selected_color))//标题文字颜色
                    .setSubmitColor(ContextCompat.getColor(this, R.color.day_colorPrimary))//确定按钮文字颜色
                    .setCancelColor(ContextCompat.getColor(this, R.color.main_gray))//取消按钮文字颜色
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

    @OnClick({R.id.iv_city1, R.id.iv_city2, R.id.iv_city3, R.id.fl_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_city1:
                selectedItem.remove(0);
                BindViewUi();
                break;
            case R.id.iv_city2:
                selectedItem.remove(1);
                BindViewUi();
                break;
            case R.id.iv_city3:
                selectedItem.remove(2);
                BindViewUi();
                break;
            case R.id.fl_add:
                initDomicile();
                break;
        }
    }
}

