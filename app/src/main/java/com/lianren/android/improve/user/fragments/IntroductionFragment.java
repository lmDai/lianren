package com.lianren.android.improve.user.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lianren.android.R;
import com.lianren.android.improve.base.fragments.BaseFragment;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.explore.activities.PublishImprintActivity;
import com.lianren.android.improve.user.activities.ImprintsDetailActivity;
import com.lianren.android.improve.user.activities.ImprintsListActivity;
import com.lianren.android.improve.user.adapter.MyNoteAdapter;
import com.lianren.android.widget.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static com.lianren.android.improve.user.fragments.UserInfoFragment.ACTION_UP_DATE;

/**
 * @package: com.lianren.android.improve.user.fragments
 * @user:xhkj
 * @date:2019/12/19
 * @description:用户介绍
 **/
public class IntroductionFragment extends BaseFragment {
    @Bind(R.id.recycler_note)
    RecyclerView recyclerNote;
    @Bind(R.id.fmc_center_dynamic)
    LinearLayout fmcCenterDynamic;
    private MyNoteAdapter mAdapter;
    private UsersInfoBean usersInfoBean;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_introducation;
    }

    public static IntroductionFragment newInstance() {
        Bundle args = new Bundle();
        IntroductionFragment fragment = new IntroductionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    protected LocalBroadcastManager mManager;
    private BroadcastReceiver mReceiver;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerLocalReceiver();
    }

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
                        mAdapter.setNewData(usersInfoBean.note);
                    }
                }
            };
        mManager.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mManager != null)
            mManager.unregisterReceiver(mReceiver);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mAdapter = new MyNoteAdapter();
        recyclerNote.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.VERTICAL));
        recyclerNote.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerNote.setNestedScrollingEnabled(false);
        mAdapter.bindToRecyclerView(recyclerNote);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UsersInfoBean.NoteBean item = mAdapter.getItem(position);
                if (!TextUtils.isEmpty(item.content)) {
                    ImprintsDetailActivity.show(mContext, usersInfoBean.base.id, item.id, "");
                } else {
                    List<String> tag = new ArrayList<>();
                    tag.add(item.tag);
                    PublishImprintActivity.show(mContext, null, item.content, tag, 1, item.id);
                }
            }
        });
    }

    @OnClick({R.id.ll_scan_imprint})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_scan_imprint:
                ImprintsListActivity.show(mContext, usersInfoBean.base.id);
                break;

        }
    }
}
