package com.lianren.android.improve.user.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lianren.android.R;
import com.lianren.android.basicData.db.BasicBeanDao;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.BasicBean;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.user.adapter.SchoolAdapter;
import com.lianren.android.util.CacheManager;
import com.lianren.android.util.greendao.DaoSessionUtils;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @package: com.lianren.android.improve.user.activities
 * @user:xhkj
 * @date:2019/12/20
 * @description:印记列表
 **/
public class SchoolListActivity extends BackActivity implements TextWatcher {
    public static final int TYPE_SCHOOL = 2000;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.et_school_name)
    EditText etSchoolName;
    @Bind(R.id.iv_login_username_del)
    ImageView ivLoginUsernameDel;
    private SchoolAdapter mAdapter;
    private List<BasicBean.ItemBean> schools;

    public static void show(Activity activity, UsersInfoBean info, int type) {
        Intent intent = new Intent(activity, SchoolListActivity.class);
        intent.putExtra("user_info", info);
        intent.putExtra("type", type);
        activity.startActivityForResult(intent, type);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_school;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        mAdapter = new SchoolAdapter();
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.bindToRecyclerView(recyclerview);
    }

    @Override
    protected void initData() {
        super.initData();
//        List<BasicBean> basicBeans = CacheManager.readListJson(this, "BasicBean", BasicBean.class);
        schools = new ArrayList<>();
        List<WhereCondition> whereConditions = new ArrayList<>();
        whereConditions.add(BasicBeanDao.Properties.Type.eq("school"));
        List<BasicBean> basic = (List<BasicBean>) DaoSessionUtils.queryConditionAll(whereConditions);
        if (basic != null && basic.size() == 1) {
            schools = basic.get(0).item;
        }
//        for (BasicBean basicBean : basicBeans) {
//            if (basicBean.type.equals("school")) {
//                schools = basicBean.item;
//                break;
//            }
//        }
        mAdapter.setNewData(schools);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BasicBean.ItemBean item = mAdapter.getItem(position);
                Intent intent = new Intent();
                intent.putExtra("user_school", item);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        etSchoolName.addTextChangedListener(this);
    }

    @OnClick(R.id.iv_login_username_del)
    public void onClick() {
        etSchoolName.setText("");
        mAdapter.setNewData(schools);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        ivLoginUsernameDel.setVisibility(TextUtils.isEmpty(s.toString()) ? View.GONE : View.VISIBLE);
        String keyword = s.toString();
        if (TextUtils.isEmpty(keyword)) {
            mAdapter.setNewData(schools);
        } else {
            List<BasicBean.ItemBean> search = new ArrayList<>();
            for (BasicBean.ItemBean itemBean : schools) {
                if (itemBean.name.contains(keyword)) {
                    search.add(itemBean);
                }
            }
            mAdapter.setNewData(search);
        }
        recyclerview.scrollToPosition(0);
    }
}
