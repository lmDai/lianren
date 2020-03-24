package com.lianren.android.improve.user.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lianren.android.R;
import com.lianren.android.basicData.db.BasicBeanDao;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.BasicBean;
import com.lianren.android.improve.bean.ProfessionBean;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.user.adapter.ProfessionAdapter;
import com.lianren.android.util.CacheManager;
import com.lianren.android.util.greendao.DaoSessionUtils;
import com.lianren.android.widget.DividerItemDecoration;
import com.lianren.android.widget.SectionItemDecoration;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @package: com.lianren.android.improve.user.activities
 * @user:xhkj
 * @date:2019/12/20
 * @description:学校专业
 **/
public class ProfessionActivity extends BackActivity implements TextWatcher {
    public static final int TYPE_SCHOOL_Profession = 3000;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.et_school_name)
    EditText etSchoolName;
    @Bind(R.id.iv_login_username_del)
    ImageView ivLoginUsernameDel;
    private ProfessionAdapter sectionAdapter;
    private List<ProfessionBean> list;

    public static void show(Activity activity, UsersInfoBean info, int type) {
        Intent intent = new Intent(activity, ProfessionActivity.class);
        intent.putExtra("user_info", info);
        intent.putExtra("type", type);
        activity.startActivityForResult(intent, type);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_school_profession;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        etSchoolName.setHint("请输入专业名称");
    }

    @Override
    protected void initData() {
        super.initData();
        etSchoolName.addTextChangedListener(this);
        showLoadingDialog();
        List<WhereCondition> whereConditions = new ArrayList<>();
        whereConditions.add(BasicBeanDao.Properties.Type.eq("profession"));
        List<BasicBean> basic = (List<BasicBean>) DaoSessionUtils.queryConditionAll(whereConditions);
        List<BasicBean.ItemBean> professions = new ArrayList<>();
        if (basic != null && basic.size() == 1) {
            professions = basic.get(0).item;
        }
//        List<BasicBean> basicBeans = CacheManager.readListJson(this, "BasicBean", BasicBean.class);
//        for (BasicBean basicBean : basicBeans) {
//            if (basicBean.type.equals("profession")) {
//                professions = basicBean.item;
//                break;
//            }
//        }
        list = new ArrayList<>();
        for (BasicBean.ItemBean item : professions) {
            for (BasicBean.ItemBean.ChildrenBean childrenBean : item.children) {
                list.add(new ProfessionBean(item.name, childrenBean));
            }
        }
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.addItemDecoration(new SectionItemDecoration(this, list), 0);
        recyclerview.addItemDecoration(new DividerItemDecoration(this), 1);
        sectionAdapter = new ProfessionAdapter();
        sectionAdapter.bindToRecyclerView(recyclerview);
        sectionAdapter.setNewData(list);
        dismissLoadingDialog();

        sectionAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ProfessionBean mySection = sectionAdapter.getItem(position);
                if (!mySection.isHeader) {
                    Intent intent = new Intent();
                    intent.putExtra("user_school_profess", mySection.t);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    @OnClick(R.id.iv_login_username_del)
    public void onClick() {
        etSchoolName.setText("");
        sectionAdapter.setNewData(list);
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
            ((SectionItemDecoration) (recyclerview.getItemDecorationAt(0))).setData(list);
            sectionAdapter.setNewData(list);
        } else {
            List<ProfessionBean> search = new ArrayList<>();
            for (ProfessionBean itemBean : list) {
                if (itemBean.t.name.contains(keyword)) {
                    search.add(itemBean);
                }
            }
            ((SectionItemDecoration) (recyclerview.getItemDecorationAt(0))).setData(search);
            sectionAdapter.setNewData(search);
        }
        recyclerview.scrollToPosition(0);
    }
}
